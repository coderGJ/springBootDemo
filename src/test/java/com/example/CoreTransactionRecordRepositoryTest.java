package com.example;

import com.example.domain.BaseShare;
import com.example.domain.CoreTransactionRecord;
import com.example.repository.BaseShareRepository;
import com.example.repository.CoreTransactionRecordRepository;
import com.example.util.HttpClientUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CoreTransactionRecordRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(CoreTransactionRecordRepositoryTest.class);

    private static final SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

    @Autowired
    CoreTransactionRecordRepository transactionRecordRepository;

    @Autowired
    BaseShareRepository shareRepository;

    @Test
    public void test() {
        long count = shareRepository.count();
        int size = 200;
        int total = (int)(count / 200) + 1;
        for (int i = 0; i <= total; i++) {
            PageRequest pageRequest = PageRequest.of(i, size);
            Page<BaseShare> page = shareRepository.findAll(pageRequest);
            if (page != null && !page.isEmpty()) {
                for (BaseShare share : page) {
                    resolvingAndSave(share.getCode());
                }
            }
        }

    }


    private void resolvingAndSave(String code) {
        log.debug("share code : {}", code);
        String result = HttpClientUtils.getShareInfo(code);
        Document doc = Jsoup.parse(result);

        Elements rows = doc.select("table[class=data]").get(1).select("tbody tr");
        Date maxRecordDate = transactionRecordRepository.maxRecordDateByStatus(code);

        if (rows.size() == 1) {
            log.info("================================没有结果=======================================");
        } else {
            log.info("================================查询到数据=======================================");
            log.info("================================记录条数 : {} ============================", rows.size() - 1);
            List<CoreTransactionRecord> list = new ArrayList<>();
            Date now = new Date();
            for (int i = 1; i < rows.size() - 1; i++) {
                Element row = rows.get(i);
                CoreTransactionRecord record = new CoreTransactionRecord();

                try {
                    Date recordDate = format.parse(row.select("td").get(0).select("a").get(0).text());
                    if (maxRecordDate != null && recordDate.compareTo(maxRecordDate) < 1) {
                        break;
                    }
                    record.setRecordDate(recordDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                record.setCode(code);
                if (StringUtils.isNotEmpty(row.select("td").get(1).text()) && !"/".equals(row.select("td").get(1).text())) {
                    record.setOpeningPrice(stringToInt(row.select("td").get(1).text()));
                }
                if (StringUtils.isNotEmpty(row.select("td").get(2).text()) && !"/".equals(row.select("td").get(2).text())) {
                    record.setHighest(stringToInt(row.select("td").get(2).text()));
                }
                if (StringUtils.isNotEmpty(row.select("td").get(3).text()) && !"/".equals(row.select("td").get(3).text())) {
                    record.setLowest(stringToInt(row.select("td").get(3).text()));
                }
                if (StringUtils.isNotEmpty(row.select("td").get(4).text()) && !"/".equals(row.select("td").get(4).text())) {
                    record.setClosingPrice(stringToInt(row.select("td").get(4).text()));
                }
                if (StringUtils.isNotEmpty(row.select("td").get(5).text()) && !"/".equals(row.select("td").get(5).text())) {
                    record.setTransactionVolume(NumberUtils.toLong(row.select("td").get(5).text().replaceAll(",", "")));
                }
                if (StringUtils.isNotEmpty(row.select("td").get(6).text()) && !"/".equals(row.select("td").get(6).text())) {
                    record.setTransactionAmount(NumberUtils.toLong(row.select("td").get(6).text().replaceAll(",", "")));
                }
                if (StringUtils.isNotEmpty(row.select("td").get(7).text()) && !"/".equals(row.select("td").get(7).text())) {
                    record.setFluctuationAmount(stringToInt(row.select("td").get(7).text()));
                } else {
                    record.setFluctuationAmount(0);
                }
                if (StringUtils.isNotEmpty(row.select("td").get(8).select("span").get(0).text()) && !"/".equals(row.select("td").get(8).select("span").get(0).text())) {
                    record.setFluctuationPercentage(stringToInt(row.select("td").get(8).select("span").get(0).text().replaceAll("%", "").trim()));
                } else {
                    record.setFluctuationPercentage(0);
                }

                if (StringUtils.isNotEmpty(row.select("td").get(10).select("span").get(0).text()) && !"/".equals(row.select("td").get(10).text())) {
                    record.setMaxBalance(stringToInt(row.select("td").get(10).select("span").get(0).text().replaceAll("%", "").trim()));
                }
                record.setGmtCreate(now);
                record.setGmtModified(now);
                list.add(record);

                if (list.size() == 200) {
                    transactionRecordRepository.saveAll(list);
                    list = new ArrayList<>();
                }
            }

            if (list.size() > 0) {
                transactionRecordRepository.saveAll(list);
            }
        }
    }

    private int stringToInt(String s){
        if(StringUtils.isBlank(s)||"/".equals(s)){
            return 0;
        }
        Double d = NumberUtils.toDouble(s.replaceAll(",", ""));
        if (d == null) {
            return 0;
        }
        d = d * 100;
        return d.intValue();
    }

    @Test
    public void thread() {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(4);
        long count = shareRepository.count();
        int size = 200;
        int total = (int)(count / 200) + 1;

        for (int i = 0; i <= total; i++) {
            final PageRequest pageRequest = new PageRequest(i, size);
            final BaseShareRepository finalShareRepository = shareRepository;
            final CoreTransactionRecordRepository finalTransactionRecordRepository = transactionRecordRepository;
            final int temp = i;
            fixedThreadPool.execute(() -> {
                log.debug("thread {}", temp);
                Page<BaseShare> page = finalShareRepository.findAll(pageRequest);
                if (page != null && !page.isEmpty()) {
                    for (BaseShare share : page) {
                        threadResolvingAndSave(share.getCode(), finalTransactionRecordRepository);
                    }
                }


            });
        }

        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private void threadResolvingAndSave(final String code, final CoreTransactionRecordRepository transactionRecordRepository) {
        log.debug("share code : {}", code);
        //final SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        String result = HttpClientUtils.getShareInfo(code);
        Document doc = Jsoup.parse(result);

        Elements rows = doc.select("table[class=data]").get(1).select("tbody tr");
        Date maxRecordDate = transactionRecordRepository.maxRecordDateByStatus(code);

        if (rows.size() == 1) {
            log.info("================================没有结果=======================================");
        } else {
            log.info("================================查询到数据=======================================");
            log.info("================================记录条数 : {} ============================", rows.size() - 1);
            List<CoreTransactionRecord> list = new ArrayList<>();
            Date now = new Date();
            for (int i = 1; i < rows.size() - 1; i++) {
                Element row = rows.get(i);
                CoreTransactionRecord record = new CoreTransactionRecord();

                try {
                    String date = row.select("td").get(0).select("a").get(0).text();
                    log.info("=============row first td date :{}===============", date);
                    Date recordDate = format.parse(date);
                    if (maxRecordDate != null && recordDate.compareTo(maxRecordDate) < 1) {
                        break;
                    }
                    record.setRecordDate(recordDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                record.setCode(code);
                if (StringUtils.isNotEmpty(row.select("td").get(1).text()) && !"/".equals(row.select("td").get(1).text())) {
                    record.setOpeningPrice(stringToInt(row.select("td").get(1).text()));
                }
                if (StringUtils.isNotEmpty(row.select("td").get(2).text()) && !"/".equals(row.select("td").get(2).text())) {
                    record.setHighest(stringToInt(row.select("td").get(2).text()));
                }
                if (StringUtils.isNotEmpty(row.select("td").get(3).text()) && !"/".equals(row.select("td").get(3).text())) {
                    record.setLowest(stringToInt(row.select("td").get(3).text()));
                }
                if (StringUtils.isNotEmpty(row.select("td").get(4).text()) && !"/".equals(row.select("td").get(4).text())) {
                    record.setClosingPrice(stringToInt(row.select("td").get(4).text()));
                }
                if (StringUtils.isNotEmpty(row.select("td").get(5).text()) && !"/".equals(row.select("td").get(5).text())) {
                    record.setTransactionVolume(NumberUtils.toLong(row.select("td").get(5).text().replaceAll(",", "")));
                }
                if (StringUtils.isNotEmpty(row.select("td").get(6).text()) && !"/".equals(row.select("td").get(6).text())) {
                    record.setTransactionAmount(NumberUtils.toLong(row.select("td").get(6).text().replaceAll(",", "")));
                }
                if (StringUtils.isNotEmpty(row.select("td").get(7).text()) && !"/".equals(row.select("td").get(7).text())) {
                    record.setFluctuationAmount(stringToInt(row.select("td").get(7).text()));
                } else {
                    record.setFluctuationAmount(0);
                }
                if (StringUtils.isNotEmpty(row.select("td").get(8).select("span").get(0).text()) && !"/".equals(row.select("td").get(8).select("span").get(0).text())) {
                    record.setFluctuationPercentage(stringToInt(row.select("td").get(8).select("span").get(0).text().replaceAll("%", "").trim()));
                } else {
                    record.setFluctuationPercentage(0);
                }

                if (StringUtils.isNotEmpty(row.select("td").get(10).select("span").get(0).text()) && !"/".equals(row.select("td").get(10).text())) {
                    record.setMaxBalance(stringToInt(row.select("td").get(10).select("span").get(0).text().replaceAll("%", "").trim()));
                }
                record.setGmtCreate(now);
                record.setGmtModified(now);
                list.add(record);

                if (list.size() == 200) {
                    transactionRecordRepository.saveAll(list);
                    list = new ArrayList<>();
                }
            }

            if (list.size() > 0) {
                transactionRecordRepository.saveAll(list);
            }
        }
    }
}

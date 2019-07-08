package com.example.util;

import com.example.domain.CoreTransactionRecord;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class JsoupUtils {
    
    public static List<CoreTransactionRecord> parseHtml(String htmlContent) throws ParseException {
        //String html = htmltest;
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        List<CoreTransactionRecord> srList = new ArrayList<>();
        Document doc = Jsoup.parse(htmlContent);
        Elements rows = doc.select("table[class=data]").get(1).select("tbody tr");
        //System.out.println(rows.text());
        if (rows.size() == 1) {
                System.out.println("没有结果");
        }else {
                System.out.println("--------------------------- 查询结果 行数： "+rows.size()+"---------------------------");
             for (int i = 1; i < rows.size()-1; i++) {
                    Element row = rows.get(i);
                    CoreTransactionRecord sr = new CoreTransactionRecord();
                    sr.setRecordDate(format.parse(row.select("td").get(0).select("a").get(0).text()));
                    if(!StringUtils.isBlank(row.select("td").get(1).text())&&!"/".equals(row.select("td").get(1).text())){
                        sr.setOpeningPrice(stringToInt(row.select("td").get(1).text()));
                    }
                    if(!StringUtils.isBlank(row.select("td").get(2).text())&&!"/".equals(row.select("td").get(2).text())){
                        sr.setHighest(stringToInt(row.select("td").get(2).text()));
                    }
                    if(!StringUtils.isBlank(row.select("td").get(3).text())&&!"/".equals(row.select("td").get(3).text())){
                        sr.setLowest(stringToInt(row.select("td").get(3).text()));
                    }
                    if(!StringUtils.isBlank(row.select("td").get(4).text())&&!"/".equals(row.select("td").get(4).text())){
                        sr.setClosingPrice(stringToInt(row.select("td").get(4).text()));
                    }
                    if(!StringUtils.isBlank(row.select("td").get(5).text())&&!"/".equals(row.select("td").get(5).text())){
                        sr.setTransactionVolume(Long.valueOf(row.select("td").get(5).text().replaceAll(",", "")));
                    }
                    if(!StringUtils.isBlank(row.select("td").get(6).text())&&!"/".equals(row.select("td").get(6).text())){
                        sr.setTransactionAmount(Long.valueOf(row.select("td").get(6).text().replaceAll(",", "")));
                    }
                    if(!StringUtils.isBlank(row.select("td").get(7).text())&&!"/".equals(row.select("td").get(7).text())){
                        sr.setFluctuationAmount(stringToInt(row.select("td").get(7).text()));
                    }
                    if(!StringUtils.isBlank(row.select("td").get(8).select("span").get(0).text())&&!"/".equals(row.select("td").get(8).select("span").get(0).text())){
                        sr.setFluctuationPercentage(stringToInt(row.select("td").get(8).select("span").get(0).text().replaceAll("%", "").trim()));
                    }

                    if(!StringUtils.isBlank(row.select("td").get(10).select("span").get(0).text())&&!"/".equals(row.select("td").get(10).text())){
                        sr.setMaxBalance(stringToInt(row.select("td").get(10).select("span").get(0).text().replaceAll("%", "").trim()));
                    }
                    srList.add(sr);
                }
                /*for (int i = 1; i < rows.size()-1; i++) {
                    Element row = rows.get(i);
                    System.out.println("------------------------------------------------------");
                    System.out.println("日期:" + row.select("td").get(0).select("a").get(0).text());
                    System.out.println("开盘:" + row.select("td").get(1).text());
                    System.out.println("最高:" + row.select("td").get(2).text());
                    System.out.println("最低:" + row.select("td").get(3).text());
                    System.out.println("收盘:" + row.select("td").get(4).text());
                    System.out.println("成交量:" + row.select("td").get(5).text());
                    System.out.println("成交金额:" + row.select("td").get(6).text());
                    System.out.println("升跌$:" + row.select("td").get(7).text());
                    System.out.println("升跌%:" + row.select("td").get(8).select("span").get(0).text());
                    System.out.println("缩:" + row.select("td").get(9).text());
                    System.out.println("高低差%：" + row.select("td").get(10).select("span").get(0).text());
                }*/
                System.out.println("-----------------------------------------------------------------");
        }
        return srList;
    }
    
    private static int stringToInt(String s){
        if(StringUtils.isBlank(s)||"/".equals(s)){
            return 0;
        }
        Double d = Double.valueOf(s); 
        d = d*100;
        return d.intValue();
    }

    private static long stringToLong(String s){
        if(!StringUtils.isBlank(s)){
            return 0;
        }
        Double d = Double.valueOf(s); 
        d = d*100;
        return d.longValue();
    }
}

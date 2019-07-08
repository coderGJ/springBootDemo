package com.example;

import com.example.domain.BaseShare;
import com.example.repository.BaseShareRepository;
import org.apache.commons.lang3.math.NumberUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Dom4JTest {

    private static final Logger log = LoggerFactory.getLogger(Dom4JTest.class);

    @Autowired
    BaseShareRepository shareRepository;

    @Test
    public void include() {

        String path = "E:\\xml\\share.xml";
        Document doc = null;
        try {
            SAXReader reader = new SAXReader();
            doc = reader.read(new File(path));
        } catch (DocumentException e) {
            log.error("doXMLParse", e);
        }

        if (doc != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date now = new Date();

            Element root = doc.getRootElement();
            Element worksheet = root.element("Worksheet");

            if (worksheet != null) {
                Element table = worksheet.element("Table");
                List<Element> elements = table.elements("Row");
                boolean isHead = true;

                List<BaseShare> shareList = new ArrayList<>();

                for (Element row : elements) {
                    if (isHead) {
                        isHead = false;
                        continue;
                    }

                    List<Element> cells = row.elements();
                    String code = cells.get(0).element("Data").getText().trim();
                    BaseShare share = shareRepository.getByCode(code);
                    if (share != null && share.getId() != null) {
                        continue;
                    }
                    share = new BaseShare();
                    share.setCode(cells.get(0).element("Data").getText().trim());


                    share.setName(cells.get(1).element("Data").getText().trim());
                    try {
                        share.setPublicDate(sdf.parse(cells.get(4).element("Data").getText().trim()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    share.setTotalStock((long)(NumberUtils.toDouble(cells.get(5).element("Data").getText().trim()) * 10000));
                    share.setFloatingStock((long)(NumberUtils.toDouble(cells.get(6).element("Data").getText().trim()) * 10000));
                    share.setGmtCreate(now);
                    share.setGmtModified(now);
                    log.debug(share.toString());
                    shareList.add(share);

                }

                shareRepository.saveAll(shareList);
            }

        }
    }

}

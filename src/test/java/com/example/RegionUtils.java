package com.example;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RegionUtils {

    private static final String INDEX_URL = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2017/";
    private static final String CHARSET_NAME_GB2312 = "GB2312";
    private static final String CHARSET_NAME_GBK = "GBK";
    private static final String[] CLASS_NAMES = {".citytr", ".countytr", ".towntr", ".villagetr"};

    @Test
    void test() throws IOException, InterruptedException {
        InputStream inputStream = new URL(INDEX_URL).openStream();
        Document doc = Jsoup.parse(inputStream, CHARSET_NAME_GB2312, INDEX_URL);
        inputStream.close();
        Elements provinceElements = doc.select(".provincetr");

        List<Map> privinceList = new LinkedList<>();
        Map privinceMap;
        for (Element provinceElement : provinceElements) {
            Elements privinceLinks = provinceElement.select("a");
            for (Element privinceLink : privinceLinks) {
                privinceMap = new LinkedHashMap();
                privinceMap.put("name", privinceLink.text());

                List<Map> childList;
                while (true) {
                    // 递归获取 Child
                    getChild(privinceMap, INDEX_URL + privinceLink.attr("href"), 0);
                    childList = (List<Map>) privinceMap.get("child");
                    // 莫名其妙，不知道为什么会出现 childList 为空的情况。
                    if (CollectionUtils.isNotEmpty(childList)) {
                        break;
                    }
                    System.out.println("childList 为空");
                }

                String code = childList.get(0).get("code").toString();
                privinceMap.put("code", code.substring(0, 2) + "0000000000");
                privinceList.add(privinceMap);
            }
        }

        // 递归保存
        save(privinceList, "0", 0);
    }

    /**
     * 递归保存
     *
     * @param list
     * @param parentCode
     * @param level
     * @throws InterruptedException
     */
    private void save(List<Map> list, String parentCode, int level) throws InterruptedException {
        /*if (level == CLASS_NAMES.length + 1) {
            return;
        }
        level += 1;

        if (list != null) {
            for (Map map : list) {
                AdministrativeDivision administrativeDivision = dozerBeanMapper.map(map, AdministrativeDivision.class);
                administrativeDivision.setParentCode(parentCode);
                administrativeDivision.setLevel( level);
                while (true) {
                    try {
                        administrativeDivisionService.insert(administrativeDivision);
                    } catch (Exception e) {
                        if ("connection holder is null".equals(e.getMessage())) {
                            TimeUnit.MINUTES.sleep(1);
                            continue;
                        }
                        break;
                    }
                    break;
                }

                save((List<Map>) map.get("child"), administrativeDivision.getCode(), level);
            }
        }*/
    }

    /**
     * 获取 child
     *
     * @param map
     * @param url
     * @param level
     */
    private void getChild(Map map, String url, int level) {
        if (level == CLASS_NAMES.length) {
            return;
        }
        System.out.println(url);

        Document doc;
        while (true) {
            try {
//                OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(1, TimeUnit.SECONDS).readTimeout(3, TimeUnit.SECONDS).build();
                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                Response response = okHttpClient.newCall(request).execute();
                if (!response.isSuccessful()) {
                    continue;
                }
                byte[] bodyBytes = response.body().bytes();
                String bodyText = new String(bodyBytes, CHARSET_NAME_GB2312);
                if (bodyText.contains("�")) {
                    bodyText = new String(bodyBytes, CHARSET_NAME_GBK);
                }
                doc = Jsoup.parse(bodyText);
                break;
            } catch (IOException e) {
                // e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }

        List<Map> childList = new LinkedList<>();
        Elements Elements = doc.select(CLASS_NAMES[level]);
        level += 1;
        Map childMap;
        for (Element element : Elements) {
            Elements links = element.select("td a");
            // 市辖区
            boolean isContinue = true;
            if (links.size() == 0) {
                links = element.select("td");
                isContinue = false;
            }

            Element codeLink = links.first();
            childMap = new LinkedHashMap();
            childMap.put("code", codeLink.text());
            childMap.put("name", links.last().text());

            if (isContinue) {
                getChild(childMap, url.substring(0, url.lastIndexOf("/") + 1) + codeLink.attr("href"), level);
            }

            childList.add(childMap);
        }
        map.put("level", level-1);
        map.put("child", childList);
    }
}
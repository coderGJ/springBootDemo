package com.example;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.domain.BaseDistrict;
import com.example.domain.BaseShare;
import com.example.domain.CoreTransactionRecord;
import com.example.repository.BaseDistrictRepository;
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
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BaseDistrictRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(BaseDistrictRepositoryTest.class);

    @Autowired
    BaseDistrictRepository districtRepository;

    @Test
    public void test() {
        File file = null;
        try {
            file = ResourceUtils.getFile("classpath:district.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (file != null) {
            String str = txt2String(file);
            JSONObject json = JSON.parseObject(str);
            int status = (int) json.get("status");
            if (status == 0) {
                JSONArray array = (JSONArray) json.get("result");
                JSONArray provinceArray = (JSONArray) array.get(0);
                JSONArray cityArray = (JSONArray) array.get(1);
                JSONArray districtArray = (JSONArray) array.get(2);

                Date now = new Date();
                for (int i = 0, size = provinceArray.size(); i < size; i++) {
                    JSONObject provinceObj = (JSONObject) provinceArray.get(i);
                    BaseDistrict province = new BaseDistrict();
                    province.setCode((String)provinceObj.get("id"));
                    province.setName((String)provinceObj.get("name"));
                    province.setFullname((String)provinceObj.get("fullname"));
                    JSONArray pinyinArray = (JSONArray) provinceObj.get("pinyin");
                    province.setPinyin(StringUtils.join(pinyinArray, ","));
                    JSONObject location = (JSONObject) provinceObj.get("location");
                    province.setLng(((BigDecimal)location.get("lng")).doubleValue());
                    province.setLat(((BigDecimal)location.get("lat")).doubleValue());
                    province.setLevel(1);
                    province.setParentId(0);
                    province.setGmtCreate(now);
                    JSONArray child = (JSONArray)provinceObj.get("cidx");
                    province = districtRepository.save(province);

                    int j = (int)child.get(0);
                    int childSize = (int) child.get(1);
                    for (; j <= childSize; j++) {

                        JSONObject cityObj = (JSONObject) cityArray.get(j);
                        BaseDistrict city = new BaseDistrict();
                        city.setCode((String)cityObj.get("id"));
                        city.setName((String)cityObj.get("name"));
                        city.setFullname((String)cityObj.get("fullname"));

                        pinyinArray = (JSONArray) cityObj.get("pinyin");
                        city.setPinyin(StringUtils.join(pinyinArray, ","));

                        location = (JSONObject) cityObj.get("location");
                        city.setLng(((BigDecimal)location.get("lng")).doubleValue());
                        city.setLat(((BigDecimal)location.get("lat")).doubleValue());
                        city.setParentId(province.getId());
                        city.setGmtCreate(now);
                        if (cityObj.get("cidx") == null) {
                            city.setLevel(3);
                            districtRepository.save(city);
                        } else {
                            child = (JSONArray)cityObj.get("cidx");
                            city.setLevel(2);
                            city = districtRepository.save(city);
                            List<BaseDistrict> districtList = new ArrayList<>();
                            int k = (int)child.get(0);
                            int districtSize = (int) child.get(1);
                            for (; k <= districtSize; k++) {
                                JSONObject districtObj = (JSONObject) districtArray.get(k);
                                BaseDistrict district = new BaseDistrict();
                                district.setCode((String)districtObj.get("id"));
                                district.setName((String)districtObj.get("name"));
                                district.setFullname((String)districtObj.get("fullname"));
                                pinyinArray = (JSONArray) districtObj.get("pinyin");
                                district.setPinyin(StringUtils.join(pinyinArray, ","));
                                location = (JSONObject) districtObj.get("location");
                                district.setLng(((BigDecimal)location.get("lng")).doubleValue());
                                district.setLat(((BigDecimal)location.get("lat")).doubleValue());
                                district.setLevel(3);
                                district.setParentId(city.getId());
                                district.setGmtCreate(now);
                                districtList.add(district);
                            }
                            districtRepository.saveAll(districtList);
                        }
                    }
                }
            }
        }
    }

    public static String txt2String(File file) {
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s;
            while ((s = br.readLine()) != null) {//使用readLine方法，一次读一行
                result.append(System.lineSeparator()).append(s);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}

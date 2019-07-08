package com.example;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DateTest {

    @Test
    public void testCom() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        LocalDate localDate = LocalDate.now();
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        Date now = Date.from(instant);

        try {
            Date date = sdf.parse("2019-05-18");
            System.out.println(now.compareTo(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}

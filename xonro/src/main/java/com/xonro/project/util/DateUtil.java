package com.xonro.project.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author GPY
 * @date 2021/4/8
 * @des 时间工具类
 */
public class DateUtil {

    public static final String FORMAT1 = "yyyy-MM-dd";
    public static final String FORMAT2 = "yyyy-MM-dd HH:mm:ss";

    public static String formatDate(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT1);
        return sdf.format(date);
    }

    /**
     * 获取某一年第几周的星期几
     * @param year
     * @param weekly
     * @param day
     * @return
     * @throws ParseException
     */
    public static Date getYearWeeklyDay(int year, int weekly,int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year); // 2016年
        cal.set(Calendar.WEEK_OF_YEAR, weekly); // 设置为2016年的第10周
        cal.set(Calendar.DAY_OF_WEEK, day); // 1表示周日，2表示周一，7表示周六
        Date date = cal.getTime();
        return date;
    }

}

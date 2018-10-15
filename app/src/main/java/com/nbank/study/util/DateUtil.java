package com.nbank.study.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间工具类
 *
 * @文件名 DateUtil.java
 * @作者 ico
 * @创建日期 2014-11-14
 * @版本 V 1.0
 */
public final class DateUtil {
    public static String[] weeks = new String[]{"一", "二", "三", "四", "五", "六", "日"};

    /**
     * 根据传入的格式，返回字符串格式的当前时间
     *
     * @param format {@link ico.ico.util.DateUtil.Format}
     * @return String
     */
    public static String getCurrentTime(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date());
    }

    /**
     * 获取当前时间的毫秒类型
     *
     * @return Long
     */
    public static Long getCurrentTimes() {
        Timestamp time = new Timestamp(new Date().getTime());
        return time.getTime();
    }

    /**
     * 根据指定的字符串和时间格式进行转化,获取对应的毫秒值
     *
     * @param time
     * @param format
     * @return
     * @throws ParseException
     */
    public static long getLong(String time, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.parse(time).getTime();
    }


    /**
     * 将时间的毫秒数，根据传入的格式，转换为字符串格式
     *
     * @param milliseconds
     * @param format       {@link ico.ico.util.DateUtil.Format}
     * @return String
     */
    public static String fromTimestamp(long milliseconds, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(milliseconds);
    }

    /**
     * 将字符串根据指定的格式转换为Date
     *
     * @param string
     * @param format {@link ico.ico.util.DateUtil.Format}
     * @return String
     */
    public static Date toDate(String string, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.parse(string);
    }


    /**
     * 获取Timestamp类型的当前时间
     *
     * @return Timestamp
     */
    public static Timestamp getCurrentTime() {
        return new Timestamp(new Date().getTime());
    }

    /**
     * 将Timestamp类型中的毫秒提取并返回
     *
     * @param time
     * @return int
     */
    public static int getMsel(Timestamp time) {
        SimpleDateFormat sdf = new SimpleDateFormat("SSS");
        String msel = sdf.format(new Date(time.getTime()));
        return Integer.valueOf(msel);
    }

    /**
     * 将传入的Timestamp时间和当前时间进行比较，根据时差返回字符串数据
     * 本周外返回yyyy-MM-dd
     * 本周内返回星期weeks
     * 相差一天的返回昨天
     * 当天显示HH:mm
     *
     * @param time
     * @return String
     */
    public static String getHumanize(Timestamp time) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdf3 = new SimpleDateFormat("HH:mm");
        int time1 = Integer.valueOf(sdf2.format(time.getTime()));
        int nowTime = Integer.valueOf(getCurrentTime("yyyyMMdd"));
        if (nowTime - time1 == 0) {
            return sdf3.format(time.getTime());
        } else if (nowTime - time1 == 1) {
            return "昨天";
        } else if (nowTime - time1 > 1
                && nowTime - time1 < getCurrentTime().getDay()) {
            return "星期" + weeks[nowTime - time1 - 1];
        } else {
            return sdf1.format(time.getTime());
        }
    }

    //格式化字符串
    public static class Format {
        /**
         * 从年一直到毫秒
         */
        public final static String YEAR2MSEL = "yyyy-MM-dd HH:mm:ss:SSS";
        /**
         * 从年到秒
         */
        public final static String YEAR2SEC = "yyyy-MM-dd HH:mm:ss";

        /**
         * 从年到日
         */
        public final static String YEAR2DAY = "yyyy-MM-dd";
        public final static String YEAR2DAY_1 = "yyyyMMdd";
        public final static String YEAR2DAY_2 = "yyyy.MM.dd";
        /**
         * 从年到月
         */
        public final static String YEAR2MONTH = "yyyy-MM";
        /**
         * 从分钟到秒
         */
        public final static String MIN2SS = "mm:ss";
        /**
         * 从年到分钟
         */
        public final static String YEAR2MIN = "yyyy-MM-dd HH:mm";
        /**
         * 从年到分钟
         */
        public final static String YEAR2MIN_S = "yyyyMMdd HH:mm";

        /**
         * 从小时到分钟
         */
        public final static String HOUR2MIN = "HH:mm";

        /**
         * 从小时到秒
         */
        public final static String HOUR2SEC = "HH:mm:ss";

        /**
         * 从小时到毫秒
         */
        public final static String HOUR2MSEL = "HH:mm:ss:SSS";

        /**
         * 文件常用的
         */
        public final static String FILE = "yyyyMMdd_HHmmssSSS";
    }

}

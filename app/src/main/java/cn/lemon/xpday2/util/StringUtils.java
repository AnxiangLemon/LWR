package cn.lemon.xpday2.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtils {

    private StringUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 是否不为空
     *
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !"".equals(str.trim());
    }

    /**
     * 是否为空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return str == null || "".equals(str.trim());
    }

    /**
     * 将小数转换成百分数
     *
     * @param decimal
     * @return
     */
    public static String doubleToPercentage(double decimal) {
        DecimalFormat df = new DecimalFormat("0.00%");
        return df.format(decimal);
    }

    /**
     * 将double型的金钱数转换成两位小数的String型
     *
     * @param money
     * @return
     */
    public static String doubleToString(double money) {
        DecimalFormat df = new DecimalFormat("######0.00");
        return df.format(money);
    }

    /**
     * 将每三个数字加上逗号处理（通常使用金额方面的编辑）
     *
     * @param str
     *            无逗号的数字
     *
     * @return 加上逗号的数字
     */
    public static String addComma(String str) {

        String beforeStr = "";
        String afterStr = "";

        if (isEmpty(str)) {
            return "0";
        }

        if (str.indexOf(".") != -1) {// 判断传过来的字符串是否含有小数点‘.’,即数字是否为小数
            // 截取小数点以及之后的字符串
            afterStr = str.substring(str.indexOf("."), str.length());
            // 截取小数点之前的字符串
            beforeStr = str.substring(0, str.indexOf("."));
        } else {
            beforeStr = str;
        }

        // 将传进数字反转
        String reverseStr = new StringBuilder(beforeStr).reverse().toString();

        String strTemp = "";

        for (int i = 0; i < reverseStr.length(); i++) {
            if (i * 3 + 3 > reverseStr.length()) {
                strTemp += reverseStr.substring(i * 3, reverseStr.length());
                break;
            }

            strTemp += reverseStr.substring(i * 3, i * 3 + 3) + ",";
        }

        // 将[789,456,] 中最后一个[,]去除
        if (strTemp.endsWith(",")) {
            strTemp = strTemp.substring(0, strTemp.length() - 1);
        }

        // 将数字重新反转
        String resultStr = new StringBuilder(strTemp).reverse().toString();
        return resultStr + afterStr;
    }

    /** 获得小时和分钟
     * @param date: 2016-10-16 20:00:00
     * @return: 20:00
     */
    public static String getTimeByDate(String date){
        String hourMinSec=date.split(" ")[1];
        String[] times=hourMinSec.split(":");
        return times[0]+":"+times[1];
    }
    /**
     * 分钟数转小时：分钟
     *
     * @param min
     * @return
     */
    public static String minutesToHour(int min){

        int hour = min / 60;
        int _min = min % 60;

        return hour + "小时" + _min + "分钟";
    }

    /** 获得几号
     * @param strDate:2016-10-16
     * @return: 16
     */
    public static String getDayByDate(String strDate){
        String[] times = strDate.split("-");
        return times[2];
    }

    /** 获得小时
     * @param date:2016-10-16 20:00:00
     * @return: 20
     */
    public static String getTimeHourByDate(String date){
        String hourMinSec=date.split(" ")[1];
        String[] times=hourMinSec.split(":");
        return times[0];
    }

    /**
     * @param currentTime:1484450460000
     * @return: 2016-10-16 20:00:00
     */
    public static String getDateByLongTime(long currentTime, String formatType)
            throws ParseException {
        Date date = longToDate(currentTime, formatType); // long类型转成Date类型
        String strTime = dateToString(date, formatType); // date类型转成String
        return strTime;
    }

    /** 将long类型的时间转换成 formatType格式的返回
     *
     * @param currentTime:1484450460000
     *  @param formatType:例如"yyyy-MM-dd HH:mm:ss"
     * @return: 2016-10-16 20:00:00
     */
    public static Date longToDate(long currentTime, String formatType)
            throws ParseException {
        Date dateOld = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
        String sDateTime = dateToString(dateOld, formatType); // 把date类型的时间转换为string
        Date date = stringToDate(sDateTime, formatType); // 把String类型转换为Date类型
        return date;
    }
    public static String dateToString(Date data, String formatType) {
        return new SimpleDateFormat(formatType).format(data);
    }

    /**
     * @param dateStr:2016-10-16 20:00:00
     * @return: 20
     */
    public static long getLongTimeByDate(String dateStr, String formatType) throws ParseException {
        Date date = stringToDate(dateStr, formatType); // String类型转成date类型
        if (date == null) {
            return 0;
        } else {
            long currentTime = dateToLong(date); // date类型转成long类型
            return currentTime;
        }
    }

    /** 将String 类型的时间转换成Date格式
     */
    public static Date stringToDate(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }
    /**
     * @param date:2016-10-16 20:00:00
     * @return: 1488739827
     */
    public static long dateToLong(Date date) {
        return date.getTime();
    }
}
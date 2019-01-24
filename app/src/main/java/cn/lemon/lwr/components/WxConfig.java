package cn.lemon.lwr.components;

/**
 * Created by AnxiangLemon on 2019/1/25.
 * //用来存放微信hook的类方法之类 的
 */

public class WxConfig {
    //微信数据库包名称
    final public static String DATABASE_PACKAGE_NAME = "com.tencent.wcdb.database.SQLiteDatabase";
    //微信数据库包hook的方法名
    final public static String DATABASE_HOOKFUN = "insertWithOnConflict";
    //发送文本消息的Send类
    final public static String SEND_TEXT_MSG = "";

}

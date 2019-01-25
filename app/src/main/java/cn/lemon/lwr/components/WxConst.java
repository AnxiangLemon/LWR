package cn.lemon.lwr.components;

/**
 * Created by AnxiangLemon on 2019/1/25.
 * //用来存放微信hook的类方法之类 的
 * 常量 7.0.0
 */
public class WxConst {
    
    //微信数据库包名称
    final public static String DATABASE_PACKAGE_NAME = "com.tencent.wcdb.database.SQLiteDatabase";
    //微信数据库包hook的方法名
    final public static String DATABASE_HOOKFUN = "insertWithOnConflict";

    //发送文本消息获取静态类的类（只有获取了静态类才能在外面调用）
    final public static String SEND_TEXT_MSG_STATICCLASS = "com.tencent.mm.kernel.g";
    //发送文本消息获取静态类的类的方法
    final public static String SEND_TEXT_MSG_STATICCLASS_FUN = "LZ";
    //发送文本消息的Send类
    final public static String SEND_TEXT_MSG_CLASS = "com.tencent.mm.ah.p";
    //发送文本消息的构造参数类
    final public static String SEND_TEXT_MSG_OBJECT_ = "com.tencent.mm.modelmulti.h";

    //该类是发送消息 所传递的抽象类
    final public static String SEND_MSG_ADSTRACT_PARAM_CLASS = "com.tencent.mm.ah.m";

    //发送local图片消息的Send类
    final public static String SEND_IMG_MSG_CLASS  = "com.tencent.mm.as.n";
    //执行静态方法获取类  获取属性 即为对象实例
    final public static String SEND_IMG_MSG_CLASS_STATICFUN ="abu";

    //发送local图片消息的代理UI类com.tencent.mm.ui.chatting.SendImgProxyUI
    final public static String SEND_IMG_MSG_PROXYUI_CLASS  = "com.tencent.mm.ui.chatting.SendImgProxyUI";
    //代理UI的函数
    final public static String SEND_IMG_MSG_PROXYUI_CLASS_STATICFUN  = "aFl";

    //对图片发送的接收方进行统计 （）
    final public static String SEND_IMG_MSG_CLASS_FUN  =  "nH";
    //上传图片的com.tencent.mm.as.i类
    final public static String UPLOAD_IMG_CLASS  = "com.tencent.mm.as.i";


}

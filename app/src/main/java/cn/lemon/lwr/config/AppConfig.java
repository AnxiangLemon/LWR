package cn.lemon.lwr.config;

/*存放一些常量数据*/
public class AppConfig {
    //Xposed包
    public static final String PACKAGE_NAME_XPOSED = "de.robv.android.xposed.installer";
    //LWR客户端包名称  hook是否模块是否激活
    final public static String WECHATGENIUS_PACKAGE_NAME = "cn.lemon.lwr";
    //微信主进程名
    final public static String WECHAT_PROCESS_NAME = "com.tencent.mm";
    //微信包
    public static final String PACKAGE_NAME_WECHAT = "com.tencent.mm";
    //微信支持的版本
    public static final String[]   WECHAT_VER= {"7.0.0"};
}



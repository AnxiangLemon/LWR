package cn.lemon.lwr;

import android.content.Context;
import android.content.ContextWrapper;

import cn.lemon.lwr.components.WxHook;
import cn.lemon.lwr.components.data.WxDataBaseHandler;
import cn.lemon.lwr.components.wallet.WxGatheringQRCode;
import cn.lemon.lwr.config.AppConfig;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedBridge.log;

public class MyXposedInit implements IXposedHookLoadPackage {
   //防止多次加载
    private static  boolean LoadWxHook = false;

    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        //region hook模块是否激活
        if (lpparam.packageName.equals(AppConfig.WECHATGENIUS_PACKAGE_NAME)) {
            //hook客户端APP的是否激活返回值。替换为true。
            Class<?> classAppUtils = XposedHelpers.findClassIfExists(AppConfig.WECHATGENIUS_PACKAGE_NAME + ".util.AppUtils", lpparam.classLoader);
            if (classAppUtils != null) {
                XposedHelpers.findAndHookMethod(classAppUtils,
                        "isModuleActive",
                        XC_MethodReplacement.returnConstant(true));
                log("LWR：激活状态。");
            }
            return;
        }
        //不是微信进程直接退出
        if (lpparam.packageName.equals(AppConfig.WECHAT_PROCESS_NAME)) {
            try {
                XposedHelpers.findAndHookMethod(ContextWrapper.class, "attachBaseContext", Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                        //        super.afterHookedMethod(param);
                        String processName = lpparam.processName;
                        //仅仅只hook微信的进程
                        if (!processName.equals(AppConfig.WECHAT_PROCESS_NAME)) {
                            return;
                        }
                        //保证 只加载一次  不然hook多次就会多次调用
                        if(!LoadWxHook){
                            //  log("是不是这里加载多次 所以....");
                            LoadWxHook = true;
                            WxHook.wxClassLoader = lpparam.classLoader;
                            //加载hook的函数
                            loadWxPlugins();
                        }
                    }
                });
            } catch (Error | Exception e) {
                log("初始化异常：" + e.getMessage());
            }
        }
    }

    /**
     * 微信防检测  貌似并没有什么卵用啊~~ 管他呢 先留着
     * */
    private void hookWxCheckXp() {
        Class<?> tclass = XposedHelpers.findClassIfExists("com.tencent.mm.app.t", WxHook.wxClassLoader);
        if (tclass == null) return;
        XposedHelpers.findAndHookMethod(tclass, "a", StackTraceElement[].class, new XC_MethodHook() {
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(false);  // 设置返回值
                log("WX防封检测，设置返回值为false！");
            }
        });
    }

    /**
     * 只有需要监听消息的函数即可
     * 比如主动调用接口函数这些 当你调用的时候会自动加载
     * 但是如果是被动的接口 你就需要去添加监听的函数  不然只加载类会找到不方法调用~
     */
    private void loadWxPlugins(){
        //数据库
        WxDataBaseHandler.hookDatabaseInsert();
        //获取收款二维码链接 这个不知道用的多不多
        WxGatheringQRCode.hookPayUrl();
        hookWxCheckXp();
    }


    //接收消息
    //
//    private void hookChatHandlerMsg() {
//        Class<?> b$1class = XposedHelpers.findClassIfExists("com.tencent.mm.booter.notification.b$1", xpClassLoader);
//        if (b$1class == null) {
//            return;
//        } else {
//            XposedHelpers.findAndHookMethod(b$1class,
//                    "handleMessage", Message.class, new XC_MethodHook() {
//                        @Override
//                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                            Message paramAnonymousMessage = (Message) param.args[0];
//                            //微信id
//                            String talker = paramAnonymousMessage.getData().getString("notification.show.talker");
//                            //消息内容
//                            String content = paramAnonymousMessage.getData().getString("notification.show.message.content");
//                            //消息类型
//                            int j = paramAnonymousMessage.getData().getInt("notification.show.message.type");
//                            //flag
//                            int i = paramAnonymousMessage.getData().getInt("notification.show.tipsflag");
//                            //表示接收了消息
//                            XposedBridge.log(talker + "--" + content + "--" + j + "--" + i);
//
//                        }
//
//                    });
//        }
//    }


}

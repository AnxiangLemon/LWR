package cn.lemon.lwr.components.wallet;

import org.json.JSONObject;

import java.lang.reflect.Method;

import cn.lemon.lwr.components.WxHook;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import static de.robv.android.xposed.XposedBridge.log;

/**
 * 微信收款二维码
 * 一个发送  一个接受
 */
public class WxGatheringQRCode {

    /**
     * 发送二维码信息
     * 跟发送消息其实是一个函数 传参数不同  后面看怎么优化
     *
     * @param account 金额
     * @param note    备注
     */
    public static void sendQrCodeMsg(double account, String note) {
        Class<?> classG = XposedHelpers.findClassIfExists("com.tencent.mm.kernel.g", WxHook.wxClassLoader);
        Object objectG = XposedHelpers.callStaticMethod(classG, "LZ");

        Class<?> sclass = XposedHelpers.findClassIfExists("com.tencent.mm.plugin.collect.b.s", WxHook.wxClassLoader);

        //构造生成二维码的参数
        Object hp = XposedHelpers.newInstance(sclass, new Class[]{double.class, String.class, String.class}, account, "0", note);
        Object[] hpobj = new Object[]{hp, 0};

        //获取微信发消息类
        Class<?> pClass = XposedHelpers.findClass("com.tencent.mm.ah.p", WxHook.wxClassLoader);
        //获取参数类 这貌似是个抽象类
        Class<?> mClass = XposedHelpers.findClass("com.tencent.mm.ah.m", WxHook.wxClassLoader);

        Method methodA = XposedHelpers.findMethodExact(pClass, "a", mClass, int.class);
        try {
            XposedBridge.invokeOriginalMethod(methodA, objectG, hpobj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //获取二维码信息
    public static void hookPayUrl() {
        Class<?> sclass = XposedHelpers.findClassIfExists("com.tencent.mm.plugin.collect.b.s", WxHook.wxClassLoader);
        if (sclass == null) {
            return;
        } else {

//  构造函数
//   XposedHelpers.findAndHookConstructor(sclass, double.class, String.class, String.class, new XC_MethodHook() {
//                @Override
//                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                    double fee = (double) param.args[0];
//                    String fee_type = (String) param.args[1];
//                    String desc = (String) param.args[2];
//                    log(fee + "--" + fee_type + "--" + desc);
//                }
//            });

            XposedHelpers.findAndHookMethod(sclass, "a", int.class, String.class, JSONObject.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    int i1 = (int) param.args[0];
                    String s1 = (java.lang.String) param.args[1];
                    JSONObject jsonObject = (JSONObject) param.args[2];
                    //微信二维码链接
                    String pay_url = jsonObject.getString("pay_url");
                    log(pay_url+"=="+i1 + "--" + s1 + "--" + jsonObject.toString());
                }
            });

        }
    }
}

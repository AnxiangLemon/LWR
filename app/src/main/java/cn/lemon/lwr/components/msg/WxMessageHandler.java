package cn.lemon.lwr.components.msg;

import java.lang.reflect.Method;
import java.util.ArrayList;

import cn.lemon.lwr.components.WxConst;
import cn.lemon.lwr.components.WxHook;
import de.robv.android.xposed.XposedHelpers;

import static de.robv.android.xposed.XposedBridge.log;

/*
 * 发送各种消息
 * 接收消息目前都在数据库那边处理
 * */
public class WxMessageHandler {

    /**
     * 发送文本消息
     *
     * @param talker  接收方wxid
     * @param content 消息内容
     */
    public static void sendTextMsg(String talker, String content) {
        /*
         * 这个是获取发送消息的静态类对象
         * 首先要找到在哪里生成这静态类（不知道怎么找）
         * 然后实例化这个静态类 这样调用方法就不会每次打开微信才发送了
         * */
        Class<?> classG = XposedHelpers.findClassIfExists(WxConst.SEND_TEXT_MSG_STATICCLASS, WxHook.wxClassLoader);
        Object objectG = XposedHelpers.callStaticMethod(classG, WxConst.SEND_TEXT_MSG_STATICCLASS_FUN);
        //    Object objectdpP = XposedHelpers.getObjectField(objectG, "eYH");

        //这是参数方法
        Class<?> hClass = XposedHelpers.findClass(WxConst.SEND_TEXT_MSG_OBJECT_, WxHook.wxClassLoader);
        //构造消息参数
        Object hp = XposedHelpers.newInstance(hClass, new Class[]{String.class, String.class, int.class, int.class, Object.class}, talker, content, 1, 1, null);
        //   Object[] hpobj = new Object[]{hp, 0};
        //获取微信发消息类
        Class<?> pClass = XposedHelpers.findClass(WxConst.SEND_TEXT_MSG_CLASS, WxHook.wxClassLoader);

        //获取参数类 这貌似是个抽象类
        Class<?> mClass = XposedHelpers.findClass(WxConst.SEND_MSG_ADSTRACT_PARAM_CLASS, WxHook.wxClassLoader);

        Method methodA = XposedHelpers.findMethodExact(pClass, "a", mClass, int.class);
        //  XposedBridge.log(pClass+"--"+mClass+"--"+hClass+"--"+methodA+"--"+objectdpP);
        try {
            //发送消息  只要这里传的静态的实例属性  两种方法都可以行
            //        XposedBridge.log(pClass+"---"+objectG+"--test--"+test);
            //     XposedBridge.invokeOriginalMethod(methodA, objectG, hpobj);
            XposedHelpers.callMethod(objectG, "a", hp, 0);
        } catch (Exception e) {
            e.printStackTrace();
            log("发送文本信息异常：" + e.getMessage());
        }
    }


    /**
     * 发送本地图片
     * @param picList   图片数组  不超出9~
     * @param isCompress   true是压缩  false发送原图  默然true
     * @param talker  发送给谁
     */
    private void sendLocalImgMsg(ArrayList<String> picList, boolean isCompress, String talker) {
        //微信发送图片的类
        Class<?> sclass = XposedHelpers.findClass(WxConst.SEND_IMG_MSG_CLASS, WxHook.wxClassLoader);
        //发送图片代理类
        Class<?> uiclass = XposedHelpers.findClass(WxConst.SEND_IMG_MSG_PROXYUI_CLASS, WxHook.wxClassLoader);
        //上传图片的类
        Class<?> iclass = XposedHelpers.findClassIfExists(WxConst.UPLOAD_IMG_CLASS, WxHook.wxClassLoader);


        if (sclass == null || uiclass == null|| iclass==null) {
            return;
        }
        //构造微信图片数组
        //      ArrayList<String> picList = new ArrayList<>();
        //       picList.add("/storage/emulated/0/launcher/ad/f807c5c11a42152c3d474409efedb98b.png");

        //设置属性为true  好像是防止会有重复的实例
        //  XposedHelpers.setStaticBooleanField(uiclass, "isRunning", true);

        //read读取函数执行   貌似是检查图片合法性
//        ArrayList<String> picList2 = new ArrayList<>();
//        ArrayList<String> readlist = (ArrayList) XposedHelpers.callStaticMethod(uiclass, "a", picList, 26214400, picList2, true);

        //执行静态方法获取类  获取属性 即为对象实例
        Object objectabu = XposedHelpers.callStaticMethod(sclass, WxConst.SEND_IMG_MSG_CLASS_STATICFUN);


        // Class<?> classG = XposedHelpers.findClassIfExists("com.tencent.mm.kernel.g", WxHook.wxClassLoader);
        //Object objectMK = XposedHelpers.callStaticMethod(classG, "MK");
        //     XposedHelpers.callMethod(objectMK, "dbI");

        try {
            if (picList.size() > 0) {
                //执行发送方法  true是压缩  false发送原图  默然true
                XposedHelpers.callMethod(objectabu, "a", picList, isCompress, 0, 0, talker, 2130838195);
                //    XposedBridge.invokeOriginalMethod(methodA, objectG, obj);
                //然后执行另一个方法 应该是校验的
                //    XposedHelpers.callMethod(objectMK, "dbJ");
                //这个是加入队列的
                XposedHelpers.callStaticMethod(uiclass, WxConst.SEND_IMG_MSG_PROXYUI_CLASS_STATICFUN);

                //获取发送的集合 上传需要
                ArrayList<Integer> nhlist = (ArrayList<Integer>) XposedHelpers.callMethod(objectabu, WxConst.SEND_IMG_MSG_CLASS_FUN, talker);
                //   XposedBridge.log("nhlist" + nhlist.toString());

                /// [50]--wxid_4b9a1yqz3s0322--wxid_psn4vfy4kfr822--[/storage/emulated/0/launcher/ad/1244800853378a628796e134a886c043.png]--0--true--2130838195
                Object iobj = iclass.newInstance();
                XposedHelpers.callMethod(iobj, "a", nhlist, "wxid_4b9a1yqz3s0322", talker, picList, 0, isCompress, 2130838195);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log("发送图片信息异常：" + e.getMessage());
        }
    }

}

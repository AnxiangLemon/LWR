package cn.lemon.xpday2;

import android.content.ContentValues;

import org.json.JSONObject;

import java.lang.reflect.Method;

import cn.lemon.xpday2.thread.Tuling123;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MyXposedInit implements IXposedHookLoadPackage {
    //微信数据库包名称
    private static final String WECHAT_DATABASE_PACKAGE_NAME = "com.tencent.wcdb.database.SQLiteDatabase";
    //聊天精灵客户端包名称
    private static final String WECHATGENIUS_PACKAGE_NAME = "cn.lemon.xpday2";
    //微信主进程名
    private static final String WECHAT_PROCESS_NAME = "com.tencent.mm";

    //类加载器
    private static ClassLoader xpClassLoader  = null;

    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //region hook模块是否激活
        if (lpparam.packageName.equals(WECHATGENIUS_PACKAGE_NAME)) {
            //hook客户端APP的是否激活返回值。替换为true。
            Class<?> classAppUtils = XposedHelpers.findClassIfExists(WECHATGENIUS_PACKAGE_NAME + ".util.AppUtils", lpparam.classLoader);
            if (classAppUtils != null) {
                XposedHelpers.findAndHookMethod(classAppUtils,
                        "isModuleActive",
                        XC_MethodReplacement.returnConstant(true));
                XposedBridge.log("成功hook住模块.AppUtils的isModuleActive方法");
            }
            return;
        }

        //不是微信进程直接退出
        if (!lpparam.processName.equals(WECHAT_PROCESS_NAME)) {
            return;
        }

        if (xpClassLoader == null) {
            xpClassLoader = lpparam.classLoader;
        }
        XposedBridge.log("进入微信进程：" + lpparam.processName);
        //调用 hook数据库插入。
       hookDatabaseInsert(lpparam);
        hookChatErCode(lpparam);

    }


    public static void sendQrCodeMsg() {
        /*
         * 这个是获取发送消息的静态类对象
         * 首先要找到在哪里生成这静态类（不知道怎么找）
         * 然后实例化这个静态类 这样调用方法就不会每次打开微信才发送了
         * */
        Class<?> classG = XposedHelpers.findClassIfExists("com.tencent.mm.kernel.g", xpClassLoader);
        Object objectG = XposedHelpers.callStaticMethod(classG, "LZ");
        Object objectdpP = XposedHelpers.getObjectField(objectG, "eYH");

        Class<?> sclass = XposedHelpers.findClassIfExists("com.tencent.mm.plugin.collect.b.s", xpClassLoader);

        Object hp = XposedHelpers.newInstance(sclass, new Class[]{ double.class, String.class, String.class},11,"0","跟发送消息一个fun");
        Object[] hpobj = new Object[]{hp, 0};

        //获取微信发消息类
        Class<?> pClass = XposedHelpers.findClass("com.tencent.mm.ah.p", xpClassLoader);
        //获取参数类 这貌似是个抽象类
        Class<?> mClass = XposedHelpers.findClass("com.tencent.mm.ah.m", xpClassLoader);

        Method methodA = XposedHelpers.findMethodExact(pClass, "a", mClass, int.class);
        //  XposedBridge.log(pClass+"--"+mClass+"--"+hClass+"--"+methodA+"--"+objectdpP);
        try {
            //发送消息
            XposedBridge.invokeOriginalMethod(methodA, objectdpP, hpobj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


        private void hookChatErCode(final  XC_LoadPackage.LoadPackageParam lpparam) {
        Class<?> sclass = XposedHelpers.findClassIfExists("com.tencent.mm.plugin.collect.b.s", lpparam.classLoader);
        if (sclass == null) {
            return;
        } else {

            //(double paramDouble, String paramString1, String paramString2)
          XposedHelpers.findAndHookConstructor(sclass, double.class, String.class, String.class, new XC_MethodHook() {
              @Override
              protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                  double fee = (double) param.args[0];
                  String fee_type = (String) param.args[1];
                  String desc = (String) param.args[2];
                  XposedBridge.log(fee+"--"+fee_type+"--"+desc);
              }
          });

          XposedHelpers.findAndHookMethod(sclass,"a",int.class,String.class,JSONObject.class,new XC_MethodHook() {
              @Override
              protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                 int i1 = (int) param.args[0];
                 String s1 = (java.lang.String) param.args[1];
                 JSONObject jsonObject = (JSONObject) param.args[2];


                 XposedBridge.log(i1+"--" +
                         s1+"--"+
                         jsonObject.toString()
                         );
                  //微信二维码链接
                  String pay_url =  jsonObject.getString("pay_url");

              }
          });

        }
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
//                            String takler = paramAnonymousMessage.getData().getString("notification.show.talker");
//                            //消息内容
//                            String content = paramAnonymousMessage.getData().getString("notification.show.message.content");
//                            //消息类型
//                            int j = paramAnonymousMessage.getData().getInt("notification.show.message.type");
//                            //flag
//                            int i = paramAnonymousMessage.getData().getInt("notification.show.tipsflag");
//                            //表示接收了消息
//                            XposedBridge.log(takler + "--" + content + "--" + j + "--" + i);
//                            //  sendTextMsg(takler, content);
//                        }
//
//                    });
//        }
//    }



    //hook数据库插入操作
    private void hookDatabaseInsert(final  XC_LoadPackage.LoadPackageParam lpparam) {
        Class<?> classDb = XposedHelpers.findClassIfExists(WECHAT_DATABASE_PACKAGE_NAME, lpparam.classLoader);
        if (classDb == null) {
            return;
        } else {
            XposedHelpers.findAndHookMethod(classDb,
                    "insertWithOnConflict",
                    String.class, String.class, ContentValues.class, int.class,
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            String tableName = (String) param.args[0];
                            ContentValues contentValues = (ContentValues) param.args[2];
                            if (tableName == null || tableName.length() == 0 || contentValues == null) {
                                return;
                            }
                            //过滤掉非聊天消息
                            if (!tableName.equals("message")) {
                                return;
                            }
                            //打印出日志
                            printInsertLog(tableName, (String) param.args[1], contentValues, (Integer) param.args[3]);

                            //判断消息类型进行回复
                            msgTypeInfo(contentValues);

                        }
                    });
        }


    }


    //输出数据库日志
    private void printInsertLog(String tableName, String nullColumnHack, ContentValues contentValues, int conflictValue) {
        String[] arrayConflicValues =
                {"", " OR ROLLBACK ", " OR ABORT ", " OR FAIL ", " OR IGNORE ", " OR REPLACE "};
        if (conflictValue < 0 || conflictValue > 5) {
            return;
        }
        XposedBridge.log("Hook数据库insert。table：" + tableName
                + "；nullColumnHack：" + nullColumnHack
                + "；CONFLICT_VALUES：" + arrayConflicValues[conflictValue]
                + "；contentValues:" + contentValues);

    }

    //消息类型
    private void msgTypeInfo(ContentValues contentValues) {
        //发送者id
        String talker = contentValues.getAsString("talker");
        //创建时间
        String createTime = contentValues.getAsString("createTime");
        //内容
        String content = contentValues.getAsString("content");
        //是否发送
        Integer isSend = contentValues.getAsInteger("isSend");
        //消息类型
        Integer type = contentValues.getAsInteger("type");
        //消息id
        Integer msgId = contentValues.getAsInteger("msgId");
        //状态 目前好友发的是3
        Integer status = contentValues.getAsInteger("status");
        //
        Integer bizChatId = contentValues.getAsInteger("bizChatId");
        //
        Integer talkerId = contentValues.getAsInteger("talkerId");
        //以下别人发消息来 bizClientMsgId
        /*
        自己发送
        * status=1 msgId=24 createTime=1547714690289 talker=wxid_psn4vfy4kfr822 content=12345 isSend=1
        * type=1 bizChatId=-1 talkerId=70
        * 对方
        * bizClientMsgId= msgId=25 msgSvrId=4358952459174946710 talker=wxid_psn4vfy4kfr822 content=12345
        *         flag=0 status=3 msgSeq=673657600
        *         createTime=1547715601000 lvbuffer=[B@19bc15d0 isSend=0 type=1 bizChatId=-1 talkerId=70
        * */


        //目前用这个状态表示好友发来的消息
        if (status == 3) {

            if (talker.endsWith("@chatroom")) {
                //群消息

            } else if (talker.startsWith("gh_")) {
                //公众号消息

            } else {
                sendQrCodeMsg();
            //    Tuling123 tuling123 = new Tuling123(talker,content);
           //     tuling123.run();
                //好友消息
              //  sendTextMsg(talker, content);
            }


        }
    }




    /*
     * 发送文本消息
     * 消息id  消息内容
     * */
    public static void sendTextMsg(String takler, String content) {
        /*
         * 这个是获取发送消息的静态类对象
         * 首先要找到在哪里生成这静态类（不知道怎么找）
         * 然后实例化这个静态类 这样调用方法就不会每次打开微信才发送了
         * */
        Class<?> classG = XposedHelpers.findClassIfExists("com.tencent.mm.kernel.g", xpClassLoader);
        Object objectG = XposedHelpers.callStaticMethod(classG, "LZ");
        Object objectdpP = XposedHelpers.getObjectField(objectG, "eYH");

        //这是参数方法
        Class<?> hClass = XposedHelpers.findClass("com.tencent.mm.modelmulti.h", xpClassLoader);
        //构造消息参数
        Object hp = XposedHelpers.newInstance(hClass, new Class[]{String.class, String.class, int.class, int.class, Object.class}, takler, content, 1, 1, null);
        Object[] hpobj = new Object[]{hp, 0};

        //获取微信发消息类
        Class<?> pClass = XposedHelpers.findClass("com.tencent.mm.ah.p", xpClassLoader);
        //获取参数类 这貌似是个抽象类
        Class<?> mClass = XposedHelpers.findClass("com.tencent.mm.ah.m", xpClassLoader);

        Method methodA = XposedHelpers.findMethodExact(pClass, "a", mClass, int.class);
        //  XposedBridge.log(pClass+"--"+mClass+"--"+hClass+"--"+methodA+"--"+objectdpP);
        try {
            //发送消息
            XposedBridge.invokeOriginalMethod(methodA, objectdpP, hpobj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

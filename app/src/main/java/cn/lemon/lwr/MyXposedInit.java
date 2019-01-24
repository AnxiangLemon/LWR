package cn.lemon.lwr;

import android.content.ContentValues;
import android.util.Log;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.lemon.lwr.components.WxConfig;
import cn.lemon.lwr.util.ClassUtil;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MyXposedInit implements IXposedHookLoadPackage {

    //聊天精灵客户端包名称
    private static final String WECHATGENIUS_PACKAGE_NAME = "cn.lemon.lwr";
    //微信主进程名
    private static final String WECHAT_PROCESS_NAME = "com.tencent.mm";

    //类加载器
    private static ClassLoader xpClassLoader = null;

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

        if (xpClassLoader == null) {  //不清楚为何赋值后 有时候有些方法会执行两次
            xpClassLoader = lpparam.classLoader;
        }
        XposedBridge.log("进入微信进程：" + lpparam.processName);
        hookWxCheckXp();
        //调用 hook数据库插入。
        hookDatabaseInsert(lpparam);
        listenerimg(lpparam);
        listenerimg2();
       listenerimg3();
        //  hookChatQrCode(lpparam);

    }


    private void listenerimg3() {
        Class<?> sclass = XposedHelpers.findClassIfExists("com.tencent.mm.ak.c", xpClassLoader);
        if (sclass == null) return;

        //此方法应该是（逆向还不是靠猜）返回文件上传路径
        XposedHelpers.findAndHookMethod(sclass, "a", String.class, long.class, String.class, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                String p1 = (String) param.args[0];
                long p2 = (long) param.args[1];
                String p3 = (String) param.args[2];
                String p4 = (String) param.args[3];
                XposedBridge.log("ak.c.a()--" + p1 + "--" + p2 + "--" + p3 + "--" + p4 + "--");

                Log.d("堆栈", "测试过会============== + param.getResult()");
                new Exception().printStackTrace(); // 直接干脆


            }
        });

        Class<?> pidclass = XposedHelpers.findClassIfExists("com.tencent.mm.as.l", xpClassLoader);
        if (pidclass == null) return;
        Class<?> leclass = XposedHelpers.findClassIfExists("com.tencent.mm.network.q", xpClassLoader);
        //欲问青天这人生有几何  浊酒一杯
        XposedHelpers.findAndHookMethod(pidclass, "a", int.class, int.class, int.class,
                String.class, leclass, byte[].class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        int p1 = (int) param.args[0];
                        int p2 = (int) param.args[1];
                        int p3 = (int) param.args[2];
                        String p4 = (String) param.args[3];
                        Object p5 = param.args[1];
                        byte[] p6 = (byte[]) param.args[1];
                        XposedBridge.log("余生不悲不喜as.l.a()--" + p1 + "--" + p2 + "--" + p3 + "--" + p4 + "--" + ClassUtil.getKeyAndValue(p5) + "--" + p6.length);
                    }
                });


        Class<?> abclass = XposedHelpers.findClassIfExists("com.tencent.mm.as.l", xpClassLoader);
        if (abclass == null) return;
        Class<?> eclass = XposedHelpers.findClass("com.tencent.mm.network.e", xpClassLoader);
        Class<?> fclass = XposedHelpers.findClass("com.tencent.mm.ah.f", xpClassLoader);
        if (eclass == null) return;

        Class<?> bclass = XposedHelpers.findClass("com.tencent.mm.plugin.ae.a.b", xpClassLoader);

        XposedHelpers.findAndHookMethod(bclass, "bTn", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {


                XposedBridge.log("DTN执行了！");

            }
        });
        XposedHelpers.findAndHookMethod(abclass, "a", eclass, fclass, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {


                Map<String, Object> map1 = new HashMap<String, Object>();
                XposedBridge.log("上传图片参数一.a()" + param.args[0] + "--" + ClassUtil.getKeyAndValue(param.args[0]).toString());
                XposedBridge.log("上传图片参数二.a()" + param.args[0] + "--" + ClassUtil.getKeyAndValue(param.args[1]).toString());


            }
        });

        //
//        Class<?> pclass = XposedHelpers.findClass("com.tencent.mm.ah.p", xpClassLoader);
//        Class<?> mClass = XposedHelpers.findClass("com.tencent.mm.ah.m", xpClassLoader);
//        XposedHelpers.findAndHookMethod(pclass, "a", mClass, int.class, new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//
//                int p2 = (int) param.args[1];
//                Map<String, Object> map1 = new HashMap<String, Object>();
//                Map<String, Object> map = ClassUtil.getKeyAndValue(param.args[0], 5, map1);
//                XposedBridge.log("监听发送消息.a()--" + map.toString() + "----" + p2);
//            }
//        });

//        Class<?> p$5class = XposedHelpers.findClass("com.tencent.mm.ah.p$5", xpClassLoader);
//        if (p$5class == null) return;
//        XposedHelpers.findAndHookMethod(p$5class, "run", new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//
//                XposedBridge.log("p$5class.run方法执行了()---"+param.thisObject.toString());
//            }
//        });


    }

    private void listenerimg2() {
        Class<?> sclass = XposedHelpers.findClassIfExists("com.tencent.mm.ui.chatting.SendImgProxyUI", xpClassLoader);
        if (sclass == null) return;


        XposedHelpers.findAndHookMethod(sclass, "a", ArrayList.class, int.class, ArrayList.class, boolean.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                ArrayList<String> paramArrayList = (ArrayList<String>) param.args[0];
                boolean paramBoolean = (boolean) param.args[3];
                ArrayList<String> paramArrayList2 = (ArrayList<String>) param.args[2];
                int paramInt = (int) param.args[1];
                XposedBridge.log("代理list--" + paramArrayList.toString() + "" +
                        "--" + paramBoolean +
                        "--" + paramInt +
                        "--" + paramArrayList2.toString() +
                        "---" + (param.getResult() == null ? param.getResult() : param.getResult().toString()));

            }
        });

        XposedHelpers.findAndHookMethod(sclass, "i", ArrayList.class, ArrayList.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                ArrayList<String> paramArrayList = (ArrayList<String>) param.args[0];
                ArrayList<String> paramArrayList2 = (ArrayList<String>) param.args[1];
                XposedBridge.log("i()--" + paramArrayList.toString() + "" +
                        "--" + paramArrayList2.toString());
            }
        });

        XposedHelpers.findAndHookMethod(sclass, "aFl", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                XposedBridge.log("afl()--执行");
            }
        });


    }

    private void listenerimg(final XC_LoadPackage.LoadPackageParam lpparam) {
        Class<?> sclass = XposedHelpers.findClassIfExists("com.tencent.mm.as.n", lpparam.classLoader);
        Class<?> asiclass = XposedHelpers.findClassIfExists("com.tencent.mm.as.i", lpparam.classLoader);

        if (sclass == null) return;

        XposedHelpers.findAndHookMethod(sclass, "a", ArrayList.class, boolean.class, int.class, int.class, String.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                ArrayList<String> paramArrayList = (ArrayList<String>) param.args[0];
                boolean paramBoolean = (boolean) param.args[1];
                int paramInt1 = (int) param.args[2];
                int paramInt2 = (int) param.args[3];
                String paramString = (String) param.args[4];
                int paramInt3 = (int) param.args[5];

                XposedBridge.log("HOOK发送图片--" + paramArrayList.toString() + "" +
                        "--" + paramBoolean +
                        "--" + paramInt1 +
                        "--" + paramInt2 +
                        "--" + paramString +
                        "--" + paramInt3);

            }


        });


        XposedHelpers.findAndHookMethod(asiclass, "a", ArrayList.class, String.class, String.class,
                ArrayList.class, int.class, boolean.class, int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        ArrayList<Integer> paramArrayList = (ArrayList<Integer>) param.args[0];
                        String paramString = (String) param.args[1];
                        String paramString2 = (String) param.args[2];
                        ArrayList<String> paramArrayList2 = (ArrayList<String>) param.args[3];
                        int paramInt1 = (int) param.args[4];
                        boolean paramBoolean = (boolean) param.args[5];
                        int paramInt2 = (int) param.args[6];


                        XposedBridge.log("我要看看这个方法--" + paramArrayList.toString() + "" +
                                "--" + paramString +
                                "--" + paramString2 +
                                "--" + paramArrayList2.toString() +
                                "--" + paramInt1 +
                                "--" + paramBoolean +
                                "--" + paramInt2
                        );

                    }


                });

        XposedHelpers.findAndHookMethod(sclass, "nH", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                String paramString = (String) param.args[0];

                XposedBridge.log("nH--" + paramString +
                        "---" + (param.getResult() == null ? param.getResult() : param.getResult().toString()));

            }

        });


    }

    private void hookChatImgMsg() {
        //微信发送图片的类
        Class<?> sclass = XposedHelpers.findClass("com.tencent.mm.as.n", xpClassLoader);
        //发送图片代理类
        Class<?> uiclass = XposedHelpers.findClass("com.tencent.mm.ui.chatting.SendImgProxyUI", xpClassLoader);

        //Class<?> b$1class = XposedHelpers.findClass("com.tencent.mm.plugin.ae.a.b", xpClassLoader);
        //上传图片的类
        Class<?> iclass = XposedHelpers.findClassIfExists("com.tencent.mm.as.i", xpClassLoader);


        if (sclass == null) {
            XposedBridge.log("未成功加载类！");
            return;
        }
        //构造微信图片数组
        ArrayList<String> picList = new ArrayList<>();
        picList.add("/storage/emulated/0/launcher/ad/f807c5c11a42152c3d474409efedb98b.png");

        //设置属性为true  好像是防止会有重复的实例
        //  XposedHelpers.setStaticBooleanField(uiclass, "isRunning", true);

        //read读取函数执行   貌似是检查图片合法性
//        ArrayList<String> picList2 = new ArrayList<>();
//        ArrayList<String> readlist = (ArrayList) XposedHelpers.callStaticMethod(uiclass, "a", picList, 26214400, picList2, true);

        //执行静态方法获取类  获取属性 即为对象实例
        Object objectabu = XposedHelpers.callStaticMethod(sclass, "abu");


        // Class<?> classG = XposedHelpers.findClassIfExists("com.tencent.mm.kernel.g", xpClassLoader);
        //Object objectMK = XposedHelpers.callStaticMethod(classG, "MK");
        //     XposedHelpers.callMethod(objectMK, "dbI");

        try {

            if (picList.size() > 0) {
                //执行发送方法  true是压缩  false发送原图  默然true
                XposedHelpers.callMethod(objectabu, "a", picList, false, 0, 0, "wxid_psn4vfy4kfr822", 2130838195);
                //    XposedBridge.invokeOriginalMethod(methodA, objectG, obj);
                //然后执行另一个方法 应该是校验的

                //    XposedHelpers.callMethod(objectMK, "dbJ");

                //这个是加入队列的
                XposedHelpers.callStaticMethod(uiclass, "aFl");

                //获取发送的集合 上传需要
                ArrayList<Integer> nhlist = (ArrayList<Integer>) XposedHelpers.callMethod(objectabu, "nH", "wxid_psn4vfy4kfr822");
             //   XposedBridge.log("nhlist" + nhlist.toString());

                /// [50]--wxid_4b9a1yqz3s0322--wxid_psn4vfy4kfr822--[/storage/emulated/0/launcher/ad/1244800853378a628796e134a886c043.png]--0--true--2130838195
                Object iobj = iclass.newInstance();
              //  XposedBridge.log("--上传线程运行-" + iobj);
                XposedHelpers.callMethod(iobj, "a", nhlist, "wxid_4b9a1yqz3s0322", "wxid_psn4vfy4kfr822", picList, 0, false, 2130838195);


            }
        } catch (Exception e) {
            e.printStackTrace();
            XposedBridge.log("发送图片异常信息：" + e.getMessage());
        }


    }


    /*发送二维码信息*/
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

        Object hp = XposedHelpers.newInstance(sclass, new Class[]{double.class, String.class, String.class}, 11, "0", "跟发送消息一个fun");
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


    //获取二维码信息
    private void hookChatQrCode(final XC_LoadPackage.LoadPackageParam lpparam) {
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
                    XposedBridge.log(fee + "--" + fee_type + "--" + desc);
                }
            });

            XposedHelpers.findAndHookMethod(sclass, "a", int.class, String.class, JSONObject.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    int i1 = (int) param.args[0];
                    String s1 = (java.lang.String) param.args[1];
                    JSONObject jsonObject = (JSONObject) param.args[2];


                    XposedBridge.log(i1 + "--" +
                            s1 + "--" +
                            jsonObject.toString()
                    );
                    //微信二维码链接
                    String pay_url = jsonObject.getString("pay_url");

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
    private void hookDatabaseInsert(final XC_LoadPackage.LoadPackageParam lpparam) {
        Class<?> classDb = XposedHelpers.findClassIfExists(WxConfig.DATABASE_PACKAGE_NAME, lpparam.classLoader);
        if (classDb == null) {
            return;
        } else {
            XposedHelpers.findAndHookMethod(classDb,
                    WxConfig.DATABASE_HOOKFUN,
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


        //目前用这个状态表示好友发来的消息  pc电脑端发送status=3 但是isSend=0
        if (status == 3 && isSend != 1) {

            if (talker.equals("weixin")) { //系统账号发来的消息

            } else if (talker.endsWith("@chatroom")) {   //群消息


            } else if (talker.startsWith("gh_")) { //公众号消息


            } else {  //好友消息
                //  sendQrCodeMsg();
                //    Tuling123 tuling123 = new Tuling123(talker,content);
                //     tuling123.run();
                hookChatImgMsg();
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
        //    Object objectdpP = XposedHelpers.getObjectField(objectG, "eYH");

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
            //发送消息  只要这里传的静态的实例属性  两种方法都可以行
            //        XposedBridge.log(pClass+"---"+objectG+"--test--"+test);
            XposedHelpers.callMethod(objectG, "a", hp, 0);
            //     XposedBridge.invokeOriginalMethod(methodA, objectG, hpobj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * 微信防检测
     * */
    private void hookWxCheckXp() {
        Class<?> tclass = XposedHelpers.findClassIfExists("com.tencent.mm.app.t", xpClassLoader);
        if (tclass == null) return;
        XposedHelpers.findAndHookMethod(tclass, "a", StackTraceElement[].class, new XC_MethodHook() {
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(false);  // 设置返回值
                XposedBridge.log("WX防封检测，设置返回值为false！");
            }
        });
    }

}

package cn.lemon.lwr.components.data;

import android.content.ContentValues;

import cn.lemon.lwr.components.WxConst;
import cn.lemon.lwr.components.WxHook;
import cn.lemon.lwr.components.msg.WxMessageHandler;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

import static de.robv.android.xposed.XposedBridge.log;

/**
 * 数据库接收信息处理类
 * 目前接收信息全部在这里调用处理
 */
public class WxDataBaseHandler {
    //hook数据库插入操作

    public  static  void hookDatabaseInsert() {
        Class<?> classDb = XposedHelpers.findClassIfExists(WxConst.DATABASE_PACKAGE_NAME, WxHook.wxClassLoader);
        if (classDb == null) {
            return;
        } else {
            XposedHelpers.findAndHookMethod(classDb,
                    WxConst.DATABASE_HOOKFUN,
                    String.class, String.class, ContentValues.class, int.class,
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            String tableName = (String) param.args[0];
                            ContentValues contentValues = (ContentValues) param.args[2];
                            if (tableName == null || tableName.length() == 0 || contentValues == null) {
                                return;
                            }
                            //处理聊天消息
                            if (tableName.equals("message")) {
                                //打印出数据库插入日志
                                printInsertLog(tableName, (String) param.args[1], contentValues, (Integer) param.args[3]);
                                //判断消息类型进行回复
                                msgTypeInfo(contentValues);
                            }
                        }
                    });
        }

    }

    //输出数据库日志
    private static void printInsertLog(String tableName, String nullColumnHack, ContentValues contentValues, int conflictValue) {
        String[] arrayConflicValues =
                {"", " OR ROLLBACK ", " OR ABORT ", " OR FAIL ", " OR IGNORE ", " OR REPLACE "};
        if (conflictValue < 0 || conflictValue > 5) {
            return;
        }
        log("Hook数据库insert。table：" + tableName
                + "；nullColumnHack：" + nullColumnHack
                + "；CONFLICT_VALUES：" + arrayConflicValues[conflictValue]
                + "；contentValues:" + contentValues);

    }


    //接收的消息类型
    private static void msgTypeInfo(ContentValues contentValues) {
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
                //  hookChatImgMsg();
                WxMessageHandler.sendTextMsg(talker, content);
            }


        }
    }


}

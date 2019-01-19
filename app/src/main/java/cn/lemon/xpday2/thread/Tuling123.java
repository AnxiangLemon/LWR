package cn.lemon.xpday2.thread;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cn.lemon.xpday2.MyXposedInit;
import cn.lemon.xpday2.util.HttpRequest;
import cn.lemon.xpday2.util.StringUtils;
import de.robv.android.xposed.XposedBridge;

/*图灵接口*/
public class Tuling123 implements Runnable{
    private String  tlurl = "http://www.tuling123.com/openapi/api?key=b07798f2f9cd4db683a86a412ea08662&info=";

    private String msg;
    private String talker;

    public Tuling123(String talker,String msg) {
        this.msg = msg;
        this.talker = talker;
    }

    @Override
    public void run() {
        try {
            String info = URLEncoder.encode(this.msg, "utf-8");
            //请求网络
            HttpRequest httpRequest = new HttpRequest(this.tlurl+info);
            String strResponse = httpRequest.getData();
             String tulingmsg =  strResponse.substring(strResponse.indexOf("text\":\"")+7,strResponse.lastIndexOf("\""));
            if(StringUtils.isNotEmpty(tulingmsg)){
                MyXposedInit.sendTextMsg(this.talker, tulingmsg);
            }else{
                XposedBridge.log("图灵消息获取失败："+strResponse);
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

}

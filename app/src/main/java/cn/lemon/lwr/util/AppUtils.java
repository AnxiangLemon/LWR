package cn.lemon.lwr.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.List;

public final class AppUtils {
    //获取某个app（微信或XP框架）的安装版本号。未安装，返回空字符串。
    public static String getAppVersionName(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        for (PackageInfo packageInfo : packageInfos) {
            if (packageInfo.packageName.equals(packageName)) {
                return packageInfo.versionName;
            }
        }
        return "";
    }

    //Xposed模块是否激活。默认返回false；激活后hook将其值改为true
    public static boolean isModuleActive() {
        return false;
    }
}

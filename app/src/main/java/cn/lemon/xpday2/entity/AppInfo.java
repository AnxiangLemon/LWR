package cn.lemon.xpday2.entity;

import android.content.Context;

import cn.lemon.xpday2.config.AppConfig;
import cn.lemon.xpday2.util.AppUtils;
import cn.lemon.xpday2.util.StringUtils;

/*
* 检测xposed 是否安装而已
* */
public final class AppInfo {
    //Xposed是否安装
    private boolean mIsXposedInstall;
    private String mXposedVersionName;
    //微信是否安装、版本号
    private boolean mIsWechatInstall;
    private String mWechatVersionName;
    //判断微信版本是否符合要求
    private boolean mIsWechatVer;




    //region 单例
    //单例
    private static volatile AppInfo mInstance = null;

    private AppInfo() {
    }

    public static AppInfo getInstance() {
        if (mInstance == null) {
            synchronized (AppInfo.class) {
                if (mInstance == null) {
                    mInstance = new AppInfo();
                }
            }
        }
        return mInstance;
    }

    //endregion
    //检测设备环境
    public void ValidateEnvironment(Context context) {
        //判断Xposed是否安装
        mXposedVersionName = AppUtils.getAppVersionName(context, AppConfig.PACKAGE_NAME_XPOSED);
        mIsXposedInstall = StringUtils.isNotEmpty(getmXposedVersionName());

        //判断微信
        mWechatVersionName = AppUtils.getAppVersionName(context, AppConfig.PACKAGE_NAME_WECHAT);
        mIsWechatInstall = !StringUtils.isEmpty(getmWechatVersionName());
        //判断微信版本是否符合要求
        mIsWechatVer = mIsWechatInstall && isContainWechatVersion();

    }

    //获取微信版本是否包含在支持列表中
    private boolean isContainWechatVersion() {
        if (mWechatVersionName == null) {
            return false;
        }
        for (int i = 0; i <AppConfig.WECHAT_VER.length; i++) {
            if (mWechatVersionName.equals(AppConfig.WECHAT_VER[i])) {
                return true;
            }
        }
        return false;
    }


    //获取Xposed模块是否激活
    public boolean isXposedActive() {
        return AppUtils.isModuleActive();
    }

    public boolean ismIsXposedInstall() {
        return mIsXposedInstall;
    }

    public void setmIsXposedInstall(boolean mIsXposedInstall) {
        this.mIsXposedInstall = mIsXposedInstall;
    }

    public String getmXposedVersionName() {
        return mXposedVersionName;
    }

    public void setmXposedVersionName(String mXposedVersionName) {
        this.mXposedVersionName = mXposedVersionName;
    }

    public boolean ismIsWechatInstall() {
        return mIsWechatInstall;
    }

    public void setmIsWechatInstall(boolean mIsWechatInstall) {
        this.mIsWechatInstall = mIsWechatInstall;
    }

    public String getmWechatVersionName() {
        return mWechatVersionName;
    }

    public void setmWechatVersionName(String mWechatVersionName) {
        this.mWechatVersionName = mWechatVersionName;
    }

    public boolean ismIsWechatVer() {
        return mIsWechatVer;
    }

    public void setmIsWechatVer(boolean mIsWechatVer) {
        this.mIsWechatVer = mIsWechatVer;
    }
}

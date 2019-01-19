package cn.lemon.xpday2;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;

import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.lemon.xpday2.entity.AppInfo;

public class MainActivity extends Activity {
    @BindView(R.id.topbar)
    QMUITopBar mTopBar;
    @BindView(R.id.groupListView)
    QMUIGroupListView mGroupListView;
    private final String TAG = getClass().getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);
        // 沉浸式状态栏
        QMUIStatusBarHelper.translucent(this);
        View root = LayoutInflater.from(this).inflate(R.layout.activity_main, null);
        ButterKnife.bind(this, root);
        //初始化状态栏
        initTopBar();
        //设置view
        setContentView(root);

        //检测环境
        AppInfo.getInstance().ValidateEnvironment(this);
        //初始化QMUIGroupListView
        initMainContentView();


    }

    //初始化状态栏
    private void initTopBar() {
        mTopBar.setTitle(getResources().getString(R.string.activity_title_main));
    }



    //初始化QMUIGroupListView
    private void initMainContentView() {
        AppInfo appInfo = AppInfo.getInstance();
        boolean boolResult;
        String strResult = "";

        //region Xposed框架状态
        //Xposed版本
        QMUICommonListItemView listItemXposed = mGroupListView.createItemView("Xposed版本");
        //查看xp是否安装
        boolResult = appInfo.ismIsXposedInstall();
        //已经安装显示版本号   未安装 提示未安装
        listItemXposed.setDetailText(boolResult ? "V" + appInfo.getmXposedVersionName() : "未安装");
        //UI勾选
        listItemXposed.setImageDrawable(getResources().getDrawable(boolResult ? R.drawable.qmui_icon_checkbox_checked : R.mipmap.icon_error));
        //Xposed模块激活
        QMUICommonListItemView listItemXposedActive = mGroupListView.createItemView("LWR模块激活");
        //查看模块是否激活
        boolResult = appInfo.isXposedActive();
        listItemXposedActive.setDetailText(boolResult ? "已激活" : "未激活");
        listItemXposedActive.setImageDrawable(getResources().getDrawable(boolResult ? R.drawable.qmui_icon_checkbox_checked : R.mipmap.icon_error));

        //微信安装版本
        QMUICommonListItemView listItemWechatVersion = mGroupListView.createItemView("微信版本");
        boolResult = appInfo.ismIsWechatInstall();
        listItemWechatVersion.setDetailText(boolResult ? "V" + appInfo.getmWechatVersionName() : "未安装");
        boolResult = appInfo.ismIsWechatVer();
        listItemWechatVersion.setImageDrawable(ContextCompat.getDrawable(this, boolResult ? R.drawable.qmui_icon_checkbox_checked : R.mipmap.icon_error));

        QMUIGroupListView.newSection(this)
                .setTitle("框架信息")
                .addItemView(listItemXposed, null)
                .addItemView(listItemXposedActive, null)
                .addItemView(listItemWechatVersion, null)
                .addTo(mGroupListView);
        //endregion

    }


}

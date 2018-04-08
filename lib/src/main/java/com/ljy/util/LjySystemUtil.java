package com.ljy.util;

import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by Mr.LJY on 2017/12/25.
 * <p>
 * 提供系统相关方法
 */

public class LjySystemUtil {

    /**
     * 判断当前栈顶的activity
     * <p>
     * 使用时应注意className应该为activity的name，fragment等是不行的
     * <p>
     * 需要权限：android.permission.GET_TASKS
     *
     * @param context
     * @param className
     * @return
     */
    public static boolean isForeground(Context context, String className) {
        context = context.getApplicationContext();
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            String currentClassName = cpn.getClassName().substring(cpn.getClassName().lastIndexOf(".") + 1);
            if (className.equals(currentClassName)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 判断当前手机api系统版本是否>=指定版本
     *
     * @param versionCode
     * @return
     */
    public static boolean checkSdkVersion(int versionCode) {
        return Build.VERSION.SDK_INT >= versionCode;
    }

    /**
     * 判断服务是否启动了
     *
     * @param context
     * @param className
     * @return
     */
    public static boolean isServiceWorked(Context context, Class className) {
        ActivityManager myManager = (ActivityManager) context.getApplicationContext().getSystemService(
                Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager
                .getRunningServices(30);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString()
                    .equals(className.getName())) {
                return true;
            }
        }
        return false;
    }

    public static String getStringFromAssets(Context context, String fileName) {
        try {
            InputStream input = context.getResources().getAssets().open(fileName);
            return getString(input);
        } catch (IOException e) {
            return "";
        }
    }


    public static String getStringFromRaw(Context context, int resId) {

        InputStream input = context.getResources().openRawResource(resId);
        return getString(input);
    }

    @NonNull
    private static String getString(InputStream input) {
        InputStreamReader inputReader = null;
        BufferedReader bufReader = null;
        try {
            inputReader = new InputStreamReader(input, "utf-8");
            bufReader = new BufferedReader(inputReader);
            String line;
            StringBuffer stringBuffer = new StringBuffer();
            while ((line = bufReader.readLine()) != null) {
                stringBuffer.append(line);
                stringBuffer.append("\n");
            }
            return stringBuffer.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputReader != null)
                    inputReader.close();
                if (bufReader != null)
                    bufReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 创建某个界面的桌面快捷入口
     * 注意：manifest中需要添加权限，
     * 对应的activity中要
     * <intent-filter>
     * <action android:name="android.intent.action.MAIN"/>
     * </intent-filter>
     *
     * @param context
     * @param activityName
     * @param name
     * @param icon
     */
    public static void addShortcut(Context context, String activityName, String name, int icon) {
        //   创建快捷方式的intent广播
        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        //  添加快捷名称
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
        //  快捷图标是允许重复
        shortcut.putExtra("duplicate", false);
        // 快捷图标
        Intent.ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(context, icon);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
        //  我们下次启动要用的Intent信息
        Intent carryIntent = new Intent(Intent.ACTION_MAIN);
        carryIntent.putExtra("name", name);
        carryIntent.setClassName(context, activityName);
        carryIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //添加携带的Intent
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, carryIntent);
        //   发送广播
        context.sendBroadcast(shortcut);
    }

    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取app 的 versionName
     */
    public static String getVersionName(Context context) {
        PackageInfo packInfo = getPackageInfo(context);
        return packInfo == null ? "" : packInfo.versionName;
    }

    /**
     * 获取app 的 versionName
     */
    public static int getVersionCode(Context context) {
        PackageInfo packInfo = getPackageInfo(context);
        return packInfo == null ? -1 : packInfo.versionCode;
    }

    /**
     * 获取app 的 PackageInfo
     */
    private static PackageInfo getPackageInfo(Context context) {
        context = context.getApplicationContext();
        //获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        try {
            return packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    /**
     * 复制文字到剪切板
     *
     * @param context
     * @param info
     */
    public static void copyToClipboard(Context context, String info) {
        if (android.os.Build.VERSION.SDK_INT > 11) {
            android.content.ClipboardManager c = (android.content.ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
            c.setPrimaryClip(ClipData.newPlainText("tag", info));

        } else {
            android.text.ClipboardManager c = (android.text.ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
            c.setText(info);
        }
    }

}

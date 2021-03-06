package com.ljy.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by Mr.LJY on 2018/4/8.
 */

public class LjyScreenUtils {
    /**
     * 获得屏幕的宽
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        if (context == null)
            return 0;
        context = context.getApplicationContext();
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        return w_screen;
    }

    /**
     * 获得屏幕的高
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        if (context == null)
            return 0;
        context = context.getApplicationContext();
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int w_height = dm.heightPixels;
        return w_height;
    }

    /**
     * 获得状态栏的高度
     *
     * @param context
     * @return
     */
    public static int getStatusHeight(Context context) {

        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    /**
     * 获取当前屏幕截图，包含状态栏
     *
     * @param activity
     * @return
     */
    public static Bitmap snapShotWithStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
        view.destroyDrawingCache();
        return bp;

    }

    /**
     * 获取当前屏幕截图，不包含状态栏
     *
     * @param activity
     * @return
     */
    public static Bitmap snapShotWithoutStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();
        return bp;

    }

    /**
     * 沉浸式(透明)状态栏
     *
     * @param activity
     */
    public static void noStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = activity.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 沉浸式(透明)底部虚拟按键
     * @param activity
     */
    public static void noNavigationBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = activity.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;//底部虚拟按键
            decorView.setSystemUiVisibility(option);
            activity.getWindow().setNavigationBarColor(Color.TRANSPARENT);//底部虚拟按键
        }
    }

    /**
     * android P 全屏时 适配刘海屏
     * <p>
     * 刘海屏只有是全屏时才需要手动适配, 有状态栏时(即使是沉浸式/透明的)是不用我们单独适配的
     */
    public static void setCutoutMode(Activity activity) {
//       setCutoutMode(activity,WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS);
    }

    public static void setCutoutMode(Activity activity, int mode) {
        if (Build.VERSION.SDK_INT >= 27) {
//            WindowManager.LayoutParams lp
//                    =activity.getWindow().getAttributes();
//            lp.layoutInDisplayCutoutMode =mode;
//            activity.getWindow().setAttributes(lp);
        }
    }

    /**
     * 获取刘海高度
     */
    @RequiresApi(api = 28)
    public static int getCutoutHeight(View view) {
        int cutoutHeight = 0;
//        DisplayCutout displayCutout = view.getRootWindowInsets().getDisplayCutout();
//        cutoutHeight = displayCutout.getSafeInsetTop();
        return cutoutHeight;
    }
}

package com.ljy.util;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * Created by Mr.LJY on 2017/12/26.
 * <p>
 * 提供view相关方法
 */

public class LjyViewUtil {

    /**
     * 重新计算ListView item的高度，解决ScrollView和ListView两个View都有滚动的效果，在嵌套使用时起冲突的问题
     * <p>
     * （ScrollView嵌套ListView只显示一行，计算的高度不正确的解决办法）
     *
     * @param listView
     */
    public static void setListViewHeight(ListView listView) {

        // 获取ListView对应的Adapter

        ListAdapter listAdapter = listView.getAdapter();

        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /**
     * View的宽高=屏幕的宽高*scaleW/H
     *
     * @param context      上下文对象，用于获取屏幕宽高
     * @param layoutParams 目标view的layoutParams
     * @param scaleW       宽的缩放
     * @param scaleH       高的缩放
     * @param type         几种状态:0为正常，1为使高等于宽，2为使宽等于高,3为height=WRAP_CONTENT，4为width=WRAP_CONTENT
     */
    public static void setViewSize(Context context, ViewGroup.LayoutParams layoutParams, float scaleW, float scaleH, int type) {
        if (scaleW <= 0 && scaleH <= 0)
            return;
        if (context == null || layoutParams == null)
            return;
        context = context.getApplicationContext();

        int windowWidth = LjyScreenUtils.getScreenWidth(context);
        int windowHeight = LjyScreenUtils.getScreenHeight(context);
        int viewWidth;
        int viewHeight;
        type = scaleW <= 0 ? 2 : scaleH <= 0 ? 1 : type;
        switch (type) {
            case 0:
                viewWidth = (int) (windowWidth * scaleW);
                viewHeight = (int) (windowHeight * scaleH);
                break;
            case 1:
                viewWidth = (int) (windowWidth * scaleW);
                viewHeight = viewWidth;
                break;
            case 2:
                viewHeight = (int) (windowHeight * scaleH);
                viewWidth = viewHeight;
                break;
            case 3:
                viewWidth = (int) (windowWidth * scaleW);
                viewHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
                break;
            case 4:
                viewWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
                viewHeight = (int) (windowHeight * scaleH);
                break;
            default:
                viewWidth = RelativeLayout.LayoutParams.WRAP_CONTENT;
                viewHeight = RelativeLayout.LayoutParams.WRAP_CONTENT;
                break;
        }
        layoutParams.width = viewWidth;
        layoutParams.height = viewHeight;
    }

    public static int getViewWidth(View view) {
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(width, height);
        return view.getMeasuredWidth();
    }

    public static int getViewHeight(View view) {
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(width, height);
        return view.getMeasuredHeight();
    }

    /**
     * 使view可以拖拽移动
     */
    public static void touchMove(final View view, ViewGroup parentView) {
        final int parentViewWidth = parentView.getWidth();
        final int parentViewHeight = parentView.getHeight();
        final int viewWidth = getViewWidth(view);
        final int viewHeight = getViewHeight(view);
        view.setOnTouchListener(new View.OnTouchListener() {
            public int lastY;
            public int lastX;
            public int firstX;
            public int firstY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = firstX = (int) event.getRawX();
                        lastY = firstY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) event.getRawX() - lastX;
                        int dy = (int) event.getRawY() - lastY;

                        int top = v.getTop() + dy;

                        int left = v.getLeft() + dx;

                        if (top <= 0) {
                            top = 0;
                        }

                        if (top >= parentViewHeight - viewHeight) {
                            top = parentViewHeight - viewHeight;
                        }
                        if (left >= parentViewWidth - viewWidth) {
                            left = parentViewWidth - viewWidth;
                        }

                        if (left <= 0) {
                            left = 0;
                        }

                        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        param.leftMargin = left;
                        param.topMargin = top;
                        v.setLayoutParams(param);
                        v.postInvalidate();
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        if (event.getRawX() == firstX && event.getRawY() == firstY) {
                            view.setClickable(true);
                        } else {
                            view.setClickable(false);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    default:
                        break;

                }
                return false;
            }
        });
    }

}

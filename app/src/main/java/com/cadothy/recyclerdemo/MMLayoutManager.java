/*
 * Copyright (C) 2011-2020 ShenZhen iBOXCHAIN Information Technology Co.,Ltd.
 * All right reserved.
 * This software is the confidential and proprietary
 * information of iBOXCHAIN Company of China.
 * ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only
 *  in accordance with the terms of the contract agreement
 *  you entered into with iBOXCHAIN inc.
 *
 */

package com.cadothy.recyclerdemo;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author weishiwei
 * @date 2021/3/23
 * @description
 **/
public class MMLayoutManager extends RecyclerView.LayoutManager {
    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private int mVerticalOffset;//竖直偏移量 每次换行时，要根据这个offset判断
    private int mFirstVisiPos;//屏幕可见的第一个View的Position
    private int mLastVisiPos;//屏幕可见的最后一个View的Position
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() == 0) {//没有Item，界面空着吧
            detachAndScrapAttachedViews(recycler);
            return;
        }
        if (getChildCount() == 0 && state.isPreLayout()) {//state.isPreLayout()是支持动画的
            return;
        }
        //onLayoutChildren方法在RecyclerView 初始化时 会执行两遍
        detachAndScrapAttachedViews(recycler);
        //初始化
        mVerticalOffset = 0;
        mFirstVisiPos = 0;
        mLastVisiPos = getItemCount();

        //初始化时调用 填充childView
        fill(recycler, state);
    }

    private void fill(RecyclerView.Recycler recycler, RecyclerView.State state) {
        Log.w("wsw","fill" );
        int topOffset = getPaddingTop();//布局时的上偏移
        int leftOffset = getPaddingLeft();//布局时的左偏移
        int lineMaxHeight = 0;//每一行最大的高度
        int minPos = mFirstVisiPos;//初始化时，我们不清楚究竟要layout多少个子View，所以就假设从0~itemcount-1
        mLastVisiPos = getItemCount() - 1;
        //顺序addChildView
        for (int i = minPos; i <= mLastVisiPos; i++) {
            //找recycler要一个childItemView,我们不管它是从scrap里取，还是从RecyclerViewPool里取，亦或是onCreateViewHolder里拿。
            View child = recycler.getViewForPosition(i);
            addView(child);
            measureChildWithMargins(child, 0, 0);
            //计算宽度 包括margin
//            if (leftOffset + getDecoratedMeasurementHorizontal(child) <= getHorizontalSpace()) {//当前行还排列的下
                layoutDecoratedWithMargins(child, leftOffset, topOffset, leftOffset + getDecoratedMeasurementHorizontal(child), topOffset + getDecoratedMeasurementVertical(child));

                //改变 left  lineHeight
                leftOffset += getDecoratedMeasurementHorizontal(child);
                lineMaxHeight = Math.max(lineMaxHeight, getDecoratedMeasurementVertical(child));
//            } else {//当前行排列不下
//                //改变top  left  lineHeight
//                leftOffset = getPaddingLeft();
//                topOffset += lineMaxHeight;
//                lineMaxHeight = 0;
//
//                //新起一行的时候要判断一下边界
//                if (topOffset - dy > getHeight() - getPaddingBottom()) {
//                    //越界了 就回收
//                    removeAndRecycleView(child, recycler);
//                    mLastVisiPos = i - 1;
//                } else {
//                    layoutDecoratedWithMargins(child, leftOffset, topOffset, leftOffset + getDecoratedMeasurementHorizontal(child), topOffset + getDecoratedMeasurementVertical(child));
//
//                    //改变 left  lineHeight
//                    leftOffset += getDecoratedMeasurementHorizontal(child);
//                    lineMaxHeight = Math.max(lineMaxHeight, getDecoratedMeasurementVertical(child));
//                }
//            }
        }

    }

    /**
     * 获取某个childView在水平方向所占的空间
     *
     * @param view
     * @return
     */
    public int getDecoratedMeasurementHorizontal(View view) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                view.getLayoutParams();
        return getDecoratedMeasuredWidth(view) + params.leftMargin
                + params.rightMargin;
    }

    /**
     * 获取某个childView在竖直方向所占的空间
     *
     * @param view
     * @return
     */
    public int getDecoratedMeasurementVertical(View view) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                view.getLayoutParams();
        return getDecoratedMeasuredHeight(view) + params.topMargin
                + params.bottomMargin;
    }

    public int getVerticalSpace() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    public int getHorizontalSpace() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    @Override
    public boolean canScrollVertically() {
        return false;
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

//    @Override
//    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
//        int realOffset = dy;//实际滑动的距离， 可能会在边界处被修复
//
//        offsetChildrenVertical(-realOffset);
//
//        return realOffset;
//    }


    @Override
    public void removeAndRecycleAllViews(@NonNull RecyclerView.Recycler recycler) {
        super.removeAndRecycleAllViews(recycler);
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int realOffset = dx;//实际滑动的距离， 可能会在边界处被修复

//        offsetChildrenHorizontal(-realOffset);


        //位移0、没有子View 当然不移动
        if (dx == 0 || getChildCount() == 0) {
            return 0;
        }

        //边界修复代码
        if (mVerticalOffset + realOffset < 0) {//左边界
            realOffset = -mVerticalOffset;
        } else if (realOffset > 0) {//右边界
            //利用最后一个子View比较修正
            View lastChild = getChildAt(getChildCount() - 1);
            if (getPosition(lastChild) == getItemCount() - 1) {
                int gap = getWidth() - getPaddingRight() - getDecoratedRight(lastChild);
                if (gap > 0) {
                    realOffset = -gap;
                } else if (gap == 0) {
                    realOffset = 0;
                } else {
                    realOffset = Math.min(realOffset, -gap);
                }
            }
        }

//        realOffset = fill(recycler, state, realOffset);//先填充，再位移。

        mVerticalOffset += realOffset;//累加实际滑动距离

        offsetChildrenHorizontal(-realOffset);//滑动
        return realOffset;
    }
}

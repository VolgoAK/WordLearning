package xyz.volgoak.wordlearning;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Alexander Karachev on 07.05.2017.
 */

public class CustomFloatingButtonBehavior extends CoordinatorLayout.Behavior<FloatingActionButton>{
    private Context mContext;

    private int mStartX;
    private int mFinishX;
    private int mScrollPAth;
    private int mBarSize;

    private int mOrderInRow;
    private int mMaxSize;
    private int mMinSize;
    private int mMargin;

    public CustomFloatingButtonBehavior(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
        mContext = context;

        if(attributeSet != null){
            TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.FloatButtonBehavior);
            mOrderInRow = a.getInt(R.styleable.FloatButtonBehavior_behavior_order_in_row, 1);
            mMaxSize = (int) a.getDimension(R.styleable.FloatButtonBehavior_behavior_max_size, 70);
            mMinSize = (int) a.getDimension(R.styleable.FloatButtonBehavior_behavior_min_size, 20);
            mMargin = (int) a.getDimension(R.styleable.FloatButtonBehavior_behavior_margin, 16);

            a.recycle();
        }
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {

        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        shouldInitialize(child, dependency);

        float dependencyBottom = dependency.getBottom();

        float percentage = (dependencyBottom - mBarSize) / mScrollPAth;
        float size = mMinSize + (mMaxSize - mMinSize ) * percentage;
        float xPos = mFinishX - (mFinishX - mStartX) * percentage;
        float yPos = dependencyBottom - size/2;

        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        lp.width = (int) size;
        lp.height = (int) size;
        child.setLayoutParams(lp);

        child.setX(xPos);
        child.setY(yPos);

        return true;
    }

    private void shouldInitialize(FloatingActionButton child, View dependency){
        if(mScrollPAth == 0) mScrollPAth = ((AppBarLayout) dependency).getTotalScrollRange();
        if(mStartX == 0) mStartX = dependency.getWidth() - (mMaxSize + mMargin) * mOrderInRow;
        if(mFinishX == 0) mFinishX = dependency.getWidth() - (mMinSize + mMargin) * mOrderInRow;
        if(mBarSize == 0) mBarSize = dependency.getHeight() - mScrollPAth;
    }

}

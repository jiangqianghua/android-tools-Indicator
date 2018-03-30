package com.jqh.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jiangqianghua.viewpagerindicator.R;

import java.util.List;

/**
 * Created by jiangqianghua on 2016/6/19.
 */
public class ViewPagerIndicator extends LinearLayout {

    // 绘制三角形
    private Paint mPaint ;

    private Path mPath ;
    //  三角形宽高
    private int mTriangleWidth ;
    private int mTriangleHeight ;

    private static final float RADIO_TRIANGLEWIDTH = 1/6f;
    //  初始化偏移位置
    private int mInitTranslationX ;
    // 移动时候的偏移位置
    private int mTranslationX;
    //  当前显示的数量
    private int mTabVisibleCount  ;
    // 默认tab可显示的数量
    private static final int COUNT_DEFAULT_TAB = 4 ;

    private List<String> mTitles ;

    private static final int COLOR_TEXT_NORMAL = 0x77ffffff;
    // 文字高亮显示
    private static final int COLOR_TEXT_HIGHLIGHT = 0xffffffff;
    //  三角形最大宽度
    private  final int DIMENSION_TRIANGLE_WIDTH_MAX = (int)(getScreenWidth()/3*RADIO_TRIANGLEWIDTH) ;
    public ViewPagerIndicator(Context context) {
        super(context);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        //  获取自定义属性
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator);
        mTabVisibleCount = a.getInt(R.styleable.ViewPagerIndicator_visbale_tab_count,COUNT_DEFAULT_TAB);
        if(mTabVisibleCount < 0)
        {
            mTabVisibleCount = COUNT_DEFAULT_TAB ;
        }
        a.recycle();
        // 初始化画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor("#ffffffff"));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setPathEffect(new CornerPathEffect(3));  // 连接处有3角度的弧度
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTriangleWidth = (int)(w/mTabVisibleCount*RADIO_TRIANGLEWIDTH);
        mTriangleWidth = Math.min(mTriangleWidth,DIMENSION_TRIANGLE_WIDTH_MAX);
        mInitTranslationX = w/mTabVisibleCount/2 - mTriangleWidth/2;
        initTriangle();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        canvas.save();
        canvas.translate(mInitTranslationX + mTranslationX, getHeight());
        canvas.drawPath(mPath, mPaint);
        canvas.restore();
        super.dispatchDraw(canvas);
    }

    // 初始化三角形
    private void initTriangle()
    {
        mTriangleHeight = mTriangleWidth/2;
        mPath = new Path();
        mPath.moveTo(0, 0);
        mPath.lineTo(mTriangleWidth, 0);
        mPath.lineTo(mTriangleWidth / 2, -mTriangleHeight);
        mPath.close();
    }

    //  布局加载完成后执行
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int cCount = getChildCount();
        if(cCount == 0)return ;
        for(int i = 0 ; i < cCount; i++)
        {
            View view = getChildAt(i);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)view.getLayoutParams();
            lp.weight = 0 ;
            lp.width = getScreenWidth()/mTabVisibleCount;
            view.setLayoutParams(lp);
        }
        setItemClickEvent();
    }
    //  获取屏幕宽度
    private int getScreenWidth()
    {
        WindowManager wm = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels ;

    }

    public void scroll(int position, float offset)
    {
        int tabWidth = getWidth()/mTabVisibleCount ;
        mTranslationX = (int)(tabWidth*(offset+position));
        // 当tab处于最后一个，容器移动
        if(position>=(mTabVisibleCount-2)&&offset>0&&getChildCount()>mTabVisibleCount)
        {
            if(mTabVisibleCount != 1)
               this.scrollTo((position-(mTabVisibleCount-2))*tabWidth+(int)(tabWidth*offset),0);
            else
                this.scrollTo(position*tabWidth+(int)(tabWidth*offset),0);
        }
        invalidate();
    }

    // 动态生成tab
    public void setTableItemTitles(List<String> titles )
    {
        if(titles != null && titles.size() > 0)
        {
            this.removeAllViews();
            mTitles = titles ;
            for(String title:mTitles)
            {
                addView(getGenerateTextView(title));
            }
        }
        setItemClickEvent();
    }


    // 动态生成tab
    private View getGenerateTextView(String title)
    {
        TextView tv = new TextView(getContext());
        LinearLayout.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        lp.width = getScreenWidth()/mTabVisibleCount;
        tv.setText(title);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tv.setTextColor(COLOR_TEXT_NORMAL);
        tv.setLayoutParams(lp);
        return tv ;
    }

    //  动态设置可现实的数量，一定要在setTableItemTitles 前调用
    public void setmTabVisibleCount(int count)
    {
        mTabVisibleCount = count;
    }
    // 设置关联的ViewPager
    private ViewPager mViewPager ;
    public void setViewPager(ViewPager viewPager, int pos)
    {
        mViewPager = viewPager ;
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // tabWidth*positionOffset + position*tabWidth
                scroll(position, positionOffset);
                if (mListener != null)
                    mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                if (mListener != null)
                    mListener.onPageSelected(position);
                hightLightTextView(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (mListener != null)
                    mListener.onPageScrollStateChanged(state);
            }
        });

        mViewPager.setCurrentItem(pos);

        hightLightTextView(pos);
    }

    /**
     * 高亮指定显示tab文字
     * @param pos
     */
    private void hightLightTextView(int pos)
    {
        resetTextViewColor();
        View view = getChildAt(pos);
        if(view instanceof TextView)
        {
            ((TextView)view).setTextColor(COLOR_TEXT_HIGHLIGHT);
        }
    }

    /**
     * 重置所有文本颜色
     */
    private void resetTextViewColor()
    {
        for(int i = 0 ;i < getChildCount(); i++)
        {
            View view = getChildAt(i);
            if(view instanceof TextView)
            {
                ((TextView)view).setTextColor(COLOR_TEXT_NORMAL);
            }
        }
    }



    /**
     * tab 点击事件
     */
    private  void setItemClickEvent()
    {
        int cCount = getChildCount() ;
        for(int i = 0 ;i < cCount ; i++)
        {
            final int j = i ;
            View view = getChildAt(i);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(j);
                }
            });
        }
    }

    /**
     * 提供给外部的接口调用
     */
    public interface PageOnChangeListener
    {
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) ;
        public void onPageSelected(int position) ;
        public void onPageScrollStateChanged(int state) ;
    }

    public PageOnChangeListener mListener ;
    public void setOnPagechangeListener(PageOnChangeListener listener)
    {
        mListener = listener ;
    }
}

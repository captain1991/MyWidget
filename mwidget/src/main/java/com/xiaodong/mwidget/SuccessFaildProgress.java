package com.xiaodong.mwidget;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;


/**
 * Created by yxd on 2017/10/11.
 */

public class SuccessFaildProgress extends View {
    Paint mPaint;
    StatusEnum mStatus;
    private int startAngle = -90;
    private int minAngle = -90;
    private int sweepAngle = 120;
    private int curAngle = 0;
    private float progressWidth;    //进度宽度
    private float progressRadius;   //圆环半径
    //追踪Path的坐标
    private PathMeasure mPathMeasure;
    //画圆的Path
    private Path mPathCircle;
    //截取PathMeasure中的path
    private Path mPathCircleDst;
    private Path successPath;
    private Path failurePathLeft;
    private Path failurePathRight;
    private float circleValue;
    private ValueAnimator circleAnimator;
    private float successValue;
    private float faildLeftValue;
    private float faildRightValue;
    private int progressColor;
    private int successColor;
    private int failedColor;

    public SuccessFaildProgress(Context context) {
        this(context, null);
    }

    public SuccessFaildProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SuccessFaildProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SuccessFaildProgress, defStyleAttr, 0);
        progressWidth = array.getDimension(R.styleable.SuccessFaildProgress_progress_width, 6);
        progressRadius = array.getDimension(R.styleable.SuccessFaildProgress_progress_radius, 50);
        progressColor = array.getColor(R.styleable.SuccessFaildProgress_progress_color,Color.parseColor("#0089f1"));
        successColor = array.getColor(R.styleable.SuccessFaildProgress_success_color,Color.parseColor("#0089f1"));
        failedColor = array.getColor(R.styleable.SuccessFaildProgress_failed_color,Color.parseColor("#778887"));
        int status = array.getInt(R.styleable.SuccessFaildProgress_mStatus,0);
        if(status==0){
            mStatus=StatusEnum.Loading;
        }else if(status==1){
            mStatus=StatusEnum.Success;
        }else if(status==2){
            mStatus=StatusEnum.Failed;
        }
        array.recycle();
        initPaint();
        initPath();
        initAnim();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(progressColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(progressWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);    //设置画笔为圆角笔触
    }

    private void initPath() {
        mPathCircle = new Path();
        mPathMeasure = new PathMeasure();
        mPathCircleDst = new Path();
        successPath = new Path();
        failurePathLeft = new Path();
        failurePathRight = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(getPaddingLeft(), getPaddingTop());   //将当前画布的点移到getPaddingLeft,getPaddingTop,后面的操作都以该点作为参照点
        if (mStatus == StatusEnum.Loading) {    //正在加载
            if (startAngle == minAngle) {
                sweepAngle += 6;
            }
            if (sweepAngle >= 300 || startAngle > minAngle) {
                startAngle += 6;
                if (sweepAngle > 20) {
                    sweepAngle -= 6;
                }
            }
            if (startAngle > minAngle + 300) {
                startAngle %= 360;
                minAngle = startAngle;
                sweepAngle = 20;
            }
            canvas.rotate(curAngle += 4, progressRadius, progressRadius);  //旋转的弧长为4
            canvas.drawArc(new RectF(progressWidth/2, progressWidth/2, progressRadius * 2-progressWidth/2, progressRadius * 2-progressWidth/2),
                    startAngle, sweepAngle, false, mPaint);
            invalidate();
        } else if (mStatus == StatusEnum.Success) {
            mPathCircleDst.reset();
            //硬件加速bug
            mPathCircleDst.lineTo(0,0);
            mPaint.setColor(successColor);
            mPathCircle.addCircle(getWidth()/2, getHeight()/2, progressRadius, Path.Direction.CCW);
            mPathMeasure.setPath(mPathCircle, false);
            mPathMeasure.getSegment(0, circleValue*mPathMeasure.getLength(), mPathCircleDst, true);
            canvas.drawPath(mPathCircleDst, mPaint);
            if(circleValue==1){
                successPath.moveTo(getWidth() / 7 * 3, getWidth() / 2);
                successPath.lineTo(getWidth() / 2, getWidth() / 5 * 3);
                successPath.lineTo(getWidth() / 3 * 2, getWidth() / 5 * 2);
                mPathMeasure.nextContour();
                mPathMeasure.setPath(successPath,false);
                mPathMeasure.getSegment(0,successValue*mPathMeasure.getLength(),mPathCircleDst,true);
                canvas.drawPath(mPathCircleDst,mPaint);
            }
        }else if(mStatus == StatusEnum.Failed){
            mPathCircleDst.reset();
            //硬件加速bug
            mPathCircleDst.lineTo(0,0);
            mPaint.setColor(failedColor);
            mPathCircle.addCircle(getWidth()/2,getHeight()/2,progressRadius, Path.Direction.CCW);
            mPathMeasure.setPath(mPathCircle,false);
            mPathMeasure.getSegment(0,circleValue*mPathMeasure.getLength(),mPathCircleDst,true);
            canvas.drawPath(mPathCircleDst,mPaint);
            if(circleValue==1){
                failurePathRight.moveTo(getWidth()/2+progressRadius/2,getHeight()/2-progressRadius/2);
                failurePathRight.lineTo(getWidth()/2-progressRadius/2,getHeight()/2+progressRadius/2);
                mPathMeasure.nextContour();
                mPathMeasure.setPath(failurePathRight,false);
                mPathMeasure.getSegment(0,faildRightValue*mPathMeasure.getLength(),mPathCircleDst,true);
                canvas.drawPath(mPathCircleDst,mPaint);
            }
            if (faildRightValue==1){
                failurePathLeft.moveTo(getWidth()/2-progressRadius/2,getHeight()/2-progressRadius/2);
                failurePathLeft.lineTo(getWidth()/2+progressRadius/2,getHeight()/2+progressRadius/2);
                mPathMeasure.nextContour();
                mPathMeasure.setPath(failurePathLeft,false);
                mPathMeasure.getSegment(0,faildLeftValue*mPathMeasure.getLength(),mPathCircleDst,true);
                canvas.drawPath(mPathCircleDst,mPaint);
            }
        }
    }

    private void initAnim() {
        circleAnimator = ValueAnimator.ofFloat(0, 1);
        circleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                circleValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
    }

    private void startSuccessAnim() {
        ValueAnimator success = ValueAnimator.ofFloat(0f, 1.0f);
        success.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                successValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        //组合动画,一先一后执行
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(success).after(circleAnimator);
        animatorSet.setDuration(500);
        animatorSet.start();
    }

    private void startFaildAnim(){
        ValueAnimator faild = ValueAnimator.ofFloat(0,1);
        faild.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                faildRightValue = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        ValueAnimator faildLeft = ValueAnimator.ofFloat(0,1);
        faildLeft.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                faildLeftValue = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        //
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(faild).after(circleAnimator).before(faildLeft);
//        animatorSet.play(faildLeft).after(faild);
        animatorSet.setDuration(500);
        animatorSet.start();
    }

    public void loadLoading() {
        setStatus(StatusEnum.Loading);
        invalidate();
    }

    public void loadSuccess(){
        setStatus(StatusEnum.Success);
        startSuccessAnim();
    }

    public void loadFaild(){
        setStatus(StatusEnum.Failed);
        startFaildAnim();
    }

    public void setStatus(StatusEnum status) {
        mStatus = status;
    }

    public enum StatusEnum {
        Loading, Success, Failed
    }
}

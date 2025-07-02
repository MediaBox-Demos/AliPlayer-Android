package com.aliyun.player.alivcplayerexpand.view.function;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;

import android.util.AttributeSet;

import com.aliyun.player.alivcplayerexpand.util.DensityUtil;
import com.aliyun.player.alivcplayerexpand.R;

/**
 * 用于展示视频广告的seekBar
 *
 * @author hanyu
 */
/****
 * seekBar for displaying video ads
 *
 * @author hanyu
 */
public class MutiSeekBarView extends AppCompatSeekBar {

    /**
     * 画笔
     */
    /****
     * paint
     */
    private Paint mPaint;

    /**
     * View的宽度
     */
    /****
     * view width
     */
    private int mViewWidth;

    /**
     * 绘制进度条的Y坐标
     */
    /****
     * draw progress bar Y coordinate
     */
    private int mPointY;

    /**
     * 视频广告要展示的位置
     */
    /****
     * video ad position
     */
    private AdvPosition mAdvPosition;

    /**
     * 广告时长
     */
    /****
     * video ad time
     */
    private long mAdvTime;

    /**
     * 需要添加的广告视频的个数
     */
    /****
     * video ad number
     */
    private int mAdvNumber;

    /**
     * 原视频时长
     */
    /****
     * video ad duration
     */
    private long mSourceTime;

    /**
     * 总时长（广告视频+原视频）
     */
    /****
     * Total duration (advertisement video + original video)
     */
    private long mTotalTime;

    private int mPaintStrokeWidth = 2;

    /**
     * 原视频进度条颜色,默认白色
     */
    /****
     * Source progress bar color, default white
     */
    private int mSourceSeekColor = getResources().getColor(R.color.alivc_common_font_white_light);

    /**
     * 视频进度条颜色,默认蓝色
     */
    /****
     * Video progress bar color, default blue
     */
    private int mAdvSeekColor = getResources().getColor(R.color.alivc_player_theme_blue);
    private int mAdvWidth;
    private int mSourceWidth;
    private int mPaddingRight;
    private int mPaddingLeft;


    public MutiSeekBarView(Context context) {
        super(context);
        init();
    }

    public MutiSeekBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MutiSeekBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintStrokeWidth = DensityUtil.dip2px(getContext(), 2);
        mPaint.setStrokeWidth(mPaintStrokeWidth);
        mPaddingLeft = getPaddingLeft();
        mPaddingRight = getPaddingRight();

    }

    /**
     * 设置原视频进度条颜色
     */
    /****
     * Set the color of the original video progress bar
     */
    public void setSourceSeekColor(int color) {
        this.mSourceSeekColor = color;
    }

    /**
     * 设置视频广告进度条颜色
     */
    /****
     * Set the color of the video ad progress bar
     */
    public void setAdvSeekColor(int color) {
        this.mAdvSeekColor = color;
    }

    /**
     * 设置时长
     */
    /****
     * Set the time
     */
    public void setTime(long advTime, long sourceTime, AdvPosition advPosition) {
        this.mAdvTime = advTime;
        this.mAdvPosition = advPosition;
        this.mSourceTime = sourceTime;

        switch (advPosition) {
        case ALL:
            mAdvNumber = 3;
            break;
        case START_END:
        case START_MIDDLE:
        case MIDDLE_END:
            mAdvNumber = 2;
            break;
        case ONLY_START:
        case ONLY_MIDDLE:
        case ONLY_END:
            mAdvNumber = 1;
            break;
        default:
            mAdvNumber = 0;
        }

        //计算比例
        //Calculate ratio
        calculateScale();
        calculateWidth();
        //重新绘制
        //redraw
        invalidate();
    }

    /**
     * 计算比例
     */
    /****
     * Calculate ratio
     */
    private void calculateScale() {
        //计算总时长
        //Calculate total time
        mTotalTime = calculateTotal();
    }

    /**
     * 计算视频广告和原视频所需展示的宽度
     */
    /****
     * Calculate the width of the video ad and the original video to be displayed
     */
    public void calculateWidth() {
        if (mTotalTime == 0) {
            return;
        }
        /*
            需要算广告视频的宽度是多少

            广告时长 / 总时长 * 控件的总宽度 = 广告的宽度
         */
        /*
            The width of the advertisement video needs to be calculated as much
            The advertisement time / total time * the total width of the control = the width of the advertisement video
         */
        mAdvWidth = (int) (((mViewWidth - mPaddingRight - mPaddingLeft) * mAdvTime / mTotalTime));
        mSourceWidth = (int) (((mViewWidth - mPaddingRight - mPaddingLeft) * mSourceTime / mTotalTime));
        invalidate();
    }

    /**
     * 计算总时长,包括视频广告总时长和原视频时长
     */
    /****
     * Calculate the total time, including the total time of the video ad and the original video
     */
    private long calculateTotal() {
        if (mAdvPosition == null) {
            return 0;
        }
        long totalTime = mAdvNumber * mAdvTime + mSourceTime;
        setMax((int) totalTime);
        setCurrentProgress(0);
        //视频广告时长 * 个数 + 原视频长度
        //Video Ad Duration * Number + Original Video Length
        return mAdvNumber * mAdvTime + mSourceTime;
    }

    /**
     * 设置当前进度
     */
    /****
     * Set the current progress
     */
    public void setCurrentProgress(int currentProgress) {
        setProgress(currentProgress);
    }

    /**
     * 判断当前播放进度是否在起始广告位置
     */
    /****
     * Determine whether the current playback progress is in the start ad position
     */
    private boolean isVideoPositionInStart(long mVideoPosition) {
        return mVideoPosition >= 0 && mVideoPosition <= mAdvTime;
    }

    /**
     * 判断是否进度实在开始位置和中间位置
     * 只适用于 ALL 情况下
     */
    /****
     * Determine whether the progress is in the start position and the middle position
     * It only applies to the ALL situation
     */
    private boolean betweenStartAndMiddle(int mVideoPosition) {
        return mVideoPosition > mAdvTime && mVideoPosition < mSourceTime / 2 + mAdvTime;
    }

    /**
     * 判断是否进度在中间位置和结束位置
     */
    /****
     * Determine whether the progress is in the middle position and the end position
     */
    private boolean betweenMiddleAndEnd(int mVideoPosition) {
        return mVideoPosition > mSourceTime / 2 + mAdvTime * 2 && mVideoPosition < mSourceTime + mAdvTime * 2;
    }

    /**
     * 判断是否是在中间广告位置之前
     */
    /****
     * Determine whether it is in the middle ad position before
     */
    private boolean inVideoPositionBeforeMiddle(int mVideoPosition) {
        if (mAdvPosition == MutiSeekBarView.AdvPosition.ALL
                || mAdvPosition == MutiSeekBarView.AdvPosition.START_MIDDLE) {
            return (mVideoPosition >= mSourceTime / 2 + mAdvTime) && (mVideoPosition <= mSourceTime / 2 + mAdvTime * 2);
        } else if (mAdvPosition == AdvPosition.START_END || mAdvPosition == AdvPosition.ONLY_START || mAdvPosition == AdvPosition.ONLY_END) {
            return false;
        } else {
            //ONLY_MIDDLE,MIDDLE_END
            return mVideoPosition >= mSourceTime / 2 && mVideoPosition <= mSourceTime / 2 + mAdvTime;
        }
    }

    /**
     * 判断当前播放进度是否在中间广告位置
     */
    /****
     * Determine whether the current playback progress is in the middle ad position
     */
    private boolean isVideoPositionInMiddle(long mVideoPosition) {
        if (mAdvPosition == MutiSeekBarView.AdvPosition.ALL
                || mAdvPosition == MutiSeekBarView.AdvPosition.START_MIDDLE) {
            return (mVideoPosition >= mSourceTime / 2 + mAdvTime) && (mVideoPosition <= mSourceTime / 2 + mAdvTime * 2);
        } else if (mAdvPosition == AdvPosition.START_END || mAdvPosition == AdvPosition.ONLY_START || mAdvPosition == AdvPosition.ONLY_END) {
            return false;
        } else {
            //ONLY_MIDDLE,MIDDLE_END
            return mVideoPosition >= mSourceTime / 2 && mVideoPosition <= mSourceTime / 2 + mAdvTime;
        }
    }

    /**
     * 判断当前播放进度是否在结束广告位置
     */
    /****
     * Determine whether the current playback progress is in the end ad position
     */
    private boolean isVideoPositionInEnd(long mVideoPosition) {
        if (mAdvPosition == MutiSeekBarView.AdvPosition.ALL
                || mAdvPosition == MutiSeekBarView.AdvPosition.START_MIDDLE) {
            return mVideoPosition >= mSourceTime + mAdvTime * 2;
        } else if (mAdvPosition == MutiSeekBarView.AdvPosition.ONLY_START
                   || mAdvPosition == MutiSeekBarView.AdvPosition.ONLY_MIDDLE
                   || mAdvPosition == MutiSeekBarView.AdvPosition.START_END
                   || mAdvPosition == MutiSeekBarView.AdvPosition.MIDDLE_END) {
            return mVideoPosition >= mSourceTime + mAdvTime;
        } else {
            return mVideoPosition >= mSourceTime;
        }
    }

    /**
     * 根据目标seek的位置，判断应该从哪个位置开始播放
     *
     * @param seekToPosition 目标seek位置
     * @return 应该播放的seek位置
     */
    /****
     * Determine the seek position that should be played according to the target seek position
     *
     * @param seekToPosition Target seek position
     * @return The seek position that should be played
     */
    public long startPlayPosition(long seekToPosition) {
        long startPlayPosition = seekToPosition;
        switch (mAdvPosition) {
        case ONLY_START:
            if (isVideoPositionInStart(seekToPosition)) {
                startPlayPosition = 0;
            }
            break;
        case ONLY_MIDDLE:
            if (isVideoPositionInMiddle(seekToPosition)) {
                startPlayPosition = mSourceTime / 2;
            }
            break;
        case ONLY_END:
            if (isVideoPositionInEnd(seekToPosition)) {
                startPlayPosition = mSourceTime;
            }
            break;
        case START_END:
            if (isVideoPositionInStart(seekToPosition)) {
                startPlayPosition = 0;
            } else if (isVideoPositionInEnd(seekToPosition)) {
                startPlayPosition = mSourceTime + mAdvTime;
            } else {
                startPlayPosition = seekToPosition;
            }
            break;
        case MIDDLE_END:
            if (isVideoPositionInMiddle(seekToPosition)) {
                startPlayPosition = mSourceTime / 2;
            } else if (isVideoPositionInEnd(seekToPosition)) {
                startPlayPosition = mSourceTime + mAdvTime;
            } else {
                startPlayPosition = seekToPosition;
            }
            break;
        case START_MIDDLE:
            if (isVideoPositionInStart(seekToPosition)) {
                startPlayPosition = 0;
            } else if (isVideoPositionInMiddle(seekToPosition)) {
                startPlayPosition = mSourceTime / 2 + mAdvTime;
            } else {
                startPlayPosition = seekToPosition;
            }
            break;
        case ALL:
            if (isVideoPositionInStart(seekToPosition)) {
                startPlayPosition = 0;
            } else if (isVideoPositionInMiddle(seekToPosition)) {
                startPlayPosition = mSourceTime / 2 + mAdvTime;
            } else if (isVideoPositionInEnd(seekToPosition)) {
                startPlayPosition = mSourceTime + mAdvTime * 2;
            } else {
                startPlayPosition = seekToPosition;
            }
            break;
        default:
            break;

        }
        return startPlayPosition;
    }


    /**
     * 获取seek后需要播放的视频
     */
    /****
     * Get the video to be played after seek
     */
    public AdvVideoView.IntentPlayVideo getIntentPlayVideo(int currentPosition, int seekToPosition) {
        if (isVideoPositionInStart(seekToPosition)) {
            return AdvVideoView.IntentPlayVideo.START_ADV;
        } else if (isVideoPositionInMiddle(seekToPosition)) {
            return AdvVideoView.IntentPlayVideo.MIDDLE_ADV;
        } else if (betweenStartAndMiddle(currentPosition) && betweenMiddleAndEnd(seekToPosition)) {
            //起始位置在1，2之间,seekTo到2，3之间的时候
            //The starting position is between 1 and 2, when seekTo is between 2 and 3.
            return AdvVideoView.IntentPlayVideo.MIDDLE_ADV_SEEK;
        } else if (isVideoPositionInEnd(seekToPosition) && betweenMiddleAndEnd(currentPosition)) {
            //起始位置在2，3之间,seekTo到末尾的视频广告处时
            //The starting position is between 2 and 3, when seekTo is at the end of the video ad
            return AdvVideoView.IntentPlayVideo.END_ADV;
        } else if (betweenStartAndMiddle(currentPosition) && betweenMiddleAndEnd(seekToPosition)) {
            //起始位置是1，2之间，seekTo的位置是2,3之间
            //The starting position is between 1 and 2, and the seekTo position is between 2 and 3.
            return AdvVideoView.IntentPlayVideo.MIDDLE_ADV_SEEK;
        } else if (betweenStartAndMiddle(currentPosition) && isVideoPositionInEnd(seekToPosition)) {
            //起始位置是1，2之间,seekTo的位置是末尾广告位置
            //The starting position is between 1 and 2, and the seekTo position is at the end of the ad
            return AdvVideoView.IntentPlayVideo.MIDDLE_END_ADV_SEEK;
        } else if (betweenStartAndMiddle(seekToPosition) && betweenStartAndMiddle(seekToPosition)) {
            //起始位置是3，4之间,seekTo位置是1,2之间
            //The starting position is between 3 and 4, and the seekTo position is between 1 and 2.
            return AdvVideoView.IntentPlayVideo.REVERSE_SOURCE;
        } else {
            return AdvVideoView.IntentPlayVideo.NORMAL;
        }
    }


    /**
     * 绘制原视频线条
     *
     * @param startX X其实位置
     * @param endX   X的结束位置
     * @param canvas canvas
     */
    /****
     * Draw the original video line
     *
     * @param startX X real position
     * @param endX   X end position
     * @param canvas canvas
     */
    private void drawSourceLine(int startX, int endX, Canvas canvas) {
        mPaint.setColor(mSourceSeekColor);
        canvas.drawLine(startX, mPointY, endX, mPointY, mPaint);
    }

    /**
     * 绘制广告视频线
     *
     * @param startX X其实位置
     * @param endX   X的结束位置
     * @param canvas canvas
     */
    /****
     * Draw the ad video line
     *
     * @param startX X real position
     * @param endX   X end position
     * @param canvas canvas
     */
    private void drawAdvLine(int startX, int endX, Canvas canvas) {
        mPaint.setColor(mAdvSeekColor);
        canvas.drawLine(startX, mPointY, endX, mPointY, mPaint);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        int mViewHeight = bottom - top;
        mViewWidth = (right - left);


        mPointY = mViewHeight / 2;

        calculateWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mAdvPosition == null) {
            return;
        }
        switch (mAdvPosition) {
        case ONLY_START:
            //只有开始位置有广告
            //Only the start position has ads
            //开始位置绘制广告
            //Drawing advertisements at the start position
            drawAdvLine(mPaddingLeft, mAdvWidth + mPaddingLeft, canvas);
            //再绘制原视频
            //Draw the original video
            drawSourceLine(mAdvWidth + mPaddingLeft, mAdvWidth + mSourceWidth + mPaddingLeft, canvas);
            break;
        case ONLY_MIDDLE:
            //只有中间位置有广告
            //Only the middle position has ads
            //开始绘制原视频(一半)
            //Draw the original video (half)
            drawSourceLine(mPaddingLeft, mSourceWidth / 2 + mPaddingLeft, canvas);
            //在中间位置绘制广告
            //Draw the advertisement at the middle position
            drawAdvLine(mSourceWidth / 2 + mPaddingLeft, mSourceWidth / 2 + mAdvWidth + mPaddingLeft, canvas);
            //再开始绘制原视频剩下的长度
            //Draw the remaining length of the original video
            drawSourceLine(mSourceWidth / 2 + mAdvWidth + mPaddingLeft, mSourceWidth + mAdvWidth + mPaddingLeft, canvas);
            break;
        case ONLY_END:
            //只有结束位置有广告
            //Only the end position has ads
            //开始绘制原视频
            //Draw the original video
            drawSourceLine(mPaddingLeft, mSourceWidth + mPaddingLeft, canvas);
            //结束时绘制视频广告
            //Draw the video ad at the end
            drawAdvLine(mSourceWidth + mPaddingLeft, mSourceWidth + mAdvWidth + mPaddingLeft, canvas);
            break;
        case START_END:
            //开始和结束位置有广告
            //Advertisements at the beginning and end positions
            //开始位置绘制广告视频
            //Draw the video ad at the beginning
            drawAdvLine((int) (getX() + mPaddingLeft), (int) (getX() + mAdvWidth + mPaddingLeft), canvas);
            //绘制原视频
            //Draw the original video
            drawSourceLine(mAdvWidth + mPaddingLeft, mAdvWidth + mSourceWidth + mPaddingLeft, canvas);
            //结束位置绘制广告视频
            //Draw the video ad at the end
            drawAdvLine(mAdvWidth + mSourceWidth + mPaddingLeft, mAdvWidth * 2 + mSourceWidth + mPaddingLeft, canvas);
            break;
        case START_MIDDLE:
            //开始和中间位置有广告
            //Advertisements at the beginning and middle positions
            //开始位置绘制广告视频
            //Draw the video ad at the beginning
            drawSourceLine(mPaddingLeft, mAdvWidth + mPaddingLeft, canvas);
            //绘制原视频的一半
            //Draw the original video (half)
            drawSourceLine(mAdvWidth + mPaddingLeft, mAdvWidth + mSourceWidth / 2 + mPaddingLeft, canvas);
            //绘制广告视频
            //Draw the advertisement
            drawAdvLine(mAdvWidth + mSourceWidth / 2 + mPaddingLeft, mAdvWidth * 2 + mSourceWidth / 2 + mPaddingLeft, canvas);
            //绘制原视频的另一半
            //Draw the original video (half)
            drawSourceLine(mAdvWidth * 2 + mSourceWidth / 2 + mPaddingLeft, mAdvWidth * 2 + mSourceWidth + mPaddingLeft, canvas);
            break;
        case MIDDLE_END:
            //中间和结束位置有广告
            //Advertisements at the middle and end positions
            //开始绘制原视频的一半
            //Draw the original video (half)
            drawSourceLine(mPaddingLeft, mSourceWidth / 2 + mPaddingLeft, canvas);
            //中间位置绘制广告
            //Draw the advertisement
            drawAdvLine(mSourceWidth / 2 + mPaddingLeft, mSourceWidth / 2 + mAdvWidth + mPaddingLeft, canvas);
            //绘制原视频的另一半
            //Draw the original video (half)
            drawSourceLine(mSourceWidth / 2 + mAdvWidth + mPaddingLeft, mSourceWidth + mAdvWidth + mPaddingLeft, canvas);
            //结束位置绘制视频广告
            //Draw the video ad at the end
            drawAdvLine(mSourceWidth + mAdvWidth + mPaddingLeft, mSourceWidth + mAdvWidth * 2 + mPaddingLeft, canvas);
            break;
        case ALL:
            //开始和中间和结束位置都有视频广告
            //All positions have video ads
            //开始位置绘制广告
            //Draw the advertisement at the start position
            drawAdvLine(mPaddingLeft, mAdvWidth + mPaddingLeft, canvas);
            //绘制原视频的一半
            //Draw the original video (half)
            drawSourceLine(mAdvWidth + mPaddingLeft, mAdvWidth + mSourceWidth / 2 + mPaddingLeft, canvas);
            //在中间位置绘制广告视频
            //Draw the video ad at the middle position
            drawAdvLine(mAdvWidth + mSourceWidth / 2 + mPaddingLeft, mAdvWidth * 2 + mSourceWidth / 2 + mPaddingLeft, canvas);
            //绘制原视频的另一半
            //Draw the original video (half)
            drawSourceLine(mAdvWidth * 2 + mSourceWidth / 2 + mPaddingLeft, mAdvWidth * 2 + mSourceWidth + mPaddingLeft, canvas);
            //在结束位置绘制广告视频
            //Draw the video ad at the end
            drawAdvLine(mAdvWidth * 2 + mSourceWidth + mPaddingLeft, mAdvWidth * 3 + mSourceWidth + mPaddingLeft, canvas);
            break;
        default:
            //没有视频广告
            //No video ads
            drawSourceLine(mPaddingLeft, mSourceWidth, canvas);
            break;
        }
        super.onDraw(canvas);
    }


    /**
     * 视频广告位置
     */
    /****
     * Video ad position
     */
    public enum AdvPosition {
        /**
         * 开始位置
         */
        /****
         * Start position
         */
        ONLY_START(0),
        /**
         * 中间位置
         */
        /****
         * Middle position
         */
        ONLY_MIDDLE(1),
        /**
         * 结束位置
         */
        /****
         * End position
         */
        ONLY_END(2),
        /**
         * 开始和结束位置
         */
        /****
         * Start and end position
         */
        START_END(3),
        /**
         * 开始和中间位置
         */
        /****
         * Start and middle position
         */
        START_MIDDLE(4),
        /**
         * 中间和结束位置
         */
        /****
         * Middle and end position
         */
        MIDDLE_END(5),
        /**
         * 所有
         */
        /****
         * All
         */
        ALL(6);

        AdvPosition(int n) {

        }
    }
}

package com.aliyun.player.externalsubtitle.custom_webvtt;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
import android.util.Log;

import com.cicada.player.utils.webVtt.VttContentAttribute;
import com.cicada.player.utils.webVtt.WebVttResolver;

/**
 * 自定义样式 WebVTT 解析器
 * 该类继承自 WebVttResolver，用于在解析字幕时动态修改文本样式（如字体大小、颜色等）。
 * 通过重写 applyTextSpans 方法，可以在原始样式基础上进行增强或覆盖。
 */
public class CustomStyleWebVttResolver extends WebVttResolver {

    private static final String TAG = "CustomStylerResolver";

    private Typeface mTypeface;

    /**
     * 构造函数
     *
     * @param context Android 上下文，用于资源访问和 View 创建
     */
    public CustomStyleWebVttResolver(Context context) {
        super(context);
        initializeFonts(context);
    }


    /**
     * 初始化自定义字体
     * 从 assets/fonts/ 目录下加载指定的 TTF 字体文件，并创建 Typeface 实例。
     * 注意：字体文件需提前放入项目的 AliPlayer-Android/API-Example/ExternalSubtitle/src/main/assets/fonts/ 目录中。
     *
     * @param context 应用上下文
     */
    private void initializeFonts(Context context) {
        // 从 assets/fonts/LongCang.ttf 加载字体
        try {
            mTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/LongCang.ttf");
        } catch (Exception e) {
            Log.e(TAG, "Failed to load font: " + Log.getStackTraceString(e.getCause()));
            mTypeface = Typeface.DEFAULT;
        }
    }


    /**
     * 重写文本样式应用逻辑，实现自定义样式效果
     * 本方法在父类解析出基础样式后调用，允许对字体大小、颜色等属性进行二次处理。
     *
     * @param spannableStringBuilder 用于构建带样式的文本
     * @param vttContentAttribute    当前文本片段的样式属性对象（包含字体、颜色、大小等）
     * @param start                  样式应用的起始位置（包含）
     * @param end                    样式应用的结束位置（不包含）
     */
    @Override
    protected void applyTextSpans(SpannableStringBuilder spannableStringBuilder, VttContentAttribute vttContentAttribute, int start, int end) {
        // 设置
        // 保存原始字体大小（单位：px），用于后续调整
        double originalFontSizePx = vttContentAttribute.fontSizePx;

        // 【自定义逻辑】将字体大小放大为原来的 2 倍
        // 注意：WebVTT 中的 fontSizePx 通常已根据视频高度计算得出，此处为演示目的进行缩放
        vttContentAttribute.fontSizePx = originalFontSizePx * 2;

        // 【自定义逻辑】强制将字体颜色设置为不透明红色（ARGB: 255, 255, 0, 0）
        // 覆盖 WebVTT 原始定义的颜色，适用于高亮或调试场景
        vttContentAttribute.mPrimaryColour = Color.argb(255, 255, 0, 0);

        // 调用父类方法，使用修改后的 vttContentAttribute 应用完整样式
        // 父类内部会处理：字体名称、大小、颜色、粗体、斜体、下划线等
        super.applyTextSpans(spannableStringBuilder, vttContentAttribute, start, end);


        /*
         * 【可选替代方案】如果不修改 vttContentAttribute，而是直接操作 SpannableStringBuilder，
         * 可以取消注释以下代码（但需注意：这样会覆盖父类设置的颜色和大小）：
         *
         * spannableStringBuilder.setSpan(
         *     new ForegroundColorSpan(Color.RED),
         *     start, end,
         *     Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
         * );
         * spannableStringBuilder.setSpan(
         *     new AbsoluteSizeSpan(20), // 单位：px
         *     start, end,
         *     Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
         * );
         * spannableStringBuilder.setSpan(
         *     new RelativeSizeSpan(2.0f), // 相对于 TextView 默认字体的倍数
         *     start, end,
         *     Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
         * );
         */

        // 再叠加自定义字体样式（覆盖父类可能设置的 TypefaceSpan）
        // 使用自定义的 CustomTypefaceSpan，支持直接传入 Typeface 对象
        spannableStringBuilder.setSpan(new CustomTypefaceSpan(mTypeface), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }


    /**
     * 自定义 Typeface Span 类
     * 继承自 MetricAffectingSpan，能够在文本绘制和测量阶段正确应用 Typeface。
     * 解决了标准 TypefaceSpan 无法直接使用 Typeface 对象的问题。
     */
    private static class CustomTypefaceSpan extends MetricAffectingSpan {

        // 要应用的自定义字体
        private final Typeface typeface;

        /**
         * 构造函数
         *
         * @param typeface 要应用的 Typeface 对象（不可为空）
         */
        public CustomTypefaceSpan(Typeface typeface) {
            this.typeface = typeface;
        }

        /**
         * 更新文本绘制状态
         * 在文本实际绘制时调用，设置画笔的字体。
         *
         * @param tp 用于绘制文本的 TextPaint 对象
         */
        @Override
        public void updateDrawState(TextPaint tp) {
            tp.setTypeface(typeface);
        }

        /**
         * 更新文本测量状态
         * 在计算文本布局（如宽度、换行）时调用，确保测量结果与实际绘制一致。
         *
         * @param p 用于测量文本的 TextPaint 对象
         */
        @Override
        public void updateMeasureState(TextPaint p) {
            p.setTypeface(typeface);
        }
    }

}

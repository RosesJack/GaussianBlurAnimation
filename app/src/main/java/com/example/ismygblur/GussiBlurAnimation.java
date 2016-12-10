package com.example.ismygblur;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.PathInterpolator;
import android.widget.ImageView;

import static android.R.attr.height;
import static android.R.attr.scaleWidth;
import static android.R.attr.voiceLanguage;
import static android.content.ContentValues.TAG;


/**
 * auther：wzy
 * date：2016/12/9 00 :33
 * desc:
 */

public class GussiBlurAnimation {


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void startGussiBlurAnimation(Context context, final ImageView out, Bitmap newBitmap, int defaultImage, long duration) {
        newBitmap = getBitmapFitImageView(out, newBitmap);

        Drawable backgroundDrawable = out.getBackground();
        if (backgroundDrawable == null) {
            //第一次加载图片，没有背景
            ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 255);
            valueAnimator.setDuration(duration);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.start();
            final Bitmap finalNewBitmap1 = newBitmap;
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Integer value = (Integer) animation.getAnimatedValue();
                    out.setImageBitmap(finalNewBitmap1);
                    //图片模糊处理
                    Bitmap bulrBitmap = blurBitmap(finalNewBitmap1);
                    out.setBackground(new BitmapDrawable(bulrBitmap));
                    out.setImageAlpha(value);
                    Log.i(TAG, "value是：" + value);
                }
            });
        } else {
            //255不透明  255~0 && 0~255
            ValueAnimator valueAnimator = ValueAnimator.ofInt(255, -255);
            valueAnimator.setDuration(duration);
            final Bitmap finalNewBitmap = newBitmap;
            //图片模糊处理
            final Bitmap bulrBitmap = blurBitmap(finalNewBitmap);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.start();
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Integer value = (Integer) animation.getAnimatedValue();
                    if (value == 0) {
                        out.setImageBitmap(finalNewBitmap);
                        out.setBackground(new BitmapDrawable(bulrBitmap));
                    }
                    if (value < 0) {
                        value = -value;
                    }
                    Log.i(TAG, "value是：" + value);
                    out.setImageAlpha(value);
                }
            });
        }
    }

    /**
     * 得到适应ImageView 长宽的Bitmap
     *
     * @param out
     * @param newBitmap
     * @return
     */
    private static Bitmap getBitmapFitImageView(ImageView out, Bitmap newBitmap) {
        //使新加的图片尺寸适应image大小
        int imageWidth = out.getWidth();
        int imageHeight = out.getHeight();
        int bitmapWidth = newBitmap.getWidth();
        int bitmapHeight = newBitmap.getHeight();
        float heightScale = imageHeight / (bitmapHeight + 0.0f);
        float widthScale = imageWidth / (bitmapWidth + 0.0f);
        if (heightScale > widthScale) {
            newBitmap = fitBitmap(newBitmap, heightScale);
        } else {
            newBitmap = fitBitmap(newBitmap, widthScale);
        }
        return newBitmap;
    }

    /**
     * 对图片进行高斯模糊
     *
     * @param bitmap
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap blurBitmap(Bitmap bitmap) {
        //先将图片缩小
        bitmap = fitBitmap(bitmap, bitmap.getWidth() / 3);
        //Let's create an empty bitmap with the same size of the bitmap we want to blur
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        //Instantiate a new Renderscript
        RenderScript rs = RenderScript.create(App.getContext());

        //Create an Intrinsic Blur Script using the Renderscript
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        //Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);

        //Set the radius of the blur: 0 < radius <= 25
        blurScript.setRadius(25.0f);

        //Perform the Renderscript
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);

        //Copy the final bitmap created by the out Allocation to the outBitmap
        allOut.copyTo(outBitmap);

        //recycle the original bitmap

        //After finishing everything, we destroy the Renderscript.
        rs.destroy();
        //将图片放大
        return fitBitmap(outBitmap, bitmap.getWidth() * 3);
    }

    /**
     * 尺寸压缩，在内存中的大小变化
     *
     * @param target
     * @param newWidth
     * @return
     */
    public static Bitmap fitBitmap(Bitmap target, int newWidth) {
        int width = target.getWidth();
        int height = target.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) newWidth) / width;
        matrix.postScale(scaleWidth, scaleWidth);
        Bitmap bmp = Bitmap.createBitmap(target, 0, 0, width, height, matrix,
                true);
        return bmp;
    }

    /**
     * 尺寸压缩，在内存中的大小变化
     *
     * @param target
     * @param scale
     * @return
     */
    public static Bitmap fitBitmap(Bitmap target, float scale) {
        int width = target.getWidth();
        int height = target.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap bmp = Bitmap.createBitmap(target, 0, 0, width, height, matrix,
                true);
        if (target != null && !target.equals(bmp) && !target.isRecycled()) {
            target.recycle();
            target = null;
        }
        return bmp;
    }


    /**
     * Recycle the userless bitmap
     *
     * @param bitmap
     */
    public static void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
            bitmap = null;
        }
        System.gc();
    }

}

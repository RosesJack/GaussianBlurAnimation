package com.example.ismygblur;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static android.content.ContentValues.TAG;

public class GBlurPic {

    private Bitmap mBitmap;

    private RenderScript mRS;
    private Allocation mInAllocation;
    private Allocation mOutAllocation;
    private ScriptIntrinsicBlur mBlur;

    public GBlurPic(Context context) {
        super();
        this.mRS = RenderScript.create(context);
        this.mBlur = ScriptIntrinsicBlur.create(mRS, Element.U8_4(mRS));
    }

    public Bitmap gBlurBitmap(Bitmap bitmap, float radius) {
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
        mBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
//		mBitmap = bitmap.copy(bitmap.getConfig(), true);
        Log.i(TAG, "mbitmap是：" + mBitmap);

        //压缩图片
//        mBitmap = compressImage(mBitmap);
        mInAllocation = null;
        mOutAllocation = null;
        mInAllocation = Allocation.createFromBitmap(mRS, mBitmap,
                Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        mOutAllocation = Allocation.createTyped(mRS, mInAllocation.getType());

        mBlur.setRadius(radius);
        mBlur.setInput(mInAllocation);
        mBlur.forEach(mOutAllocation);

        mOutAllocation.copyTo(mBitmap);

        return mBitmap;
    }


    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

}

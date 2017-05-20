package com.bhatnagar.arpit.wallet.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

/**
 * Created by Home Laptop on 12-May-17.
 */

public class QRGenerator
{
	public static Bitmap getQRCode(String code, int width) throws WriterException
	{
		BitMatrix result;
		try
		{
			result = new MultiFormatWriter().encode(code,
					BarcodeFormat.QR_CODE, width, width, null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		int w = result.getWidth();
		int h = result.getHeight();
		int[] pixels = new int[w * h];
		for (int y = 0; y < h; y++) {
			int offset = y * w;
			for (int x = 0; x < w; x++) {
				pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
			}
		}
		Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, w, h);
		return bitmap;
	}

	public static int getAppropriateSize(Context context)
	{
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metrics);
		return (int)(metrics.widthPixels*0.8);
	}
}

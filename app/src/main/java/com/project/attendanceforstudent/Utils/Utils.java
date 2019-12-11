package com.project.attendanceforstudent.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.kaopiz.kprogresshud.KProgressHUD;

import java.io.ByteArrayOutputStream;

public class Utils {
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    public static Bitmap getImage(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    public static KProgressHUD loadingIndicator = null;

    public static void showLoadingIndicator(Context context, String message) {
        hideLoadingIndicator();
        if (loadingIndicator == null) {
            loadingIndicator = KProgressHUD.create(context)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel(message)
                    .setMaxProgress(100).show();
        }
    }

    public static void hideLoadingIndicator() {

        if (loadingIndicator != null && loadingIndicator.isShowing()) {
            loadingIndicator.dismiss();
            loadingIndicator = null;
        }
    }

}

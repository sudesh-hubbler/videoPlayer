package com.example.exoplayer;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by hubbler-sudesh on 16/04/2018 AD.
 */

public class Screenshot {

    private static String FolderName = "Hubbler Images";

    public void takeScreenShot() {
//        v.setDrawingCacheEnabled(true);
//        v.buildDrawingCache(true);
//        Bitmap snap = Bitmap.createBitmap(v.getDrawingCache());
//        v.setDrawingCacheEnabled(false);
//        return snap;
//
//        View v1 = PlayerActivity.getWindow().getDecorView().getRootView();
//        v1.setDrawingCacheEnabled(true);
//        Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
//        v1.setDrawingCacheEnabled(false);
    }

//    public static Bitmap takeScreenhotOfRootView(View v){
//        return takeScreenShot(v.getRootView());
//    }

    public static void SaveImageToLocalDirectory(Bitmap bitmap) {

        try {
            File imageFile;
            File dir;

            dir = new File(Environment.getExternalStorageDirectory(),FolderName);

            boolean success = true;
            if (!dir.exists()) {
                success = dir.mkdirs();
            }
            if (success) {
                java.util.Date date = new java.util.Date();
                imageFile = new File(dir.getAbsolutePath()
                        + File.separator
                        + new java.sql.Timestamp(date.getTime()).toString()
                        + "Image.jpg");

                imageFile.createNewFile();
            } else {
                return;
            }
            ByteArrayOutputStream ostream = new ByteArrayOutputStream();

            // save image into gallery
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);

            FileOutputStream fout = new FileOutputStream(imageFile);
            fout.write(ostream.toByteArray());
            fout.close();
            ContentValues values = new ContentValues();

            values.put(MediaStore.Images.Media.DATE_TAKEN,
                    System.currentTimeMillis());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.MediaColumns.DATA,
                    imageFile.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

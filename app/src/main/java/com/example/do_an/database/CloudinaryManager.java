package com.example.do_an.database;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.do_an.database.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class CloudinaryManager {
    private static Cloudinary cloudinary;

    // Khởi tạo Cloudinary
    public static void init(Context context) {
        if (cloudinary == null) {
            cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "dn46brqbw", "YOUR_CLOUD_NAME",
                    "111584918739439", "YOUR_API_KEY",
                    "Tea-Xxrl0dvqpgKTD6hQHWkWWVY", "YOUR_API_SECRET"
            ));
        }
    }

    // Interface callback để nhận kết quả upload
    public interface UploadCallback {
        void onSuccess(String imageUrl);
        void onError(String errorMessage);
    }

    // Phương thức upload ảnh
    public static void uploadImage(Context context, Uri imageUri, UploadCallback callback) {
        new AsyncTask<Void, Void, String>() {
            private String errorMessage;

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    // Lấy đường dẫn thực của ảnh
                    File file = new File(FileUtils.getPath(context, imageUri));
                    Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
                    return uploadResult.get("secure_url").toString();
                } catch (IOException e) {
                    errorMessage = e.getMessage();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String imageUrl) {
                if (imageUrl != null) {
                    callback.onSuccess(imageUrl);
                } else {
                    callback.onError(errorMessage);
                }
            }
        }.execute();
    }
}

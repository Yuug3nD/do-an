package com.example.do_an.database;

import android.util.Log;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.io.File;
import java.util.Map;

public class CloudinaryHelper {
    private static final String CLOUD_NAME = "dn46brqbw";
    private static final String API_KEY = "111584918739439";
    private static final String API_SECRET = "Tea-Xxrl0dvqpgKTD6hQHWkWWVY";

    private Cloudinary cloudinary;

    public CloudinaryHelper() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", CLOUD_NAME,
                "api_key", API_KEY,
                "api_secret", API_SECRET,
                "secure", true
        ));
    }

    public String uploadImage(File file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
            return (String) uploadResult.get("secure_url"); // Trả về URL ảnh
        } catch (Exception e) {
            Log.e("Cloudinary", "Lỗi upload ảnh: " + e.getMessage());
            return null;
        }
    }
}

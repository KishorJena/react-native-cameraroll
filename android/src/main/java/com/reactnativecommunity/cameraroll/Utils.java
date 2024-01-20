package com.reactnativecommunity.cameraroll;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

public class Utils {
    private static Context _context;

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static String getExtension(String mimeType) {
        String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
        return extension;
    }


    public void setContext(Context context){
        _context = context;
    }
    
    public static boolean isAnimatedWebp(Uri imageUri) {
        try (InputStream inputStream = _context.getContentResolver().openInputStream(imageUri)) {
            return isAnimatedWebp(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

     public static boolean isAnimatedWebp(InputStream inputStream) {
        try {
            byte[] header = new byte[4];
            if (inputStream.read(header) != 4 || !isValidHeader(header, "RIFF")) {
                android.util.Log.d("CameraRoll","NO RIFF  ");
                return false;
            }else{
                android.util.Log.d("CameraRoll","YES RIFF  ");
            }
            
            // Skip 4 bytes (File size), we don't need this information for our check
            inputStream.skip(4);
            
            if (inputStream.read(header) != 4 || !isValidHeader(header, "WEBP")) {
                android.util.Log.d("CameraRoll","NO WEBP  ");
                return false;
            }else{
                android.util.Log.d("CameraRoll","YES WEBP  ");

            }

            

            // Skip 18 bytes (VP8X chunk data), we don't need this information for our check
            inputStream.skip(18);

            if (inputStream.read(header) != 4 || !isValidHeader(header, "ANIM")) {
                android.util.Log.d("CameraRoll","No ANIM  ");
                return false;
            }else{
                android.util.Log.d("CameraRoll","YES ANIM  ");
            }

            // If we reached here, it means all checks passed, and it's an animated WebP
            return true;
        } catch (IOException e) {
            // Handle any IO exceptions if needed
            android.util.Log.d("CameraRoll","catch 2  "+e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private static boolean isValidHeader(byte[] header, String expectedHeader) {
        for (int i = 0; i < header.length; i++) {
            if (header[i] != expectedHeader.charAt(i)) {
                return false;
            }
        }
        return true;
    }

}

package com.zaf.waterfly.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;


import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class PermissionUtils {
    public static final int STORAGE_PERMISSION_REQUEST_CODE = 100;

    public static boolean checkPermission(Context mContext) {
        int result = ContextCompat.checkSelfPermission(mContext.getApplicationContext(), READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(mContext.getApplicationContext(), CAMERA);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermission(Activity mContext) {
        ActivityCompat.requestPermissions(mContext, new String[]{READ_EXTERNAL_STORAGE, CAMERA}, STORAGE_PERMISSION_REQUEST_CODE);
    }


    public static boolean checkAndRequestPermissions(final Activity activity, final int requestCode) {
        /*int permissionReadExternalStorage = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionWriteExternalStorage = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCamera = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);

        int permissionFineLocation = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCourseLocation = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCallPhone = ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE);

        int permissionCamera = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        int permissionReadExternalStorage = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionWriteExternalStorage = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
      */
        List<String> listPermissionsNeeded = new ArrayList<>();
       /* if (permissionFineLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permissionCallPhone != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
        }*/
       /* if (permissionCourseLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SYSTEM_ALERT_WINDOW);
        }*/

        /*if (permissionReadExternalStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionWriteExternalStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }*/

        /*
        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (permissionReadExternalStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionWriteExternalStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionCallPhone != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
        }*/
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), requestCode);
            return false;
        }
        return true;
    }
}

package com.zaf.waterfly.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.zaf.waterfly.R;
import com.zaf.waterfly.UserActivity;

public class Util {
    public static final String TAG = "PubNub>> ";

    public static void DEBUG(String sb) {
        //To print the log on debug mode only

        if (sb.length() > 4000) {
            int chunkCount = sb.length() / 4000;
            for (int i = 0; i <= chunkCount; i++) {
                int max = 4000 * (i + 1);
                if (max >= sb.length()) {
                    Log.d(TAG, " >> " + sb.substring(4000 * i));
                } else {
                    Log.d(TAG, " >> " + sb.substring(4000 * i, max));
                }
            }
        } else {
            Log.d(TAG, " >> " + sb.toString());
        }
    }

    public static void showToast(Context activity, String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
    }

    public static void showDialogDoubleButton(Context ctx, String btnCancelTxt, String btnOkTxt, String message, final DialogButtonClick listener) {
        final Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_double_button);
        Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        TextView textMessage = (TextView) dialog.findViewById(R.id.text_message);
        textMessage.setText(message);
        btnCancel.setText(btnCancelTxt);
        btnOk.setText(btnOkTxt);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onOkClick();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void setUserType(Activity mContext, String b) {
        if (mContext == null)
            return;
        AppSharedPrefs prefs = AppSharedPrefs.getInstance(mContext);
        prefs.put(mContext.getString(R.string.key_user_type), b);
    }

    public static String getUserType(Activity context) {
        AppSharedPrefs prefs = AppSharedPrefs.getInstance(context);
        String userType = null;
        userType = (String) prefs.get(context.getString(R.string.key_user_type));
        if (userType == null) {
            return null;
        }
        return userType;
    }

    @SuppressLint("MissingPermission")
    public static Location getUsersCurrentLocation(UserActivity mContext) {
        final Location[] mLocation = {null};
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, mContext);
        FusedLocationProviderClient flp = LocationServices.getFusedLocationProviderClient(mContext);
        flp.getLastLocation().addOnSuccessListener(mContext, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                mLocation[0] = location;
                Util.DEBUG("User Location: "+location.toString());
            }
        });
        return mLocation[0];
    }
}

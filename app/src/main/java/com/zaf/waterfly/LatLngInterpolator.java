package com.zaf.waterfly;

import com.google.android.gms.maps.model.LatLng;

/*
       This interface defines the interpolate method that allows us to get LatLng coordinates for
       a location a fraction of the way between two points. It also utilizes a Linear method, so
       that paths are linear, as they should be in most streets.
    */
public interface LatLngInterpolator {

    LatLng interpolate(float fraction, LatLng a, LatLng b);

    class LinearFixed implements LatLngInterpolator {
        @Override
        public LatLng interpolate(float fraction, LatLng a, LatLng b) {
            double lat = (b.latitude - a.latitude) * fraction + a.latitude;
            double lngDelta = b.longitude - a.longitude;
            if (Math.abs(lngDelta) > 180) {
                lngDelta -= Math.signum(lngDelta) * 360;
            }
            double lng = lngDelta * fraction + a.longitude;
            return new LatLng(lat, lng);
        }
    }
}

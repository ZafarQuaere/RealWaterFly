package com.zaf.waterfly;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.pubnub.api.models.consumer.pubsub.PNSignalResult;
import com.pubnub.api.models.consumer.pubsub.message_actions.PNMessageActionResult;
import com.pubnub.api.models.consumer.pubsub.objects.PNMembershipResult;
import com.pubnub.api.models.consumer.pubsub.objects.PNSpaceResult;
import com.pubnub.api.models.consumer.pubsub.objects.PNUserResult;
import com.zaf.waterfly.user.UserRegisterActivity;
import com.zaf.waterfly.util.Constants;
import com.zaf.waterfly.util.DialogButtonClick;
import com.zaf.waterfly.util.JsonUtil;
import com.zaf.waterfly.util.Util;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class UserActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private SupportMapFragment mMapFragment; // MapView UI element
    private GoogleMap gMap; // object that represents googleMap and allows us to use Google Maps API features
    private Marker driverMarker; // Marker to display driver's location
    private DrawerLayout drawer;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_nav_drawer);
        mContext  = this;
        initNavigation();

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.userMap);
        mMapFragment.getMapAsync(this);
        initAd();
    }

    private void initNavigation() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
         drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void openDrawer() {
        drawer.openDrawer(GravityCompat.START);
    }

    private void closeDrawer() {
        if (drawer != null ){
            drawer.closeDrawer(GravityCompat.START);
        } else {
            DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }


    private void initAd() {
        AdView mAdView = (AdView) findViewById(R.id.adViewUser);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.bringToFront();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapFragment.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapFragment.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapFragment.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapFragment.onLowMemory();
    }


    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions mk = new MarkerOptions();
        mk.position(latLng);
        gMap.addMarker(mk);
        gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        gMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            gMap = googleMap;
            gMap.setMyLocationEnabled(true);
            gMap.setMinZoomPreference(12F);
            gMap.setIndoorEnabled(true);
            UiSettings uiSettings  = gMap.getUiSettings();
            uiSettings.setIndoorLevelPickerEnabled(true);
            uiSettings.setMyLocationButtonEnabled(true);
            uiSettings.setMapToolbarEnabled(true);
            uiSettings.setCompassEnabled(true);
            uiSettings.setZoomControlsEnabled(true);
            uiSettings.setZoomGesturesEnabled(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        // This code adds the listener and subscribes passenger to channel with driver's location.
        MainActivity.pubnub.addListener(new SubscribeCallback() {
            @Override
            public void status(@NotNull PubNub pubnub, @NotNull PNStatus pnStatus) {  }

            @Override
            public void message(@NotNull PubNub pubnub, @NotNull final PNMessageResult message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Map<String, String> newLocation = JsonUtil.fromJson(message.getMessage().toString(), LinkedHashMap.class);
                            updateUI(newLocation);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void presence(@NotNull PubNub pubnub, @NotNull PNPresenceEventResult pnPresenceEventResult) { }
            @Override
            public void signal(@NotNull PubNub pubnub, @NotNull PNSignalResult pnSignalResult) {}
            @Override
            public void user(@NotNull PubNub pubnub, @NotNull PNUserResult pnUserResult) { }
            @Override
            public void space(@NotNull PubNub pubnub, @NotNull PNSpaceResult pnSpaceResult) {}
            @Override
            public void membership(@NotNull PubNub pubnub, @NotNull PNMembershipResult pnMembershipResult) { }
            @Override
            public void messageAction(@NotNull PubNub pubnub, @NotNull PNMessageActionResult pnMessageActionResult) {}
        });
        MainActivity.pubnub.subscribe()
                .channels(Arrays.asList(Constants.PUBNUB_CHANNEL_NAME))// subscribe to channels
                .execute();
    }

    /*
       This method gets the new location of driver and calls method animateCar
       to move the marker slowly along linear path to this location.
       Also moves camera, if marker is outside of map bounds.
    */
    private void updateUI(final Map<String, String> newLoc) {
        LatLng newLocation = new LatLng(Double.parseDouble(newLoc.get("lat")), Double.parseDouble(newLoc.get("lng")));
        if (driverMarker != null) {
            animateCar(newLocation);
            boolean contains = gMap.getProjection()
                    .getVisibleRegion()
                    .latLngBounds
                    .contains(newLocation);
            if (!contains) {
                gMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
            }
        } else {
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    newLocation, 15.5f));
            driverMarker = gMap.addMarker(new MarkerOptions().position(newLocation).
                    icon(BitmapDescriptorFactory.fromResource(R.drawable.car1)));
            driverMarker.setTitle(newLoc.get("mobile"));
            driverMarker.setTag("300 mts");
            gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Util.showToast(mContext,marker.getTitle() );
                    Intent intent  = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse(newLoc.get("mobile")));
                    startActivity(intent);
                    return false;
                }
            });
        }
    }


    /*
       Animates car by moving it by fractions of the full path and finally moving it to its
       destination in a duration of 5 seconds.
    */
    private void animateCar(final LatLng destination) {
        final LatLng startPosition = driverMarker.getPosition();
        final LatLng endPosition = new LatLng(destination.latitude, destination.longitude);
        final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(3000); // duration 5 seconds
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                try {
                    float v = animation.getAnimatedFraction();
                    LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                    driverMarker.setPosition(newPosition);
                } catch (Exception ex) {
                }
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });
        valueAnimator.start();
    }


    public void logoutClick(View view) {
        closeDrawer();
        Util.showDialogDoubleButton(this, "Cancel", "Ok", getString(R.string.do_you_really_want_to_logout), new DialogButtonClick() {
            @Override
            public void onOkClick() {
                    //TODO
            }

            @Override
            public void onCancelClick() {

            }
        });
    }

    public void registerClick(View view) {
        closeDrawer();
        startActivity(new Intent(this, UserRegisterActivity.class));
    }

    public void rateUsClick(View view) {
        closeDrawer();
    }
}
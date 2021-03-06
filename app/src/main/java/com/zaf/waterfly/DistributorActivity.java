package com.zaf.waterfly;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.zaf.waterfly.user.UserRegisterActivity;
import com.zaf.waterfly.util.Constants;
import com.zaf.waterfly.util.DialogButtonClick;
import com.zaf.waterfly.util.Util;

import java.util.LinkedHashMap;

public class DistributorActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private FusedLocationProviderClient mFusedLocationClient; // Object used to receive location updates
    private LocationRequest locationRequest; // Object that defines important parameters regarding location request.

    private SupportMapFragment distributorMap; // MapView UI element
    private GoogleMap driverMap; // object that represents googleMap and allows us to use Google Maps API features
    private AdView mAdView;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distributor);

        updateUI();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(3000); // 3 second delay between each request
        locationRequest.setFastestInterval(3000); // 3 seconds fastest time in between each request
        locationRequest.setSmallestDisplacement(5); // 5 meters minimum displacement for new location request
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // enables GPS high accuracy location requests

        sendUpdatedLocationMessage();
    }

    private void initAd() {
        mAdView = (AdView) findViewById(R.id.adViewDistributor);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.bringToFront();
    }

    private void sendUpdatedLocationMessage() {
        try {
            mFusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

                    Location location = locationResult.getLastLocation();
                    LinkedHashMap<String, String> message = getNewLocationMessage(location.getLatitude(), location.getLongitude());
                    MainActivity.pubnub.publish()
                            .message(message)
                            .channel(Constants.PUBNUB_CHANNEL_NAME)
                            .async(new PNCallback<PNPublishResult>() {
                                @Override
                                public void onResponse(PNPublishResult result, PNStatus status) {
                                    // handle publish result, status always present, result if successful
                                    // status.isError() to see if error happened
                                    if (!status.isError()) {
                                        Util.DEBUG(" pub timetoken: " + result.getTimetoken());
                                    }
                                    Util.DEBUG(" pub status code: " + status.getStatusCode());
                                }
                            });
                }
            }, Looper.myLooper());

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /*
       Helper method that takes in latitude and longitude as parameter and returns a LinkedHashMap representing this data.
       This LinkedHashMap will be the message published by driver.
    */
    private LinkedHashMap<String, String> getNewLocationMessage(double lat, double lng) {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        map.put("lat", String.valueOf(lat));
        map.put("lng", String.valueOf(lng));
        map.put("mobile", "7834908329");
        return map;
    }


    private void updateUI() {
        distributorMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.distributorMap);
        distributorMap.getMapAsync(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.distributorToolbar);
        toolbar.setTitle(R.string.water_fly);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_distributor);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
       /* Toolbar toolbar = findViewById(R.id.distributorToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });*/

    }

    private void openDrawer() {
        drawer.openDrawer(GravityCompat.START);
    }

    private void closeDrawer() {
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
            drawerLayout.closeDrawer(GravityCompat.START);
        }
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

    @Override
    public void onResume() {
        super.onResume();
        distributorMap.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        distributorMap.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        distributorMap.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        distributorMap.onLowMemory();
    }


    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions mk = new MarkerOptions();
        mk.position(latLng);
        driverMap.addMarker(mk);
        driverMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        driverMap.animateCamera(CameraUpdateFactory.zoomTo(15));
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
            driverMap = googleMap;
            driverMap.setMyLocationEnabled(true);
            driverMap.setMinZoomPreference(12F);
            driverMap.setIndoorEnabled(true);
            UiSettings uiSettings = driverMap.getUiSettings();
            uiSettings.setIndoorLevelPickerEnabled(true);
            uiSettings.setMyLocationButtonEnabled(true);
            uiSettings.setMapToolbarEnabled(true);
            uiSettings.setCompassEnabled(true);
            uiSettings.setZoomControlsEnabled(true);
            uiSettings.setZoomGesturesEnabled(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}
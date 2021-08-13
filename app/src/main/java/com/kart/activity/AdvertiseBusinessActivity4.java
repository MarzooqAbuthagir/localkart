package com.kart.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.kart.R;
import com.kart.model.LocationData;
import com.kart.support.Utilis;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class AdvertiseBusinessActivity4 extends AppCompatActivity implements OnMapReadyCallback, LocationListener,
        GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraMoveCanceledListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private String Tag = "AdvertiseBusinessActivity4";
    Utilis utilis;
    Toolbar toolbar;
    ActionBar actionBar = null;

    String keyIntent = "", strBusinessId = "";

    View mapView;
    public static GoogleMap mMap;
    GoogleApiClient gac;
    LocationRequest locationRequest;
    private static final int REQUEST_CODE = 101;
    String address = "";
    TextView tvAddress;
    double latitude = 0.0;
    double longitude = 0.0;
    float zoomlevel = 11;

    String apiKey = "AIzaSyCAdZwm8yXwsIX-V5aZtXc64Q2xpEbHWio";
    int AUTOCOMPLETE_REQ_CODE = 1;
    List<com.google.android.libraries.places.api.model.Place.Field> fields;

    TextView tvSatellite;
    TextView tvTerrain;
    int mapId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertise_business4);

        utilis = new Utilis(AdvertiseBusinessActivity4.this);

        Intent intent = getIntent();
        keyIntent = intent.getStringExtra("key");
        strBusinessId = intent.getStringExtra("businessType");

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(AdvertiseBusinessActivity4.this, R.color.colorPrimaryDark));

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDefaultDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        TextView toolBarTitle = findViewById(R.id.toolbar_title);
        toolBarTitle.setText("Register Your Business");

        View progressView = findViewById(R.id.progress_view);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(700, 10);
        progressView.setLayoutParams(lp);

        tvAddress = findViewById(R.id.tv_address);

        try {
            SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapView = supportMapFragment.getView();
            supportMapFragment.getMapAsync(AdvertiseBusinessActivity4.this);
        } catch (Exception e) {
            System.out.println("onSuccess Excep " + e.getMessage());
        }

//        fetchLastLocation();
        if (Utilis.isGpsOn()) {
            getLocation();
        } else {
            Toast.makeText(this, "Enable GPS", Toast.LENGTH_SHORT).show();
        }

        if (!Places.isInitialized()) {
            Places.initialize(AdvertiseBusinessActivity4.this, apiKey);
        }

        TextView tvPlacesSearch = findViewById(R.id.tv_places_search);

        tvPlacesSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fields = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);

                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields
                ).build(AdvertiseBusinessActivity4.this);
                startActivityForResult(intent, AUTOCOMPLETE_REQ_CODE);
            }
        });

        tvSatellite = findViewById(R.id.tv_satellite);
        tvSatellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapId = 2;
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        });
        tvTerrain = findViewById(R.id.tv_terrain);


        tvTerrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapId = 1;
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            }
        });

        Button btnPrevious = findViewById(R.id.btn_previous);
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

        Button btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("onnext click");
                System.out.println(tvAddress.getText().toString().trim());
                System.out.println(latitude);
                System.out.println(longitude);
                if (tvAddress.getText().toString().trim().isEmpty()) {
                    Toast.makeText(AdvertiseBusinessActivity4.this, "Address not found", Toast.LENGTH_SHORT).show();
                } else if (latitude == 0.0 || longitude == 0.0) {
                    Toast.makeText(AdvertiseBusinessActivity4.this, "Gps issue, try again", Toast.LENGTH_SHORT).show();
                } else {
                    LocationData locationData = new LocationData(
                            tvAddress.getText().toString().trim(),
                            String.valueOf(latitude),
                            String.valueOf(longitude)
                    );
                    Utilis.saveLocDetails(locationData);
                    Intent intent = new Intent(AdvertiseBusinessActivity4.this, AdvertiseBusinessActivity5.class);
                    intent.putExtra("key", keyIntent);
                    intent.putExtra("businessType", strBusinessId);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });
    }

//    private void fetchLastLocation() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
//            return;
//        }
//        if (Utilis.isGpsOn()) {
//            try {
//                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//                mapView = supportMapFragment.getView();
//                supportMapFragment.getMapAsync(AdvertiseBusinessActivity4.this);
//            } catch (Exception e) {
//                System.out.println("onSuccess Excep " + e.getMessage());
//            }
//
//            getAddress(latitude, longitude);
//        }
//
//    }

    private void getAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(AdvertiseBusinessActivity4.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null) {
                Address myAddress = addresses.get(0);
                StringBuilder stringBuilder = new StringBuilder("");
                for (int i = 0; i <= myAddress.getMaxAddressLineIndex(); i++) {
                    if (i == 0)
                        stringBuilder.append(myAddress.getAddressLine(i));
                    else
                        stringBuilder.append("\n").append(myAddress.getAddressLine(i));
                }
                address = stringBuilder.toString();
                tvAddress.setText(address);
            }
        } catch (Exception e) {
            System.out.println("getAddress Exception " + e.getMessage());
        }
    }

    @SuppressLint("RestrictedApi")
    private void getLocation() {
        isGooglePlayServicesAvailable();
        locationRequest = new LocationRequest();
        /* 10 secs */
        long UPDATE_INTERVAL = 2 * 1000;
        locationRequest.setInterval(UPDATE_INTERVAL);
        /* 2 sec */
        long FASTEST_INTERVAL = 2000;
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        gac = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        gac.connect();
    }

    public boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(this, status, 2404).show();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    private void back() {
        LocationData locationData = new LocationData(
                tvAddress.getText().toString().trim(),
                String.valueOf(latitude),
                String.valueOf(longitude)
        );
        Utilis.saveLocDetails(locationData);
        finish();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (Utilis.isGpsOn()) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(AdvertiseBusinessActivity4.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_CODE);
                return;
            }
            Log.d(Tag, "onConnected");
            Location ll = LocationServices.FusedLocationApi.getLastLocation(gac);
            Log.d(Tag, "LastLocation: " + (ll == null ? "NO LastLocation" : ll.toString()));
            LocationServices.FusedLocationApi.requestLocationUpdates(gac, locationRequest, this);
        } else {
            Toast.makeText(this, "Enable GPS", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            updateUI(location);
        }
    }

    private void updateUI(Location location) {
        LocationData locData = Utilis.getLocDetails(AdvertiseBusinessActivity4.this);
        if (locData != null) {
            LatLng latLng = new LatLng(Double.parseDouble(locData.getLatitude()), Double.parseDouble(locData.getLongitude()));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
            latitude = Double.parseDouble(locData.getLatitude());
            longitude = Double.parseDouble(locData.getLongitude());
            tvAddress.setText(locData.getAddress());
            gac.disconnect();
        } else {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            getAddress(latitude, longitude);
            gac.disconnect();
        }
    }

    @Override
    public void onCameraIdle() {
        zoomlevel = mMap.getCameraPosition().zoom;
        latitude = mMap.getCameraPosition().target.latitude;
        longitude = mMap.getCameraPosition().target.longitude;
        getAddress(latitude, longitude);
    }

    @Override
    public void onCameraMoveCanceled() {

    }

    @Override
    public void onCameraMove() {

    }

    @Override
    public void onCameraMoveStarted(int i) {
        switch (i) {
            case GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE:
                //Toast.makeText(this, "The user gestured on the map.",Toast.LENGTH_SHORT).show();
                System.out.println("The user gestured on the map");
                break;
            case GoogleMap.OnCameraMoveStartedListener.REASON_API_ANIMATION:
                //Toast.makeText(this, "The user tapped something on the map.",Toast.LENGTH_SHORT).show();
                System.out.println("The user tapped something on the map");
                break;
            case GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION:
                //Toast.makeText(this, "The app moved the camera.",Toast.LENGTH_SHORT).show();
                System.out.println("The app moved the camera.");
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraMoveCanceledListener(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (mapId == 1) {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        } else {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
//            fetchLastLocation();
            if (Utilis.isGpsOn()) {
                getLocation();
            } else {
                Toast.makeText(this, "Enable GPS", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == AUTOCOMPLETE_REQ_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                tvAddress.setText(place.getAddress());
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;
                mMap.clear();
                LatLng latLng = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                fetchLastLocation();
                if (Utilis.isGpsOn()) {
                    getLocation();
                } else {
                    Toast.makeText(this, "Enable GPS", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
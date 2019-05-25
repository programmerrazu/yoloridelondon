package com.razu.activity;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.razu.Apps;
import com.razu.R;
import com.razu.helper.PreferencesManager;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final float MAP_ZOOM = 14.0f;
    private SupportMapFragment fragmentMaps;
    private GoogleMap maps;
    private Boolean locationPermissionGranted;
    private View btnLocation;
    private FloatingActionButton fabBtnLocation;
    private CardView cardViewSearchContainer;
    private TextView tvCLoc;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    public static final String EXTRA_REVEAL_X = "EXTRA_REVEAL_X";
    public static final String EXTRA_REVEAL_Y = "EXTRA_REVEAL_Y";
    private Boolean doubleBackPressed = false;
    private PreferencesManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        session = new PreferencesManager(this);
        if (savedInstanceState == null) {
            revealAnimation();
        }
        setUIComponent();
    }

    private void revealAnimation() {
        final View mainLayout = findViewById(R.id.main_drawer_layout);
        final Intent intent = getIntent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && intent.hasExtra(EXTRA_REVEAL_X) && intent.hasExtra(EXTRA_REVEAL_Y)) {
            mainLayout.setVisibility(View.INVISIBLE);
            ViewTreeObserver viewTreeObserver = mainLayout.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        revealAnimator(mainLayout);
                        mainLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        } else {
            mainLayout.setVisibility(View.VISIBLE);
        }
    }

    private void revealAnimator(View rootLayout) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            float finalRadius = (float) (Math.max(rootLayout.getWidth(), rootLayout.getHeight()) * 1.1);
            Animator animator = ViewAnimationUtils.createCircularReveal(rootLayout, rootLayout.getMeasuredWidth() / 2, rootLayout.getMeasuredHeight() / 2, 50, finalRadius);
            animator.setDuration(800);
            animator.setInterpolator(new AccelerateInterpolator());
            rootLayout.setVisibility(View.VISIBLE);
            animator.start();
        } else {
            finish();
        }
    }

    private void setUIComponent() {

        //  toolbar = (Toolbar) findViewById(R.id.toolbar_in_maps);
        // setSupportActionBar(toolbar);
        //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);

//        toggle.setDrawerIndicatorEnabled(false);
//        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
//                    drawerLayout.closeDrawer(Gravity.RIGHT);
//                } else if (!drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
//                    drawerLayout.isDrawerOpen(Gravity.RIGHT);
//                }
//              //  drawerLayout.openDrawer(Gravity.RIGHT);
//            }
//        });
        //  toggle.setHomeAsUpIndicator(R.drawable.ic_user);

        fragmentMaps = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_maps);
        assert fragmentMaps != null;
        fragmentMaps.getMapAsync(this);

        fabBtnLocation = (FloatingActionButton) findViewById(R.id.fab_btn_location);
        tvCLoc = (TextView) findViewById(R.id.tv_current_loc);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        maps = googleMap;
        maps.setTrafficEnabled(false);
        maps.setIndoorEnabled(false);
        maps.setBuildingsEnabled(false);
        maps.getUiSettings().setZoomControlsEnabled(false);
        getLocationPermission();
        getLocations();
    }

    private void setMarker(LatLng latLng) {
        maps.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(Gravity.END)) {
                drawerLayout.closeDrawer(Gravity.END);
            } else {
                drawerLayout.openDrawer(Gravity.END);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void getLocations() {
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @SuppressLint("MissingPermission")
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                    maps.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, MAP_ZOOM));

                    setMarker(new LatLng(loc.latitude, loc.longitude));

                    // getAddress(location.getLatitude(), location.getLongitude());
                    maps.setMyLocationEnabled(true);
                    customiseBtnLocation();
                }
            }
        });
    }

    private void customiseBtnLocation() {
        if (maps == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                btnLocation = ((View) fragmentMaps.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                if (btnLocation != null) {
                    btnLocation.setVisibility(View.GONE);
                }
                fabBtnLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (maps != null) {
                            if (btnLocation != null) {
                                btnLocation.callOnClick();
                            }
                        }
                    }
                });
            } else {
                getLocationPermission();
            }
        } catch (SecurityException e) {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0) {
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted) {
                        locationPermissionGranted = true;
                        getLocations();
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel(
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                                                }
                                            }
                                        });
                            }
                        }
                    }
                }
        }
    }

    private void showMessageOKCancel(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage("You need to allow location access permission")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            tvCLoc.setText(obj.getAddressLine(0));

            /* String add = obj.getAddressLine(0);
            add = add + "\n" + obj.getCountryName();
            add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea();
            add = add + "\n" + obj.getPostalCode();
            add = add + "\n" + obj.getSubAdminArea();
            add = add + "\n" + obj.getLocality();
            add = add + "\n" + obj.getSubThoroughfare(); */

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onHome(View view) {
        drawerLayout.closeDrawer(Gravity.END);
        Apps.redirect(MainActivity.this, MainActivity.class);
    }

    public void onOrder(View view) {
        drawerLayout.closeDrawer(Gravity.END);
        Apps.redirect(MainActivity.this, OrderActivity.class);
    }

    public void onPayments(View view) {
        drawerLayout.closeDrawer(Gravity.END);
        Apps.redirect(MainActivity.this, PaymentsActivity.class);
    }

    public void onAbout(View view) {
        drawerLayout.closeDrawer(Gravity.END);
        Apps.redirect(MainActivity.this, AboutActivity.class);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.END)) {
            drawerLayout.closeDrawer(Gravity.END);
        } else {
            if (doubleBackPressed) {
                finish();
            } else {
                doubleBackPressed = true;
                Apps.snackBarMsg(getString(R.string.back_press), drawerLayout, this);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackPressed = false;
                    }
                }, 3000);
            }
        }
    }

    public void onRightDrawer(View view) {
        if (drawerLayout.isDrawerOpen(Gravity.END)) {
            drawerLayout.closeDrawer(Gravity.END);
        } else {
            drawerLayout.openDrawer(Gravity.END);
        }
    }

    public void onSettings(View view) {
        Toasty.info(getApplicationContext(), "Settings coming soon", Toast.LENGTH_LONG, true).show();
    }
}
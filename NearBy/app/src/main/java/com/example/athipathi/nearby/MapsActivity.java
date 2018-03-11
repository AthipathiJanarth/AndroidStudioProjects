package com.example.athipathi.nearby;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,NavigationView.OnNavigationItemSelectedListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    private GoogleMap mMap;
    public static final int REQUEST_LOCATION_CODE=99;
    int PROXIMITY_RADIUS=5000;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toogle;
    NavigationView navigationView;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentLocationMarker;
    double latitude,longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toogle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toogle);
        toogle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mapFragment.getMapAsync(this);
        navigationView = (NavigationView) findViewById(R.id.nav);
        navigationView.setNavigationItemSelectedListener(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            buildGoogleApiClient();

            mMap.setMyLocationEnabled(true);
        }
    /*mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            Intent i=new Intent(MapsActivity.this,PlacesActivity.class);
            startActivity(i);
            return true;
        }
    });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.radiusmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    protected synchronized void buildGoogleApiClient()
    {
        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        client.connect();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toogle.onOptionsItemSelected(item)){
            return true;
        }
        int radiusid=item.getItemId();
        switch (radiusid){
            case R.id.km_5:
                PROXIMITY_RADIUS=5000;
                Toast.makeText(this,"5 km radius is selected",Toast.LENGTH_SHORT).show();
                break;
            case R.id.km_10:
                PROXIMITY_RADIUS=10000;
                Toast.makeText(this,"10 km radius is selected",Toast.LENGTH_SHORT).show();
                break;
            case R.id.km_20:
                PROXIMITY_RADIUS=20000;
                Toast.makeText(this,"20 km radius is selected",Toast.LENGTH_SHORT).show();
                break;
            case R.id.km_30:
                PROXIMITY_RADIUS=30000;
                Toast.makeText(this,"30 km radius is selected",Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        Object dataTransfer[]=new Object[2];
        String url;
        GetNearbyPlacesData getNearbyPlacesData=new GetNearbyPlacesData();
        latitude= lastLocation.getLatitude();
        longitude=lastLocation.getLongitude();
        LatLng latlng=new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
        switch (id)
        {

            case R.id.cafe: {
                mMap.clear();

                url=getUrl(latitude,longitude,"cafe");
                dataTransfer[0]=mMap;
                dataTransfer[1]=url;

                getNearbyPlacesData.execute(dataTransfer);
                MarkerOptions markerOptions= new MarkerOptions();
                markerOptions.position(latlng);
                markerOptions.title("Your location");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                currentLocationMarker=mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                Toast.makeText(this, "Showing Nearby Cafe", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.hotel: {
                mMap.clear();

                url=getUrl(latitude,longitude,"restaurant");
                dataTransfer[0]=mMap;
                dataTransfer[1]=url;

                getNearbyPlacesData.execute(dataTransfer);
                MarkerOptions markerOptions= new MarkerOptions();
                markerOptions.position(latlng);
                markerOptions.title("Your location");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                currentLocationMarker=mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                Toast.makeText(MapsActivity.this,"Showing nearby Restaurant",Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.beach:
            {
                mMap.clear();

                url=getUrl(latitude,longitude,"beach");
                dataTransfer[0]=mMap;
                dataTransfer[1]=url;
                getNearbyPlacesData.execute(dataTransfer);
                MarkerOptions markerOptions= new MarkerOptions();
                markerOptions.position(latlng);
                markerOptions.title("Your location");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                currentLocationMarker=mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                Toast.makeText(this,"Showing nearby Beach",Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.mall: {

                mMap.clear();
                url = getUrl(latitude, longitude,"mall");
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                MarkerOptions markerOptions= new MarkerOptions();
                markerOptions.position(latlng);
                markerOptions.title("Your location");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                currentLocationMarker=mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                Toast.makeText(this, "Showing nearby Mall", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.bar:
            {   mMap.clear();

                url=getUrl(latitude,longitude,"bar");
                dataTransfer[0]=mMap;
                dataTransfer[1]=url;

                getNearbyPlacesData.execute(dataTransfer);
                MarkerOptions markerOptions= new MarkerOptions();
                markerOptions.position(latlng);
                markerOptions.title("Your location");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                currentLocationMarker=mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                Toast.makeText(this,"Showing nearby Bar",Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.theatre:
            {
                mMap.clear();

                url=getUrl(latitude,longitude,"theatre");
                dataTransfer[0]=mMap;
                dataTransfer[1]=url;

                getNearbyPlacesData.execute(dataTransfer);
                MarkerOptions markerOptions= new MarkerOptions();
                markerOptions.position(latlng);
                markerOptions.title("Your location");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                currentLocationMarker=mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                Toast.makeText(this,"Showing nearby Theatre",Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.theme_park:
            { mMap.clear();

                url=getUrl(latitude,longitude,"theme park");
                dataTransfer[0]=mMap;
                dataTransfer[1]=url;
                getNearbyPlacesData.execute(dataTransfer);
                MarkerOptions markerOptions= new MarkerOptions();
                markerOptions.position(latlng);
                markerOptions.title("Your location");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                currentLocationMarker=mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                Toast.makeText(this,"Showing nearby Theme Park",Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.game:
            {
                mMap.clear();

                url=getUrl(latitude,longitude,"game world");
                dataTransfer[0]=mMap;
                dataTransfer[1]=url;

                getNearbyPlacesData.execute(dataTransfer);
                MarkerOptions markerOptions= new MarkerOptions();
                markerOptions.position(latlng);
                markerOptions.title("Your location");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                currentLocationMarker=mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                Toast.makeText(this,"Showing nearby Game Center",Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.park:
            {
                mMap.clear();
                url=getUrl(latitude,longitude,"park");
                dataTransfer[0]=mMap;
                dataTransfer[1]=url;

                getNearbyPlacesData.execute(dataTransfer);

                MarkerOptions markerOptions= new MarkerOptions();
                markerOptions.position(latlng);
                markerOptions.title("Your location");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                currentLocationMarker=mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));

                Toast.makeText(this,"Showing nearby Park",Toast.LENGTH_SHORT).show();
                break;
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    
    private String getUrl(double latitude , double longitude , String nearbyPlace)
    {

        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type="+nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+"AIzaSyD3Yy_oP8Gp6-CEa0gQBjGAnqYgaM6RV1U");
        String c=googlePlaceUrl.toString();
        Log.d("MapsActivity", "url = "+googlePlaceUrl.toString());

        return googlePlaceUrl.toString();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode)
        {
            case REQUEST_LOCATION_CODE:
                if(grantResults.length>0 &&grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    //permission is granted
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
                    {
                        if(client==null)
                        {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else
                {
                    Toast.makeText(this,"Permission Denied",Toast.LENGTH_LONG).show();
                }
                return;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }
    public boolean checkLocationPermission(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_CODE);
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_CODE);
            }
            return false;
        }
        else
            return true;
    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation=location;
        if(currentLocationMarker != null)
        {
            currentLocationMarker.remove();
        }
        LatLng latlng=new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions= new MarkerOptions();
        markerOptions.position(latlng);
        markerOptions.title("Your location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        currentLocationMarker=mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(50));

        if(client != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
        }
    }
}

package com.example.athipathi.nearby;

import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Athipathi on 16-02-2018.
 */

public class GetNearbyPlacesData extends AsyncTask<Object,String,String>{
String googlePlacesData;
GoogleMap mMap;
String url;
    @Override
    protected String doInBackground(Object... objects) {
       mMap=(GoogleMap)objects[0];
       url=(String)objects[1];
       DownloadUrl downloadUrl=new DownloadUrl();
        try {
            googlePlacesData=downloadUrl.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String s) {
     List<HashMap<String,String>>nearbyPlaceList=null;
     Dataparser parser=new Dataparser();
     nearbyPlaceList=parser.parse(s);
     showNearbyPlaces(nearbyPlaceList);
    }
    private  void showNearbyPlaces(List<HashMap<String,String>> nearbyPlace)
    {
        for (int i=0;i<nearbyPlace.size();i++)
        {
            MarkerOptions markerOptions=new MarkerOptions();
            HashMap<String,String>googlePlace=nearbyPlace.get(i);

            String placeName=googlePlace.get("place_name");
            String vicinity=googlePlace.get("vicinity");
            double lat=Double.parseDouble(googlePlace.get("Lat"));
            double lng=Double.parseDouble(googlePlace.get("Lng"));
            LatLng latLng=new LatLng(lat,lng);
            markerOptions.position(latLng);
            markerOptions.title(placeName+" "+vicinity);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(70));

        }
    }
}

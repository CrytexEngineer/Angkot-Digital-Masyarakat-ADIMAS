package com.example.aqil.angkotcustomerside;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private HashMap<String, Marker> mMarkers = new HashMap<>();
    String TAG = MapsActivity.class.getSimpleName();
    List<com.google.maps.model.LatLng> mCapturedLocations;
    List<com.google.maps.model.LatLng> mCapturedLocations2;
    TextView idAngkotBar, tujuanBar;
    RecyclerView rv;
    ArrayList<Angkot> angkotDatabase = new ArrayList<>();
    ArrayList<Angkot> angkots = new ArrayList<>();
    private static final int PERMISSIONS_REQUEST = 1;
    ArrayList<Shelter> shelters = new ShelterList().getShelters();
    AngkotAdapter adapter;
    LatLng currentPoisiton;
    private TextView tvCapacity, tvDistance, tvPlate;
    private Button btnPesan;
    float distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        requetPermission();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        idAngkotBar = (TextView) findViewById(R.id.angkot_id_bar);
        tujuanBar = (TextView) findViewById(R.id.tujuan_bar);
        rv = (RecyclerView) findViewById(R.id.list_angkot_main);
        tvCapacity = (TextView) findViewById(R.id.textview_capacity);

        btnPesan = (Button) findViewById(R.id.button_pesan);
        //tvDistance=(TextView)findViewById(R.id.textview_distance);
        tvPlate = (TextView) findViewById(R.id.textview_plate);
        tvPlate.setVisibility(View.INVISIBLE);
        tvCapacity.setVisibility(View.INVISIBLE);
        btnPesan.setVisibility(View.INVISIBLE);
        adapter = new AngkotAdapter(this,this);


        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        findViewById(R.id.btn_current_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestLocationUpdates();
                Log.d("onClick", "reqLocations");
            }
        });

        requestLocationUpdates();


        btnPesan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              new dialog(MapsActivity.this).show();
            }
        });


    }

    private void initShelters() {

        for (Shelter shelter : shelters) {
            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_shelter)).
                    title(shelter.getName()).position(new LatLng(shelter.getLat(), shelter.getLong()))).setTag("Shelter");

        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap = googleMap;
        mMap.setMaxZoomPreference(16);
        loginToFireBase();
        initShelters();
        startGpx();
    }

    private void loginToFireBase() {
        String email = getString(R.string.firebase_email);
        String password = getString(R.string.firebase_password);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    subscribeToUpdates();
                    Log.d(TAG, "firebase auth success");
                } else {
                    Log.d(TAG, "firebase auth failed");
                }
            }
        });


    }

    private void subscribeToUpdates() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_path));
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                setMarker(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                setMarker(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }

    public void setMarker(final DataSnapshot dataSnapshot) {
        String key = dataSnapshot.getKey();
        HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();
        double lat = 0;
        if (value != null) {
            lat = Double.parseDouble(value.get("latitude").toString());
        }
        assert value != null;
        double lng = Double.parseDouble(value.get("longitude").toString());
        final LatLng location = new LatLng(lat, lng);
        Angkot angkot;
        if (!mMarkers.containsKey(key)) {
            mMarkers.put(key, mMap.addMarker(new MarkerOptions().title(key).position(location)));
            Log.d("TAG", "setMarker: " + "success");

            angkot = new Angkot(dataSnapshot.getKey(),
                    dataSnapshot.child("Tujuan").getValue().toString(),
                    dataSnapshot.child("Penumpang").getValue().toString(),
                    dataSnapshot.child("Nomor Plat").getValue().toString());
            angkotDatabase.add(angkot);
            angkots.add(angkot);

            Log.d("TAG", dataSnapshot.toString());
        } else {
            mMarkers.get(key).setPosition(location);
            Log.d("TAG", "setMarker: " + "alternate");

            double earthRadius = 6371000; //meters
            double dLat = Math.toRadians(mMarkers.get(key).getPosition().latitude - currentPoisiton.latitude);
            double dLng = Math.toRadians(mMarkers.get(key).getPosition().longitude - currentPoisiton.longitude);
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                    Math.cos(Math.toRadians(mMarkers.get(key).getPosition().latitude)) * Math.cos(Math.toRadians(currentPoisiton.latitude)) *
                            Math.sin(dLng / 2) * Math.sin(dLng / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
             distance = (float) (earthRadius * c);
            Log.d("Distance", String.valueOf(distance));
            Log.d("TAG", dataSnapshot.toString());

            if (distance > 100) {
                for (int i = 0; i < angkots.size(); i++) {
                    Angkot angkot1 = angkots.get(i);
                    if (angkot1.getNomorAngkot().equals(mMarkers.get(key).getTitle())) {
                        angkots.remove(angkot1);
                        Log.d("JALAN", "ANJING");
                        btnPesan.setVisibility(View.INVISIBLE);
                    }
                }
            }

            if (distance < 100) {
                btnPesan.setVisibility(View.VISIBLE);
                Log.d("JALAN", "JALAN");
                for (Angkot angkot2 : angkotDatabase) {
                    if (angkot2.getNomorAngkot().equals(mMarkers.get(key).getTitle())) {

                        if (!angkots.contains(angkot2)) {
                            angkots.add(angkot2);
                        }
                    }
                }


            }
            adapter.setListAngkot(angkots);
            adapter.notifyDataSetChanged();


        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (
                Marker marker : mMarkers.values())

        {
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_angkot));
            marker.setTag("Angkot");
            builder.include(marker.getPosition());


        }


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()

        {
            @Override
            public boolean onMarkerClick(Marker marker) {

                String id = marker.getTitle();
                if (marker.getTag().equals("Angkot")) {
                    idAngkotBar.setText(id);
                    tujuanBar.setText(dataSnapshot.child("Tujuan").getValue().toString());
                    tvCapacity.setText(dataSnapshot.child("Penumpang").getValue().toString() + "/8");
                    tvPlate.setText(dataSnapshot.child("Nomor Plat").getValue().toString());
                    tvCapacity.setVisibility(View.VISIBLE);
                    tvPlate.setVisibility(View.VISIBLE);

                    if(distance<100){
                    btnPesan.setVisibility(View.VISIBLE);}

                    //   tvDistance.setText(dataSnapshot.child("Tujuan").getValue().toString());

                }

                if (marker.getTag().equals("Shelter")) {
                    idAngkotBar.setText(marker.getTitle());
                    for (Shelter shelter : shelters) {
                        if (shelter.Name.equals(marker.getTitle())) {
                            tujuanBar.setText(shelter.Lokasi);
                            tvCapacity.setVisibility(View.INVISIBLE);
                            tvPlate.setVisibility(View.INVISIBLE);
                            btnPesan.setVisibility(View.INVISIBLE);

                        }
                    }

                }
                return true;
            }
        });

    }


    private List<com.google.maps.model.LatLng> loadGpxData(XmlPullParser parser, InputStream gpxIn)
            throws XmlPullParserException, IOException {
        List<com.google.maps.model.LatLng> latLngs = new ArrayList<>();   // List<> as we need subList for paging later
        parser.setInput(gpxIn, null);
        parser.nextTag();

        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            if (parser.getName().equals("wpt")) {
                // Save the discovered lat/lon attributes in each <wpt>
                latLngs.add(new com.google.maps.model.LatLng(
                        Double.valueOf(parser.getAttributeValue(null, "lat")),
                        Double.valueOf(parser.getAttributeValue(null, "lon"))));
            }
            // Otherwise, skip irrelevant data
        }

        return latLngs;
    }

    /**
     * Handles the GPX button-click event, running the demo snippet {@link #loadGpxData}.
     */
    public void startGpx() {
        try {
            mCapturedLocations = loadGpxData(Xml.newPullParser(), getResources().openRawResource(R.raw.gpx_data));
            mCapturedLocations2 = loadGpxData(Xml.newPullParser(), getResources().openRawResource(R.raw.gpx_lely));
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            PolylineOptions polyline = new PolylineOptions();
            PolylineOptions polyline2 =new PolylineOptions();

            for (com.google.maps.model.LatLng ll : mCapturedLocations) {
                com.google.android.gms.maps.model.LatLng mapPoint =
                        new com.google.android.gms.maps.model.LatLng(ll.lat, ll.lng);
                builder.include(mapPoint);
                polyline.add(mapPoint);
                Log.d(TAG, "startGpx: ");
            }

            mMap.addPolyline(polyline.color(Color.BLUE));


            for (com.google.maps.model.LatLng ll : mCapturedLocations2) {
                com.google.android.gms.maps.model.LatLng mapPoint =
                        new com.google.android.gms.maps.model.LatLng(ll.lat, ll.lng);
                builder.include(mapPoint);
                polyline2.add(mapPoint);
                Log.d(TAG, "startGpx: ");
            }

            mMap.addPolyline(polyline2.color(Color.GREEN));

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();

        }
    }

    private void requestLocationUpdates() {

        LocationRequest request = new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED)
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        currentPoisiton = new LatLng(location.getLatitude(), location.getLongitude());
                        builder.include(currentPoisiton);
                        mMap.addMarker(new MarkerOptions().position(currentPoisiton).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_mylocation)));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 0));
                        Log.d("onLocationResult", "Success");
                    }
                }
            }, null);
    }


    public void requetPermission() {
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show();
            finish();
        }

        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}


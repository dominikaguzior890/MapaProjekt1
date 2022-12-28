package edu.mob.mapaprojekt1;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.location.LocationListenerCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.util.List;
import java.util.Locale;

public class CMain extends AppCompatActivity implements LocationListenerCompat {

    protected LocationManager locMan;
    protected TextView tvLat;
    protected TextView tvLon;
    protected TextView tvAlt;
    protected TextView tvAdress;
    Button btnNewWayPoint;
    Button btnWayPointList;
    Button btnShowMap;


    Location currentLocation;
    List<Location> savedLocations;


    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(), isGranted -> {
                        if (isGranted) {
                            Toast.makeText(getApplicationContext(),
                                    "Dostęp uzyskany", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Brak dostępu", Toast.LENGTH_LONG).show();
                        }
                    }
            );

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locMan = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        tvLat = findViewById(R.id.tvLat);
        tvLon = findViewById(R.id.tvLon);
        tvAlt = findViewById(R.id.tvAlt);
        tvAdress = findViewById(R.id.tvAdress);

        btnNewWayPoint = findViewById(R.id.btnNewWayPoint);
        btnWayPointList = findViewById(R.id.btnWayPointList);
        btnShowMap = findViewById(R.id.btnShowMap);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        btnNewWayPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CMyApp myApp = (CMyApp) getApplicationContext();
                savedLocations = myApp.getMyLocations();
                savedLocations.add(currentLocation);

            }
        });

        btnWayPointList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CMain.this, CListLocation.class);
                startActivity(i);
            }
        });

        btnShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CMain.this, CMapa.class);
                startActivity(i);
            }
        });
    }

    private String formatPosition(double value, char loc) {
        int deg = (int) value;
        int min = (int) ((value - deg) * 60); //jak podzielimy przez 60 to wyjdzie calkowita
        double sec = ((value - deg - min / 60.) * 3600); //a jak przez 60. to wyjdzie z przecinkiem
        return String.format(Locale.getDefault(),
                "\t\t%3d\u00b0 %2d' %5.3f\" %c", deg, min, sec, loc);
    }

    public void onLocationChanged(@NonNull Location location) {

        double v = location.getLongitude();
        tvLon.setText(formatPosition(Math.abs(v), (v >= 0) ? 'E' : 'W'));
        v = location.getLatitude();
        tvLat.setText(formatPosition(Math.abs(v), (v >= 0) ? 'N' : 'S'));
        tvAlt.setText(String.format(Locale.getDefault(),
                "%9.1f m", location.getAltitude()));

        Geocoder geocoder = new Geocoder(CMain.this);
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            tvAdress.setText(addresses.get(0).getAddressLine(0));
        } catch (Exception e) {
            tvAdress.setText("Nie można zwrócić adresu");

        }
    }

        @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Brak dostępu", Toast.LENGTH_LONG).show();
            return;
        }
        locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500L, 20.f, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locMan.removeUpdates(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

}




package com.example.aqil.angkotcustomerside;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        findViewById(R.id.cek_route).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);

            }
        });

        findViewById(R.id.Riwayat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, Riwayat.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.karcis).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, TicketActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.karcis).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, TicketActivity.class);
                startActivity(intent);
            }
        });
    }
}

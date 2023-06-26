package com.example.aplikasibioskop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Menu extends AppCompatActivity implements View.OnClickListener {

    private Button listfilmButton;
    private Button bookingButton;
    private Button batalbookingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        listfilmButton = findViewById(R.id.listfilmButton);
        bookingButton = findViewById(R.id.bookingButton);
        batalbookingButton = findViewById(R.id.batalbookingButton);

        listfilmButton.setOnClickListener(this);
        bookingButton.setOnClickListener(this);
        batalbookingButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.listfilmButton:
                listfilm();
                break;
            case R.id.bookingButton:
                booking();
                break;
            case R.id.batalbookingButton:
                batalbooking();
                break;
        }
    }

    private void listfilm() {
        Intent intent = new Intent(this, ListFilm.class);
        startActivity(intent);
    }

    private void booking() {
        Intent intent = new Intent(this, Booking.class);
        startActivity(intent);
    }

    private void batalbooking() {
        Intent intent = new Intent(this, BatalBooking.class);
        startActivity(intent);
    }
}
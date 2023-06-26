package com.example.aplikasibioskop;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;

public class Booking extends AppCompatActivity {

    private EditText editTextNama;
    private EditText editTextTanggal;
    private EditText editTextJudulFilm;
    private EditText editTextNoTeater;
    private Button buttonBooking;

    private Spinner rowSpinner;
    private Spinner columnSpinner;

    private String[] rows = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    private String[] columns = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};

    private static final String SERVER_URL = "http://192.168.182.241:7000/booking";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        editTextNama = findViewById(R.id.editTextNama);
        editTextTanggal = findViewById(R.id.editTextTanggal);
        editTextJudulFilm = findViewById(R.id.editTextJudulFilm);
        editTextNoTeater = findViewById(R.id.editTextNoTeater);
        buttonBooking = findViewById(R.id.buttonBooking);

        rowSpinner = findViewById(R.id.rowSpinner);
        columnSpinner = findViewById(R.id.columnSpinner);

        ArrayAdapter<String> rowAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, rows);
        rowAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rowSpinner.setAdapter(rowAdapter);

        ArrayAdapter<String> columnAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, columns);
        columnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        columnSpinner.setAdapter(columnAdapter);

        editTextTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        buttonBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nama = editTextNama.getText().toString();
                String tanggal = editTextTanggal.getText().toString();
                String judulFilm = editTextJudulFilm.getText().toString();
                String noTeater = editTextNoTeater.getText().toString();
                String selectedRow = rowSpinner.getSelectedItem().toString();
                String selectedColumn = columnSpinner.getSelectedItem().toString();
                String noKursi = selectedRow + selectedColumn;

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("nama", nama);
                    jsonObject.put("tanggal", tanggal);
                    jsonObject.put("judul", judulFilm);
                    jsonObject.put("noteater", noTeater);
                    jsonObject.put("nokursi", noKursi);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                new BookingTask().execute(jsonObject.toString());
            }
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String selectedDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                editTextTanggal.setText(selectedDate);
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    private class BookingTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(SERVER_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setDoOutput(true);

                OutputStream outputStream = urlConnection.getOutputStream();
                outputStream.write(params[0].getBytes("UTF-8"));
                outputStream.close();

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        response.append(line);
                    }
                    bufferedReader.close();
                    return response.toString();
                } else {
                    return null;
                }
            } catch (Exception e) {
                Log.e("Booking", "Error occurred during booking", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    int status = jsonResponse.optInt("status");
                    String response = jsonResponse.optString("response");

                    if (status == 200) {
                        Toast.makeText(Booking.this, "Berhasil"+response, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Booking.this, response, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("Booking", "Error parsing booking response", e);
                }
            } else {
                Toast.makeText(Booking.this, "Sudah terisi", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

package com.example.aplikasibioskop;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ListFilm extends AppCompatActivity {

    private ListView listFilm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_film);
        listFilm = findViewById(R.id.listFilm);
        new datafilm().execute();
    }

    private class datafilm extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String result;
            try {
                URL url = new URL("http://192.168.182.241:7000/film");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    result = response.toString();
                } else {
                    result = "Error: " + responseCode;
                }
                connection.disconnect();
            } catch (Exception e) {
                result = "Error: " + e.getMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            List<String> listfilm = new ArrayList<>();
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject film = jsonArray.getJSONObject(i);
                    String JudulFilm = film.getString("JudulFilm");
                    int HargaFilm = film.getInt("HargaFilm");
                    int NoTeater = film.getInt("NoTeater");
                    int JumlahKursi = film.getInt("JumlahKursi");

                    String detail = "Judul Film: " + JudulFilm + "\n"
                            + "Harga: " + HargaFilm + "\n"
                            + "Nomor Teater: " + NoTeater + "\n"
                            + "Jumlah Kursi Tersedia: " + JumlahKursi;

                    listfilm.add(detail);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(ListFilm.this, android.R.layout.simple_list_item_1, listfilm);
                listFilm.setAdapter(adapter);

            } catch (JSONException e) {
                Toast.makeText(ListFilm.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}
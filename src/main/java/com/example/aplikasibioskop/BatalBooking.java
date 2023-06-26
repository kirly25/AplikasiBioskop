package com.example.aplikasibioskop;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BatalBooking extends Activity {

    private EditText usernameField;
    private EditText bookingIdField;
    private EditText nokursiField;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batal_booking);

        usernameField = findViewById(R.id.usernameField);
        bookingIdField = findViewById(R.id.noteaterField);
        nokursiField = findViewById(R.id.nokursiField);
        cancelButton = findViewById(R.id.cancelButton);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameField.getText().toString();
                String bookingId = bookingIdField.getText().toString();
                String nokursi = nokursiField.getText().toString();

                if (username.isEmpty() || bookingId.isEmpty() || nokursi.isEmpty()) {
                    Toast.makeText(BatalBooking.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject cancelData = new JSONObject();
                        cancelData.put("nama", username);
                        cancelData.put("noteater", bookingId);
                        cancelData.put("nokursi", nokursi);

                        sendCancelRequest(cancelData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void sendCancelRequest(final JSONObject cancelData) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://192.168.182.241:7000/batal"); // Replace with your server URL

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                    outputStream.write(cancelData.toString().getBytes());
                    outputStream.flush();
                    outputStream.close();

                    int responseCode = connection.getResponseCode();
                    String responseMessage = connection.getResponseMessage();
                    final String responseBody = readResponse(connection.getInputStream());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (responseCode == HttpURLConnection.HTTP_OK) {
                                Toast.makeText(BatalBooking.this, "Booking cancelled successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(BatalBooking.this, "Failed to cancel booking: " + responseBody, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    connection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private String readResponse(InputStream inputStream) throws IOException {
        StringBuilder responseBuilder = new StringBuilder();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            responseBuilder.append(new String(buffer, 0, bytesRead));
        }
        inputStream.close();
        return responseBuilder.toString();
    }
}

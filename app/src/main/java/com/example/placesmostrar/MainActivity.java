package com.example.placesmostrar;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mapi;
    private RequestQueue requestQueue;
    private Marker select;
    String apiKey = "AIzaSyD0ONVovLBMhzWI2nU0XEkJguQO-y_cJrI";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = Volley.newRequestQueue(this);
        SupportMapFragment mapi = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapi.getMapAsync(this);

    }

    private void changeMapType(int mapType) {
        if (mapi != null) {
            mapi.setMapType(mapType);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapi = googleMap;

        mapi.setOnMapClickListener(latLng -> {
            if (select != null) {
                select.remove();
            }
            select = mapi.addMarker(new MarkerOptions().position(latLng));
            mapi.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f));
            Detalles(latLng);
        });
    }

    private void Detalles(LatLng latLng) {
        String Url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                "location=" + latLng.latitude + "," + latLng.longitude +
                "&radius=1500&type=bar" +
                "&key=" + apiKey;

        Voley voley = new Voley(requestQueue);
        voley.sendJsonObjectRequest(Url,
                response -> {
                    try {
                        JSONArray Resultados = response.getJSONArray("results");
                        if (Resultados.length() > 0) {
                            JSONObject jsonobjt = Resultados.getJSONObject(0);
                            String Id = jsonobjt.getString("place_id");
                            Forma(Id);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e("Error", "Hubo un error: " + error.toString());
                    Toast.makeText(this, "Ocurrió un error al obtener los detalles del lugar. Por favor, inténtalo de nuevo.", Toast.LENGTH_LONG).show();
                }

        );
    }

    private void Forma(String Id) {
        String Url = "https://maps.googleapis.com/maps/api/place/details/json?" +
                "fields=name,rating,formatted_phone_number" +
                "&place_id=" + Id +
                "&key=" + apiKey;

        Voley httpUtil = new Voley(requestQueue);
        httpUtil.sendJsonObjectRequest(Url,
                response -> {
                    try {
                        JSONObject resultado = response.getJSONObject("result");
                        String Name = resultado.getString("name");
                        double rango = resultado.optDouble("rating");
                        String Number = resultado.optString("formatted_phone_number");
                        String Iamge = resultado.optString("photo_reference");

                        Ver(Name, rango, Number, Iamge);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e("Error", "Hubo un error: " + error.toString());
                    Toast.makeText(this, "Ocurrió un error al obtener los detalles del lugar. Por favor, inténtalo de nuevo.", Toast.LENGTH_LONG).show();
                }

        );
    }

    private void Ver(String Name, double rango, String Number, String Image) {
        Context ctx = this;

        LayoutInflater info = LayoutInflater.from(ctx);
        View dialog = info.inflate(R.layout.info, null);

        ImageView ImageUrl = dialog.findViewById(R.id.Image);
        TextView name = dialog.findViewById(R.id.Name);
        TextView Valoracion = dialog.findViewById(R.id.Rango);
        TextView number = dialog.findViewById(R.id.Number);

        name.setText(Name);
        Valoracion.setText("Valoración: " + rango);
        number.setText("Numero: " + Number);

        if (!Image.isEmpty()) {
            String photoUrl = "https://maps.googleapis.com/maps/api/place/photo" +
                    "?maxwidth=400" +
                    "&photo_reference=" + Image +
                    "&key=" + apiKey;

            Glide.with(ctx)
                    .load(photoUrl)
                    .into(ImageUrl);
        }

        Dialog dialogo = new Dialog(ctx);
        dialogo.setContentView(dialog);
        dialogo.setCancelable(true);

        dialogo.show();
    }

    public class Voley {

        private final RequestQueue requestQueue;

        public Voley(RequestQueue requestQueue) {
            this.requestQueue = requestQueue;
        }

        public void sendJsonObjectRequest(String url, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, listener, errorListener);
            requestQueue.add(request);
        }
    }
}

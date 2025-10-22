package com.example.upnfit.fragmentos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.upnfit.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import cz.msebera.android.httpclient.Header;

public class MapaFragment extends Fragment {
    private final static String urlMostrarUbicaciones = "http://renovaapp.atwebpages.com/Services/Listar_ubicaciones.php";
    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL); // Usa HYBRID si prefieres satélite

            // Habilitar controles de zoom
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.getUiSettings().setMapToolbarEnabled(true);

            // Habilitar gestos
            googleMap.getUiSettings().setAllGesturesEnabled(true);

            // Verificar permisos de ubicación
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 10);
            } else {
                googleMap.setMyLocationEnabled(true);
            }

            //Llamada HTTP para cargar eventos
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(urlMostrarUbicaciones, null, new BaseJsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                    try {
                        JSONArray jsonArray = new JSONArray(rawJsonResponse);
                        LatLng primeraUbicacion = null;

                        for (int i = 0; i < jsonArray.length(); i++) {
                            double lat = jsonArray.getJSONObject(i).getDouble("latitud");
                            double lng = jsonArray.getJSONObject(i).getDouble("longitud");
                            String nombre = jsonArray.getJSONObject(i).getString("nombre");
                            String descripcion = jsonArray.getJSONObject(i).getString("descripcion");
                            String tipoActividad = jsonArray.getJSONObject(i).getString("tipo_actividad");

                            LatLng ubicacion = new LatLng(lat, lng);
                            MarkerOptions marker = new MarkerOptions().position(ubicacion).title(nombre).snippet(descripcion);

                            // Personalizar íconos según descripción
                            if ("correr".equalsIgnoreCase(tipoActividad)) {
                                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.pe32));
                            } else if ("gimnasio".equalsIgnoreCase(tipoActividad)) {
                                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ad32));
                            } else if ("libre".equalsIgnoreCase(tipoActividad)) {
                                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.pe32));
                            } else {
                                marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                            }

                            googleMap.addMarker(marker);

                            if (i == 0) {
                                primeraUbicacion = ubicacion;
                            }
                        }

                        if (primeraUbicacion != null) {
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(primeraUbicacion, 15));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Object errorResponse) {
                    throwable.printStackTrace(); // Manejo básico de error
                }

                @Override
                protected Object parseResponse(String rawJsonData, boolean isFailure) {
                    return null; // No se usa aquí
                }
            });
        }
    };

            // 🎯 MARCADORES MANUALES

            /*
            //
            LatLng evento1 = new LatLng(-12.0029236, -77.0050172); // Lima
            LatLng evento2 = new LatLng(-12.0090251, -77.0023951); // Otro punto en Lima
            LatLng evento3 = new LatLng(-12.0198717, -77.0098626); // Otro punto más

            MarkerOptions marcador1 = new MarkerOptions()
                    .position(evento1)
                    .title("Zona para correr")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pe32));

            MarkerOptions marcador2 = new MarkerOptions()
                    .position(evento2)
                    .title("Zona de entrenamiento")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ad32));

            MarkerOptions marcador3 = new MarkerOptions()
                    .position(evento3)
                    .title("Área Mixta")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

            googleMap.addMarker(marcador1);
            googleMap.addMarker(marcador2);
            googleMap.addMarker(marcador3);

            // Centrar la cámara en el primer evento
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(evento1, 16));
        }
        */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mapa, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}
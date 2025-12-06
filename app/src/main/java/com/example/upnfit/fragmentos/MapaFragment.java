package com.example.upnfit.fragmentos;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.upnfit.R;
// Importaciones de Google Maps
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

// Heredar de Fragment e implementar OnMapReadyCallback
public class MapaFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    // CREACIÓN DE LA VISTA (

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // INFLAR EL NUEVO LAYOUT DEL FRAGMENTO
        return inflater.inflate(R.layout.fragment_mapa, container, false);
    }

    // VISTA CREADA

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Botón Volver
        Button btnVolver = view.findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> {
            // GESTIONAR LA NAVEGACIÓN DENTRO DEL FRAGMENTO
            // Volver al fragmento anterior (ActividadFragment)
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else {
                // Si no hay fragmentos en el stack, simplemente finalizar la actividad (MainActivity)
                requireActivity().finish();
            }
        });

        // 2.  INICIALIZAR EL SupportMapFragment
        // Usamos ChildFragmentManager para gestionar el Fragmento anidado (el mapa)
        SupportMapFragment mapFragment = new SupportMapFragment();

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();


        fragmentTransaction.replace(R.id.map_container, mapFragment);
        fragmentTransaction.commit();

        mapFragment.getMapAsync(this);
    }

    // MAPA LISTO

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Controles UI
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);

        // Permisos
        checkLocationPermission();

        // Cámara en Lima
        LatLng lima = new LatLng(-12.0464, -77.0428);
        mMap.addMarker(new MarkerOptions().position(lima).title("Lima, Perú"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lima, 15));
    }

    // PERMISOS

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // Permiso ya concedido
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            // Solicitamos el permiso al usuario
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, intentamos activar la capa de ubicación
                if (mMap != null && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
            }
        }
    }
}
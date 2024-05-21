package com.cookandroid.cooking;

import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.cookandroid.cooking.databinding.ActivityMapsBinding;
import android.Manifest;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private EditText searchBox; // 검색 상자 추가
    private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 검색 상자 초기화
        searchBox = findViewById(R.id.search_box);
        geocoder = new Geocoder(this, Locale.getDefault());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // 배재대학교의 위도와 경도
        double latitude = 36.321861;
        double longitude = 127.367281;

        // 배재대학교 위치를 LatLng 객체로 생성합니다.
        LatLng baekjeUniversity = new LatLng(latitude, longitude);

        // 배재대학교 위치에 마커를 추가합니다.
        mMap.addMarker(new MarkerOptions().position(baekjeUniversity).title("배재대학교"));

        // 배재대학교 위치로 지도의 카메라를 이동시킵니다.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(baekjeUniversity, 17));

        // 검색 버튼 클릭 시 이벤트 처리
        binding.searchButton.setOnClickListener(view -> {
            String locationName = searchBox.getText().toString();
            if (!locationName.isEmpty()) {
                try {
                    List<Address> addresses = geocoder.getFromLocationName(locationName, 1);
                    if (!addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng).title(locationName));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                    } else {
                        Toast.makeText(MapsActivity.this, "Location not found", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(MapsActivity.this, "Please enter a location", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

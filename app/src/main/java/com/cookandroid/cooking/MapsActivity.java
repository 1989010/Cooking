package com.cookandroid.cooking;

import androidx.fragment.app.FragmentActivity;

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

        // 배재대학교 사거리의 위도와 경도
        double latitude = 36.322504;
        double longitude = 127.370224;

        // 배재대학교 사거리 위치를 LatLng 객체로 생성합니다.
        LatLng baekjeUniversity = new LatLng(latitude, longitude);


        // 배재대학교 사거리 위치로 지도의 카메라를 이동시킵니다.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(baekjeUniversity, 17));

        // 추가할 마커들
        addMarker(36.323508, 127.368818, "훼미리마트 배재대점");
        addMarker(36.321689, 127.369253, "솔로몬마트");
        addMarker(36.322427, 127.371255, "훼미리마트 배재원룸점");
        addMarker(36.324943, 127.370636, "K2쇼핑마트");
        addMarker(36.325568, 127.370167, "대박마트");
        addMarker(36.326938, 127.370894, "GS슈퍼마켓 대전도마점");
        addMarker(36.322732, 127.373958, "(주)귀빈장마트");
        addMarker(36.322284, 127.375208, "영할인마트");
        addMarker(36.322448, 127.376372, "중앙할인마트");

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
                        Toast.makeText(MapsActivity.this, "위치가 검색되지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(MapsActivity.this, "위치를 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMarker(double lat, double lng, String title) {
        LatLng location = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(location).title(title));
    }
}

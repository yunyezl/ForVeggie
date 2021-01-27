package com.forveggie.ui.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.forveggie.R;
import com.forveggie.ui.fragment.MyPageFragment;
import com.forveggie.ui.fragment.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    SearchFragment searchFragment;
    MyPageFragment myPageFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchFragment = new SearchFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, searchFragment).commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.bottom_search:
                    if (searchFragment != null) getSupportFragmentManager().beginTransaction().show(searchFragment).commit();
                    if (myPageFragment != null) getSupportFragmentManager().beginTransaction().hide(myPageFragment).commit();
                    return true;
                case R.id.bottom_mypage:
                    if (myPageFragment == null) {
                        myPageFragment = new MyPageFragment();
                        getSupportFragmentManager().beginTransaction().add(R.id.container, myPageFragment).commit();
                    }
                    if (searchFragment != null) getSupportFragmentManager().beginTransaction().hide(searchFragment).commit();
                    if (myPageFragment != null) getSupportFragmentManager().beginTransaction().show(myPageFragment).commit();
                    return true;
            }
            return false;
        });

    }
}
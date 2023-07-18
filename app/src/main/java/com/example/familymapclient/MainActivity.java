package com.example.familymapclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import Data.DataCache;

public class MainActivity extends AppCompatActivity implements Login_Fragment.Listener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Iconify.with(new FontAwesomeModule());

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.login_fragment);
        Fragment mapFragment = new MapsFragment();
        DataCache dc = DataCache.getInstance();
        String authToken = dc.getAuthToken();

        if (authToken == null) {
            if (fragment == null) {
                fragment = createLoginFragment();
                fm.beginTransaction().add(R.id.main_frame_layout, fragment).commit();
            } else {
                if (fragment instanceof Login_Fragment) {
                    ((Login_Fragment) fragment).registerListener(this);
                }
            }
        }else{
            if (mapFragment == null) {
                mapFragment = createMapFragment();
                fm.beginTransaction().add(R.id.main_frame_layout, mapFragment).commit();
            }
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        System.out.println("PRESSED");
    }

    private Fragment createLoginFragment (){
        Login_Fragment fragment = new Login_Fragment();
        fragment.registerListener(this);
        return fragment;
    }

    private Fragment createMapFragment (){
        MapsFragment fragment = new MapsFragment();
        return fragment;
    }

    public void notifyDone(){
        FragmentManager fm = this.getSupportFragmentManager();
        Fragment fragment = createMapFragment();
        fm.beginTransaction().
                replace(R.id.main_frame_layout, fragment).commit();
    }


}
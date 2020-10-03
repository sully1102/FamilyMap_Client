package com.sully11.android.familymapclient.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.sully11.android.familymapclient.FamilyData;
import com.sully11.android.familymapclient.Fragments.LoginFragment;
import com.sully11.android.familymapclient.Fragments.MapFragment;
import com.sully11.android.familymapclient.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FragmentManager fm = getSupportFragmentManager();
        Fragment loginFragment = fm.findFragmentById(R.id.fragmentContainer);
        Fragment mapFragment = fm.findFragmentById(R.id.fragmentContainer);


        if(FamilyData.getInstance().isLoggedIn()) {
            if(mapFragment == null) {
                mapFragment = new MapFragment();
                fm.beginTransaction()
                        .add(R.id.fragmentContainer, mapFragment)
                        .commit();
            }
        } else {
            if (loginFragment == null) {
                loginFragment = LoginFragment.newInstance(this);
                fm.beginTransaction()
                        .add(R.id.fragmentContainer, loginFragment)
                        .commit();
            }
        }
    }

    public void loginSuccess() {

        MapFragment mapFragment = new MapFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, mapFragment);
        fragmentTransaction.commit();
    }
}

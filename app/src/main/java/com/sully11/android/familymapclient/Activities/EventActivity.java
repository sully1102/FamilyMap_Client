package com.sully11.android.familymapclient.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.sully11.android.familymapclient.FamilyData;
import com.sully11.android.familymapclient.Fragments.MapFragment;
import com.sully11.android.familymapclient.R;

public class EventActivity extends AppCompatActivity {

    private String eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        eventID = getIntent().getExtras().getString("eventID");

        FamilyData fd = FamilyData.getInstance();
        fd.setSelectedEventID(eventID);


        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.mapContainer);

        if(mapFragment == null) {
            mapFragment = new MapFragment();
            fm.beginTransaction()
                    .add(R.id.mapContainer, mapFragment)
                    .commit();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        FamilyData fd = FamilyData.getInstance();
        fd.setComingFromPersonOrSearch(false);
    }
}

package com.sully11.android.familymapclient.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sully11.android.familymapclient.Activities.PersonActivity;
import com.sully11.android.familymapclient.Activities.SearchActivity;
import com.sully11.android.familymapclient.Activities.SettingActivity;
import com.sully11.android.familymapclient.FamilyData;
import com.sully11.android.familymapclient.R;

import java.util.ArrayList;

import XPOJOS.Model.Event;
import XPOJOS.Model.Person;


public class MapFragment extends Fragment implements OnMapReadyCallback  {

    private GoogleMap map;
    private View mView;

    private String selectedPersonID = new String();
    private ArrayList<Polyline> polylines = new ArrayList<Polyline>();


    @Override
    public void onCreate(Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        super.onCreate(savedInstanceState);
        Iconify.with(new FontAwesomeModule());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        FamilyData fd = FamilyData.getInstance();
        if(fd.isComingFromPersonOrSearch()) {
            setHasOptionsMenu(false);
        } else {
            inflater.inflate(R.menu.main_menu, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {

        switch(menu.getItemId()) {
            case R.id.searchMenuItem:
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                return true;
            case R.id.settingsMenuItem:
                Intent intent1 = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent1);
                return true;
            default:
                return super.onOptionsItemSelected(menu);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(layoutInflater, container, savedInstanceState);
        mView = layoutInflater.inflate(R.layout.fragment_map, container, false);
        
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        
        return mView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        FamilyData fd = FamilyData.getInstance();

        fd.repopulateCurrentEvents();

        if(!fd.isComingFromPersonOrSearch()) {
            map.moveCamera(CameraUpdateFactory.newLatLng(fd.getLastLocation()));
        }
        if(fd.isComingFromPersonOrSearch()) {
            float latitude = fd.getAllEventIDMap().get(fd.getSelectedEventID()).getLatitude();
            float longitude = fd.getAllEventIDMap().get(fd.getSelectedEventID()).getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }


        for(String key : fd.getCurrentEventsMap().keySet()) {
            for(int i = 0; i < fd.getCurrentEventsMap().get(key).size(); i++) {
                Event currentEvent = fd.getCurrentEventsMap().get(key).get(i);

                LatLng location = new LatLng(currentEvent.getLatitude(), currentEvent.getLongitude());

                MarkerOptions options = new MarkerOptions().position(location);
                float color = getColor(currentEvent.getEventType());
                options.icon(BitmapDescriptorFactory.defaultMarker(color));

                map.addMarker(options).setTag(currentEvent);
            }
        }


        map.setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                FamilyData fd = FamilyData.getInstance();
                if(!fd.isComingFromPersonOrSearch()) {
                    fd.setLastLocation(marker.getPosition());
                }
                simulateClick(marker);
                return false;
            }
        });


        LinearLayout eventDisplay = (LinearLayout) mView.findViewById(R.id.EventDisplay);
        eventDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), PersonActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("personID", getSelectedPersonID());
                startActivity(intent);

            }
        });
    }


    public void simulateClick(Marker marker) {
        FamilyData fd = FamilyData.getInstance();

        for(Polyline line : polylines) {
            line.remove();
        }
        polylines.clear();


        Event selectedEvent = (Event) marker.getTag();
        Person selectedPerson = fd.getAllPersonsMap().get(selectedEvent.getPersonID());
        setSelectedPersonID(selectedPerson.getPersonID());


        String fullname = selectedPerson.getFirstName() + " " + selectedPerson.getLastName();
        String eventDescription = selectedEvent.getEventType();
        String timePlace = selectedEvent.getCity() + ", " + selectedEvent.getCountry() + " (" + selectedEvent.getYear() + ")";


        ImageView icon = mView.findViewById(R.id.iconImage);
        TextView textName = mView.findViewById(R.id.EventPerson);
        TextView textDescription1 = mView.findViewById(R.id.EventType);
        TextView textDescription2 = mView.findViewById(R.id.EventTimePlace);


        if(selectedPerson.getGender().equals("m")) {
            Drawable genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_male).colorRes(R.color.blue).sizeDp(50);
            icon.setImageDrawable(genderIcon);
        } else {
            Drawable genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_female).colorRes(R.color.magenta).sizeDp(50);
            icon.setImageDrawable(genderIcon);
        }

        textName.setText(fullname);
        textDescription1.setText(eventDescription);
        textDescription2.setText(timePlace);


        drawPolyLines(marker.getPosition(), selectedPerson);
    }


    public void drawPolyLines(LatLng src, Person selectedPerson) {
        FamilyData fd = FamilyData.getInstance();
        int currentWidth = 16;

        if(fd.isSpouseLinesOn()) {
            if(fd.getCurrentEventsMap().containsKey(selectedPerson.getSpouseID())) {

                LatLng dest = getLatLng(selectedPerson.getSpouseID(), 0);
                addPolyline(src, dest, Color.YELLOW, currentWidth);
            }
        }

        if(fd.isLifeStoryLinesOn()) {
            if(fd.getCurrentEventsMap().get(selectedPerson.getPersonID()).size() > 1) {

                for(int i = 0; i < fd.getCurrentEventsMap().get(selectedPerson.getPersonID()).size()-1; i++) {

                    LatLng source = getLatLng(selectedPerson.getPersonID(), i);
                    LatLng dest = getLatLng(selectedPerson.getPersonID(), i+1);
                    addPolyline(source, dest, Color.RED, currentWidth);
                }
            }
        }

        if(fd.isFamilyTreeLinesOn()) {

            if(fd.getCurrentEventsMap().containsKey(selectedPerson.getFatherID())) {

                LatLng dest = getLatLng(selectedPerson.getFatherID(), 0);
                addPolyline(src, dest, Color.GREEN, currentWidth);

                Person father = fd.getAllPersonsMap().get(selectedPerson.getFatherID());
                drawParentLines(dest, father, currentWidth);
            }

            if(fd.getCurrentEventsMap().containsKey(selectedPerson.getMotherID())) {

                LatLng dest = getLatLng(selectedPerson.getMotherID(), 0);
                addPolyline(src, dest, Color.GREEN, currentWidth);

                Person mother = fd.getAllPersonsMap().get(selectedPerson.getMotherID());
                drawParentLines(dest, mother, currentWidth);
            }
        }
    }

    public void drawParentLines(LatLng src, Person person, int currentWidth) {
        FamilyData fd = FamilyData.getInstance();

        if(currentWidth > 4) { currentWidth = (currentWidth / 2); }

        if(fd.getCurrentEventsMap().containsKey(person.getFatherID())) {

            LatLng dest = getLatLng(person.getFatherID(), 0);
            addPolyline(src, dest, Color.GREEN, currentWidth);

            Person father = fd.getAllPersonsMap().get(person.getFatherID());
            drawParentLines(dest, father, currentWidth);
        }

        if(fd.getCurrentEventsMap().containsKey(person.getMotherID())) {

            LatLng dest = getLatLng(person.getMotherID(), 0);
            addPolyline(src, dest, Color.GREEN, currentWidth);

            Person mother = fd.getAllPersonsMap().get(person.getMotherID());
            drawParentLines(dest, mother, currentWidth);
        }
    }

    public LatLng getLatLng(String personID, int position) {
        FamilyData fd = FamilyData.getInstance();
        LatLng newLatLng = new LatLng(fd.getCurrentEventsMap().get(personID).get(position).getLatitude(),
                fd.getCurrentEventsMap().get(personID).get(position).getLongitude());

        return newLatLng;
    }

    public void addPolyline(LatLng src, LatLng dest, int color, int width) {

        Polyline newPolyline = map.addPolyline(new PolylineOptions()
                .clickable(false)
                .add(new LatLng(src.latitude, src.longitude),
                        new LatLng(dest.latitude, dest.longitude))
                .width(width).color(color));

        polylines.add(newPolyline);
    }

    public float getColor(String eventType) {
        FamilyData fd = FamilyData.getInstance();
        int position = 0;


        for(String entry : fd.getEventTypes()) {
            if(entry.equals(eventType.toLowerCase())) {
                break;
            }
            position++;
        }

        float finalColor = (360 / fd.getEventTypes().size()) * position;
        return finalColor;
    }

    public String getSelectedPersonID() {
        return selectedPersonID;
    }

    public void setSelectedPersonID(String selectedPersonID) {
        this.selectedPersonID = selectedPersonID;
    }

    public MapFragment() {
        // Required empty public constructor
    }
}

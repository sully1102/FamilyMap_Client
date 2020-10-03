package com.sully11.android.familymapclient.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.sully11.android.familymapclient.FamilyData;
import com.sully11.android.familymapclient.R;

import java.util.ArrayList;

import XPOJOS.Model.Event;
import XPOJOS.Model.Person;

public class PersonActivity extends AppCompatActivity {

    private static final int PERSON_GROUP_POSITION = 0;
    private static final int EVENT_GROUP_POSITION = 1;

    private String personID = new String();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        FamilyData fd = FamilyData.getInstance();

        personID = getIntent().getExtras().getString("personID");


        TextView firstNameView = findViewById(R.id.actualFirstName);
        firstNameView.setText(fd.getAllPersonsMap().get(personID).getFirstName());

        TextView lastNameView = findViewById(R.id.actualLastName);
        lastNameView.setText(fd.getAllPersonsMap().get(personID).getLastName());


        TextView genderView = findViewById(R.id.actualGender);
        if(fd.getAllPersonsMap().get(personID).getGender().equals("m")) {
            genderView.setText(R.string.male);
        } else {
            genderView.setText(R.string.female);
        }

        ExpandableListView expandableListView = findViewById(R.id.expandableListView);
        expandableListView.setAdapter(new ExpandableListAdapter(getFamilyList(), getEventList()));
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {

        private final ArrayList<Person> familyList;
        private final ArrayList<Event> eventList;

        ExpandableListAdapter(ArrayList<Person> familyList, ArrayList<Event> eventList) {
            this.familyList = familyList;
            this.eventList = eventList;
        }

        @Override
        public int getGroupCount() { return 2; }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition) {
                case PERSON_GROUP_POSITION:
                    return familyList.size();
                case EVENT_GROUP_POSITION:
                    return eventList.size();
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            switch (groupPosition) {
                case PERSON_GROUP_POSITION:
                    return "Family";
                case EVENT_GROUP_POSITION:
                    return "Event";
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            switch (groupPosition) {
                case PERSON_GROUP_POSITION:
                    return familyList.get(childPosition);
                case EVENT_GROUP_POSITION:
                    return eventList.get(childPosition);
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public long getGroupId(int groupPosition) { return groupPosition; }

        @Override
        public long getChildId(int groupPosition, int childPosition) { return childPosition; }

        @Override
        public boolean hasStableIds() { return false; }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_group, parent, false);
            }


            TextView titleView = convertView.findViewById(R.id.listTitle);

            if(groupPosition == PERSON_GROUP_POSITION) {
                titleView.setText(R.string.personFamilyTitle);
            } else {
                titleView.setText(R.string.personEventTitle);
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView;

            if(groupPosition == PERSON_GROUP_POSITION) {
                itemView = getLayoutInflater().inflate(R.layout.item_person, parent, false);
                initializeFamilyView(itemView, childPosition);
            } else {
                itemView = getLayoutInflater().inflate(R.layout.item_event, parent, false);
                initializeEventView(itemView, childPosition);
            }

            return itemView;
        }

        private void initializeFamilyView(View personItemView, final int childPosition) {
            FamilyData fd = FamilyData.getInstance();


            ImageView icon = personItemView.findViewById(R.id.iconPersonImage);
            if(familyList.get(childPosition).getGender().equals("m")) {
                Drawable genderIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_male).colorRes(R.color.blue).sizeDp(50);
                icon.setImageDrawable(genderIcon);
            } else {
                Drawable genderIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_female).colorRes(R.color.magenta).sizeDp(50);
                icon.setImageDrawable(genderIcon);
            }


            TextView personNameView = personItemView.findViewById(R.id.PersonName);
            String fullName = familyList.get(childPosition).getFirstName() + " " + familyList.get(childPosition).getLastName();
            personNameView.setText(fullName);


            Person currentPerson = fd.getAllPersonsMap().get(personID);
            TextView personRelationView = personItemView.findViewById(R.id.RelationTitle);
            personRelationView.setText(fd.getRelationship(currentPerson, familyList.get(childPosition).getPersonID()));


            personItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newPersonID = familyList.get(childPosition).getPersonID();
                    Intent intent = new Intent(PersonActivity.this, PersonActivity.class);
                    intent.putExtra("personID", newPersonID);
                    startActivity(intent);
                }
            });
        }

        private void initializeEventView(View eventItemView, final int childPosition) {

            ImageView icon = eventItemView.findViewById(R.id.iconEventImage);
            Drawable genderIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_map_marker).colorRes(R.color.forestGreen).sizeDp(50);
            icon.setImageDrawable(genderIcon);


            TextView personName = eventItemView.findViewById(R.id.EventPerson1);
            personName.setText(eventList.get(childPosition).getEventType());

            TextView blankSpace = eventItemView.findViewById(R.id.EventType1);
            blankSpace.setText(" ");


            TextView location = eventItemView.findViewById(R.id.EventTimePlace1);
            String namePlace = eventList.get(childPosition).getCity() + ", " +
                                eventList.get(childPosition).getCountry() + " (" +
                                eventList.get(childPosition).getYear() + ")";
            location.setText(namePlace);


            eventItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FamilyData fd = FamilyData.getInstance();
                    fd.setComingFromPersonOrSearch(true);
                    String newEventID = eventList.get(childPosition).getEventID();
                    Intent intent = new Intent(PersonActivity.this, EventActivity.class);
                    intent.putExtra("eventID", newEventID);
                    startActivity(intent);
                }
            });
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) { return true; }
    }

    public ArrayList<Person> getFamilyList() {
        FamilyData fd = FamilyData.getInstance();
        ArrayList<Person> finalList = new ArrayList<Person>();
        Person currentPerson = fd.getAllPersonsMap().get(personID);

        if(currentPerson.getFatherID() != null) {
            finalList.add(fd.getAllPersonsMap().get(currentPerson.getFatherID()));
        }
        if(currentPerson.getMotherID() != null) {
            finalList.add(fd.getAllPersonsMap().get(currentPerson.getMotherID()));
        }
        if(currentPerson.getSpouseID() != null) {
            finalList.add(fd.getAllPersonsMap().get(currentPerson.getSpouseID()));
        }
        if(fd.getChildrenMap().containsKey(currentPerson.getPersonID())) {
            finalList.add(fd.getChildrenMap().get(currentPerson.getPersonID()));
        }


        return finalList;
    }

    public ArrayList<Event> getEventList() {
        FamilyData fd = FamilyData.getInstance();
        ArrayList<Event> finalList = new ArrayList<Event>();

        if(fd.getCurrentEventsMap().containsKey(personID)) {
            finalList.addAll(fd.getCurrentEventsMap().get(personID));
        }

        return finalList;
    }
}

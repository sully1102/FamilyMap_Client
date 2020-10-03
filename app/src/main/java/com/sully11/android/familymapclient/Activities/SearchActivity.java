package com.sully11.android.familymapclient.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.sully11.android.familymapclient.FamilyData;
import com.sully11.android.familymapclient.R;

import java.util.ArrayList;

import XPOJOS.Model.Event;
import XPOJOS.Model.Person;

public class SearchActivity extends AppCompatActivity {

    private static final int PERSON_VIEW_TYPE = 0;
    private static final int EVENT_VIEW_TYPE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {

                RecyclerView recyclerView = findViewById(R.id.RecyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

                SearchAdapter adapter = new SearchAdapter(performPersonSearch(query.toLowerCase()),
                                                           performEventSearch(query.toLowerCase()));

                recyclerView.setAdapter(adapter);
                return false;
            }
        });
    }

    private class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {
        private final ArrayList<Person> personList;
        private final ArrayList<Event> eventList;

        SearchAdapter(ArrayList<Person> personList, ArrayList<Event> eventList) {
            this.personList = personList;
            this.eventList = eventList;
        }

        @Override
        public int getItemViewType(int position) {
            return position < personList.size() ? PERSON_VIEW_TYPE : EVENT_VIEW_TYPE;
        }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;

            if(viewType == PERSON_VIEW_TYPE) {
                view = getLayoutInflater().inflate(R.layout.item_person, parent, false);
            } else {
                view = getLayoutInflater().inflate(R.layout.item_event, parent, false);
            }

            return new SearchViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
            if(position < personList.size()) {
                holder.bind(personList.get(position));
            } else {
                holder.bind(eventList.get(position - personList.size()));
            }
        }

        @Override
        public int getItemCount() { return personList.size() + eventList.size(); }
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView icon;
        private final TextView name;
        private final TextView eventType;
        private final TextView namePlace;

        private final int viewType;
        private Person mPerson;
        private Event mEvent;


        SearchViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;

            itemView.setOnClickListener(this);

            if(viewType == PERSON_VIEW_TYPE) {
                icon = itemView.findViewById(R.id.iconPersonImage);
                name = itemView.findViewById(R.id.PersonName);
                eventType = null;
                namePlace = null;
            } else {
                icon = itemView.findViewById(R.id.iconEventImage);
                name = itemView.findViewById(R.id.EventPerson1);
                eventType = itemView.findViewById(R.id.EventType1);
                namePlace = itemView.findViewById(R.id.EventTimePlace1);
            }
        }

        private void bind(Person person) {
            this.mPerson = person;

            if(mPerson.getGender().equals("m")) {
                Drawable genderIcon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_male).colorRes(R.color.blue).sizeDp(50);
                icon.setImageDrawable(genderIcon);
            } else {
                Drawable genderIcon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_female).colorRes(R.color.magenta).sizeDp(50);
                icon.setImageDrawable(genderIcon);
            }

            String fullName = person.getFirstName() + " " + person.getLastName();
            name.setText(fullName);
        }

        private void bind(Event event) {
            this.mEvent = event;
            FamilyData fd = FamilyData.getInstance();
            Person person = fd.getAllPersonsMap().get(event.getPersonID());

            Drawable markerIcon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_map_marker).colorRes(R.color.forestGreen).sizeDp(50);
            icon.setImageDrawable(markerIcon);


            String fullName = person.getFirstName() + " " + person.getLastName();
            name.setText(fullName);
            eventType.setText(event.getEventType());
            String namePlace1 = event.getCity() + ", " + event.getCountry() + " (" + event.getYear() + ")";
            namePlace.setText(namePlace1);
        }


        @Override
        public void onClick(View view) {
            if(viewType == PERSON_VIEW_TYPE) {
                Intent intent = new Intent(SearchActivity.this, PersonActivity.class);
                intent.putExtra("personID", mPerson.getPersonID());
                startActivity(intent);
            } else {
                FamilyData fd = FamilyData.getInstance();
                fd.setComingFromPersonOrSearch(true);
                Intent intent = new Intent(SearchActivity.this, EventActivity.class);
                intent.putExtra("eventID", mEvent.getEventID());
                startActivity(intent);
            }
        }
    }



    public ArrayList<Person> performPersonSearch(String query) {
        FamilyData fd = FamilyData.getInstance();

        ArrayList<Person> finalList = new ArrayList<Person>();

        for(String key : fd.getAllPersonsMap().keySet()) {
            Person currentPerson = fd.getAllPersonsMap().get(key);

            if(currentPerson.getFirstName().toLowerCase().contains(query)) {
                finalList.add(currentPerson);
            } else if(currentPerson.getLastName().toLowerCase().contains(query)) {
                finalList.add(currentPerson);
            }
        }

        return finalList;
    }


    public ArrayList<Event> performEventSearch(String query) {
        FamilyData fd = FamilyData.getInstance();

        ArrayList<Event> finalList = new ArrayList<Event>();

        for(String key : fd.getCurrentEventsMap().keySet()) {
            Person person = fd.getAllPersonsMap().get(key);

            if(person.getFirstName().toLowerCase().contains(query) || person.getLastName().toLowerCase().contains(query)) {
                finalList.addAll(fd.getCurrentEventsMap().get(key));
            } else {
                for (int i = 0; i < fd.getCurrentEventsMap().get(key).size(); i++) {
                    Event currentEvent = fd.getCurrentEventsMap().get(key).get(i);

                    if (currentEvent.getCountry().toLowerCase().contains(query)) {
                        finalList.add(currentEvent);
                    } else if (currentEvent.getCity().toLowerCase().contains(query)) {
                        finalList.add(currentEvent);
                    } else if (currentEvent.getEventType().toLowerCase().contains(query)) {
                        finalList.add(currentEvent);
                    } else if (Integer.toString(currentEvent.getYear()).contains(query)) {
                        finalList.add(currentEvent);
                    }
                }
            }
        }

        return finalList;
    }
}

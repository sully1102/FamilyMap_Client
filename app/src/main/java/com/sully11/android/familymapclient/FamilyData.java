package com.sully11.android.familymapclient;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import XPOJOS.Model.Event;
import XPOJOS.Model.Person;

public class FamilyData {

    private static FamilyData instance;


    private Person user;

    private String authTokenID;


    private boolean lifeStoryLinesOn = true;
    private boolean familyTreeLinesOn = true;
    private boolean spouseLinesOn = true;
    private boolean fatherSideOn = true;
    private boolean motherSideOn = true;
    private boolean maleEventsOn = true;
    private boolean femaleEventsOn = true;

    private boolean isLoggedIn = false;
    private boolean comingFromPersonOrSearch = false;
    private LatLng lastLocation = new LatLng(31.97, -99.9);
    private String selectedEventID = new String();


    private final Map<String, Person> allPersonsMap =  new HashMap<>();
    private final Map<String, Person> childrenMap = new HashMap<>();

    private Map<String, Event> allEventIDMap = new HashMap<>();
    private Map<String, ArrayList<Event>> allEventsMap = new HashMap<>();
    private Map<String, ArrayList<Event>> currentEventsMap =  new HashMap<>();


    private final Set<String> immediateFamilyMales = new HashSet<>();
    private final Set<String> immediateFamilyFemales = new HashSet<>();

    private final Set<String> fatherSideMales = new HashSet<>();
    private final Set<String> fatherSideFemales = new HashSet<>();
    private final Set<String> motherSideMales = new HashSet<>();
    private final Set<String> motherSideFemales = new HashSet<>();

    private final Set<String> eventTypes = new HashSet<>();


    private FamilyData() {

    }

    public static FamilyData getInstance() {
        if(instance == null) {
            instance = new FamilyData();
        }
        return instance;
    }

    public void setEvents(Event[] events) {

        setLoggedIn(true);
        setSelectedEventID(events[0].getEventID());

        Map<String, ArrayList<Event>> tempMap = new HashMap<>();

        for(int i = 0; i < events.length; i++) {

            allEventIDMap.put(events[i].getEventID(), events[i]);
            eventTypes.add(events[i].getEventType().toLowerCase());

            if(!tempMap.containsKey(events[i].getPersonID())) {
                ArrayList<Event> newList = new ArrayList<>();
                tempMap.put(events[i].getPersonID(), newList);
            }
            tempMap.get(events[i].getPersonID()).add(events[i]);
        }


        for(String key : tempMap.keySet()) {

            Set birthSet = new HashSet<>();
            ArrayList<Event> intermediateList = new ArrayList<Event>();
            Set deathSet = new HashSet<>();


            for(int i = 0; i < tempMap.get(key).size(); i++) {
                Event currentEvent = tempMap.get(key).get(i);

                if(currentEvent.getEventType().toLowerCase().equals("birth")) {

                    birthSet.add(currentEvent);

                } else if(currentEvent.getEventType().toLowerCase().equals("death")) {

                    deathSet.add(currentEvent);

                } else {
                    if(intermediateList.size() > 0) {
                        if(currentEvent.getYear() < intermediateList.get(0).getYear()) {
                            intermediateList.add(0, currentEvent);
                        } else if (currentEvent.getYear() >= intermediateList.get(intermediateList.size()-1).getYear()) {
                            intermediateList.add(currentEvent);
                        } else {
                            for(int j = 0; j < intermediateList.size() - 1; j++) {
                                if(intermediateList.get(j).getYear() <= currentEvent.getYear() &&
                                    intermediateList.get(j+1).getYear() > currentEvent.getYear()) {

                                    intermediateList.add(j+1, currentEvent);
                                }
                            }
                        }
                    } else {
                        intermediateList.add(currentEvent);
                    }
                }
            }

            ArrayList<Event> finalList = new ArrayList<Event>();
            finalList.addAll(birthSet);
            finalList.addAll(intermediateList);
            finalList.addAll(deathSet);


            allEventsMap.put(key, finalList);
        }
    }

    public void setPeople(Person[] people) {

        setUser(people[0]);

        for(int i = 0; i < people.length; i++) {
            allPersonsMap.put(people[i].getPersonID(), people[i]);
        }

        setImmediateFamily();

        setRestOfFamily();
    }

    public void setRestOfFamily() {

        if(user.getFatherID() != null) {
            Person father = allPersonsMap.get(user.getFatherID());
            fatherSideMales.add(father.getPersonID());
            childrenMap.put(user.getFatherID(), user);

            setOneSideOfFamily(father, true);
        }
        if(user.getMotherID() != null) {
            Person mother = allPersonsMap.get(user.getMotherID());
            motherSideFemales.add(mother.getPersonID());
            childrenMap.put(user.getMotherID(), user);

            setOneSideOfFamily(mother, false);
        }
    }

    public void setOneSideOfFamily(Person currentPerson, boolean isFatherSide) {

        if(isFatherSide) {
            if(currentPerson.getFatherID() != null) {
                Person father = allPersonsMap.get(currentPerson.getFatherID());
                fatherSideMales.add(father.getPersonID());
                childrenMap.put(currentPerson.getFatherID(), currentPerson);

                setOneSideOfFamily(father, true);
            }
            if(currentPerson.getMotherID() != null) {
                Person mother = allPersonsMap.get(currentPerson.getMotherID());
                fatherSideFemales.add(mother.getPersonID());
                childrenMap.put(currentPerson.getMotherID(), currentPerson);

                setOneSideOfFamily(mother, true);
            }
        } else {
            if(currentPerson.getFatherID() != null) {
                Person father = allPersonsMap.get(currentPerson.getFatherID());
                motherSideMales.add(father.getPersonID());
                childrenMap.put(currentPerson.getFatherID(), currentPerson);

                setOneSideOfFamily(father, false);
            }
            if(currentPerson.getMotherID() != null) {
                Person mother = allPersonsMap.get(currentPerson.getMotherID());
                motherSideFemales.add(mother.getPersonID());
                childrenMap.put(currentPerson.getMotherID(), currentPerson);

                setOneSideOfFamily(mother, false);
            }
        }
    }

    public void setImmediateFamily() {

        if(user.getGender().equals("m")) {
            immediateFamilyMales.add(user.getPersonID());
        } else {
            immediateFamilyFemales.add(user.getPersonID());
        }


        if(user.getSpouseID() != null) {

            Person spouse = allPersonsMap.get(user.getSpouseID());

            if (spouse.getGender().equals("m")) {
                immediateFamilyMales.add(spouse.getPersonID());
            } else {
                immediateFamilyFemales.add(spouse.getPersonID());
            }
        }
    }


    public void repopulateCurrentEvents() {

        currentEventsMap.clear();

        ArrayList<String> currentPeopleList = new ArrayList<String>();


        if(maleEventsOn) {
            currentPeopleList.addAll(immediateFamilyMales);
        }
        if(femaleEventsOn) {
            currentPeopleList.addAll(immediateFamilyFemales);
        }

        if(maleEventsOn && fatherSideOn) {
            currentPeopleList.addAll(fatherSideMales);
        }
        if(femaleEventsOn && fatherSideOn) {
            currentPeopleList.addAll(fatherSideFemales);
        }

        if(maleEventsOn && motherSideOn) {
            currentPeopleList.addAll(motherSideMales);
        }
        if(femaleEventsOn && motherSideOn) {
            currentPeopleList.addAll(motherSideFemales);
        }

        for(int i = 0; i < currentPeopleList.size(); i++) {

            String personID = currentPeopleList.get(i);
            ArrayList<Event> eventList = allEventsMap.get(personID);

            currentEventsMap.put(personID, eventList);
        }
    }

    public String getRelationship(Person currentPerson, String personID) {

        String relation = new String();

        if(currentPerson.getFatherID() != null) {
            if (currentPerson.getFatherID().equals(personID)) {
                relation = "Father";
            }
        }
        if(currentPerson.getMotherID() != null) {
            if (currentPerson.getMotherID().equals(personID)) {
                relation = "Mother";
            }
        }
        if(currentPerson.getSpouseID() != null) {
            if (currentPerson.getSpouseID().equals(personID)) {
                relation = "Spouse";
            }
        }
        if(getChildrenMap().containsKey(personID)) {
            if(getChildrenMap().get(personID).getPersonID().equals(personID)) {
                relation = "Child";

            }
        }

        return relation;
    }

    public void setUser(Person user) {
        this.user = user;
    }

    public Person getUser() {
        return user;
    }

    public String getAuthToken() {
        return authTokenID;
    }

    public void setAuthToken(String authTokenID) {
        this.authTokenID = authTokenID;
    }

    public Map<String, Person> getChildrenMap() {
        return childrenMap;
    }

    public Map<String, Event> getAllEventIDMap() {
        return allEventIDMap;
    }

    public Map<String, ArrayList<Event>> getAllEventsMap() {
        return allEventsMap;
    }

    public Map<String, ArrayList<Event>> getCurrentEventsMap() {
        return currentEventsMap;
    }

    public Map<String, Person> getAllPersonsMap() {
        return allPersonsMap;
    }

    public Set<String> getImmediateFamilyMales() {
        return immediateFamilyMales;
    }

    public Set<String> getImmediateFamilyFemales() {
        return immediateFamilyFemales;
    }

    public Set<String> getFatherSideMales() {
        return fatherSideMales;
    }

    public Set<String> getFatherSideFemales() {
        return fatherSideFemales;
    }

    public Set<String> getMotherSideMales() {
        return motherSideMales;
    }

    public Set<String> getMotherSideFemales() {
        return motherSideFemales;
    }

    public Set<String> getEventTypes() {
        return eventTypes;
    }

    public boolean isLifeStoryLinesOn() {
        return lifeStoryLinesOn;
    }

    public void setLifeStoryLinesOn(boolean lifeStoryLinesOn) {
        this.lifeStoryLinesOn = lifeStoryLinesOn;
    }

    public boolean isFamilyTreeLinesOn() {
        return familyTreeLinesOn;
    }

    public void setFamilyTreeLinesOn(boolean familyTreeLinesOn) {
        this.familyTreeLinesOn = familyTreeLinesOn;
    }

    public boolean isSpouseLinesOn() {
        return spouseLinesOn;
    }

    public void setSpouseLinesOn(boolean spouseLinesOn) {
        this.spouseLinesOn = spouseLinesOn;
    }

    public boolean isFatherSideOn() {
        return fatherSideOn;
    }

    public void setFatherSideOn(boolean fatherSideOn) {
        this.fatherSideOn = fatherSideOn;
    }

    public boolean isMotherSideOn() {
        return motherSideOn;
    }

    public void setMotherSideOn(boolean motherSideOn) {
        this.motherSideOn = motherSideOn;
    }

    public boolean isMaleEventsOn() {
        return maleEventsOn;
    }

    public void setMaleEventsOn(boolean maleEventsOn) {
        this.maleEventsOn = maleEventsOn;
    }

    public boolean isFemaleEventsOn() {
        return femaleEventsOn;
    }

    public void setFemaleEventsOn(boolean femaleEventsOn) {
        this.femaleEventsOn = femaleEventsOn;
    }

    public boolean isComingFromPersonOrSearch() {
        return comingFromPersonOrSearch;
    }

    public void setComingFromPersonOrSearch(boolean comingFromPersonOrSearch) {
        this.comingFromPersonOrSearch = comingFromPersonOrSearch;
    }

    public LatLng getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(LatLng lastLocation) {
        this.lastLocation = lastLocation;
    }

    public String getSelectedEventID() {
        return selectedEventID;
    }

    public void setSelectedEventID(String selectedEventID) {
        this.selectedEventID = selectedEventID;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public void clearClient() {

        instance = null;
        user = null;
        authTokenID = null;

        lifeStoryLinesOn = true;
        familyTreeLinesOn = true;
        spouseLinesOn = true;
        fatherSideOn = true;
        motherSideOn = true;
        maleEventsOn = true;
        femaleEventsOn = true;

        isLoggedIn = false;
        comingFromPersonOrSearch = false;
        lastLocation = null;
        selectedEventID = null;

        allPersonsMap.clear();
        childrenMap.clear();

        allEventIDMap.clear();
        allEventsMap.clear();
        currentEventsMap.clear();


        immediateFamilyMales.clear();
        immediateFamilyFemales.clear();

        fatherSideMales.clear();
        fatherSideFemales.clear();
        motherSideMales.clear();
        motherSideFemales.clear();

        eventTypes.clear();
    }
}

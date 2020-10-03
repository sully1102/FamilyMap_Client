package com.sully11.android.familymapclient;

import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import SerializeDeserialize.ReadWrite;
import XPOJOS.Request.LoginRequest;
import XPOJOS.Request.RegisterRequest;
import XPOJOS.Response.EventAllResponse;
import XPOJOS.Response.LoginResponse;
import XPOJOS.Response.PersonAllResponse;
import XPOJOS.Response.RegisterResponse;
import SerializeDeserialize.Deserializer;
import SerializeDeserialize.Serializer;

public class ServerProxy {

    public LoginResponse login(URL url, LoginRequest loginRequest) {
        LoginResponse loginResponse = new LoginResponse();

        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.addRequestProperty("Accept", "application/json");
            connection.connect();

            String json = Serializer.serialize(loginRequest);
            OutputStream os = connection.getOutputStream();
            ReadWrite.writeString(json, os);


            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream reqBody = connection.getInputStream();
                String reqData = ReadWrite.readString(reqBody);

                loginResponse = Deserializer.deserialize(reqData, LoginResponse.class);
                connection.getInputStream().close();

                return loginResponse;
            }
        } catch (Exception e) {
            Log.e("HttpClient", e.getMessage(), e);
        }

        loginResponse.setSuccess(false);
        loginResponse.setMessage("Error: in server Proxy login");
        return loginResponse;
    }


    public RegisterResponse register(URL url, RegisterRequest registerRequest) {
        RegisterResponse registerResponse = new RegisterResponse();

        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.addRequestProperty("Accept", "application/json");
            connection.connect();

            String json = Serializer.serialize(registerRequest);
            OutputStream os = connection.getOutputStream();
            ReadWrite.writeString(json, os);


            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream reqBody = connection.getInputStream();
                String reqData = ReadWrite.readString(reqBody);

                registerResponse = Deserializer.deserialize(reqData, RegisterResponse.class);
                connection.getInputStream().close();

                return registerResponse;
            }
        } catch (Exception e) {
            Log.e("HttpClient", e.getMessage(), e);
        }

        registerResponse.setSuccess(false);
        registerResponse.setMessage("Error: in server Proxy register");
        return registerResponse;
    }


    public EventAllResponse events(URL url, String authTokenID) {
        EventAllResponse eventAllResponse = new EventAllResponse();

        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(false);
            connection.addRequestProperty("Accept", "application/json");
            connection.addRequestProperty("Authorization", authTokenID);
            connection.connect();


            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream reqBody = connection.getInputStream();
                String reqData = ReadWrite.readString(reqBody);

                eventAllResponse = Deserializer.deserialize(reqData, EventAllResponse.class);
                connection.getInputStream().close();

                return eventAllResponse;
            }
        } catch (Exception e) {
            Log.e("HttpClient", e.getMessage(), e);
        }

        eventAllResponse.setSuccess(false);
        eventAllResponse.setMessage("Error: in server Proxy events");
        return eventAllResponse;
    }


    public PersonAllResponse people(URL url, String authTokenID) {
        PersonAllResponse personAllResponse = new PersonAllResponse();

        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(false);
            connection.addRequestProperty("Accept", "application/json");
            connection.addRequestProperty("Authorization", authTokenID);
            connection.connect();


            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream reqBody = connection.getInputStream();
                String reqData = ReadWrite.readString(reqBody);

                personAllResponse = Deserializer.deserialize(reqData, PersonAllResponse.class);
                connection.getInputStream().close();

                return personAllResponse;
            }
        } catch (Exception e) {
            Log.e("HttpClient", e.getMessage(), e);
        }

        personAllResponse.setSuccess(false);
        personAllResponse.setMessage("Error: in server Proxy people");
        return personAllResponse;
    }

    public ServerProxy() {
        //empty constructor
    }
}

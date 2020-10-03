package com.sully11.android.familymapclient.Fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.sully11.android.familymapclient.FamilyData;
import com.sully11.android.familymapclient.Activities.MainActivity;
import com.sully11.android.familymapclient.R;
import com.sully11.android.familymapclient.ServerProxy;

import java.net.MalformedURLException;
import java.net.URL;

import XPOJOS.Request.LoginRequest;
import XPOJOS.Request.RegisterRequest;
import XPOJOS.Response.EventAllResponse;
import XPOJOS.Response.LoginResponse;
import XPOJOS.Response.PersonAllResponse;
import XPOJOS.Response.RegisterResponse;


public class LoginFragment extends Fragment {

    //ServerHost = 10.0.2.2
    //ServerPort = 8080

    private static Context sContext;

    private LoginRequest mLoginRequest;
    private RegisterRequest mRegisterRequest;


    private String serverHost;
    private String serverPort;

    private EditText mHostEditText;
    private EditText mPortEditText;
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private EditText mFirstNameEditText;
    private EditText mLastNameEditText;
    private EditText mEmailEditText;

    private RadioGroup mGenderButton;
    private RadioButton mMaleButton;
    private RadioButton mFemaleButton;

    private Button mLoginButton;
    private Button mRegisterButton;

    public static LoginFragment newInstance(Context newContext) {
        sContext = newContext;
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoginRequest = new LoginRequest();
        mRegisterRequest = new RegisterRequest();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_login, container, false);


        mRegisterButton = (Button) v.findViewById(R.id.button_register);
        mRegisterButton.setEnabled(false);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRegisterButtonClicked();
            }
        });
        mLoginButton = (Button) v.findViewById(R.id.button_login);
        mLoginButton.setEnabled(false);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginButtonClicked();
            }
        });


        mHostEditText = (EditText) v.findViewById(R.id.ServerHostField);
        mHostEditText.addTextChangedListener(new LoginTextWatcher(mHostEditText));
        mHostEditText.addTextChangedListener(new RegisterTextWatcher(mHostEditText));



        mPortEditText = (EditText) v.findViewById(R.id.ServerPortField);
        mPortEditText.addTextChangedListener(new LoginTextWatcher(mPortEditText));
        mPortEditText.addTextChangedListener(new RegisterTextWatcher(mPortEditText));


        mUsernameEditText = (EditText) v.findViewById(R.id.UserNameField);
        mUsernameEditText.addTextChangedListener(new LoginTextWatcher(mUsernameEditText));
        mUsernameEditText.addTextChangedListener(new RegisterTextWatcher(mUsernameEditText));


        mPasswordEditText = (EditText) v.findViewById(R.id.PasswordField);
        mPasswordEditText.addTextChangedListener(new LoginTextWatcher(mPasswordEditText));
        mPasswordEditText.addTextChangedListener(new RegisterTextWatcher(mPasswordEditText));


        mFirstNameEditText = (EditText) v.findViewById(R.id.FirstNameField);
        mFirstNameEditText.addTextChangedListener(new RegisterTextWatcher(mFirstNameEditText));


        mLastNameEditText = (EditText) v.findViewById(R.id.LastNameField);
        mLastNameEditText.addTextChangedListener(new RegisterTextWatcher(mLastNameEditText));


        mEmailEditText = (EditText) v.findViewById(R.id.EmailField);
        mEmailEditText.addTextChangedListener(new RegisterTextWatcher(mEmailEditText));


        mGenderButton = (RadioGroup) v.findViewById(R.id.button_gender);
        mGenderButton.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                checkRegisterFields();
            }
        });
        mMaleButton = (RadioButton) v.findViewById(R.id.button_male);
        mMaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRegisterRequest.setGender("m");
            }
        });
        mFemaleButton = (RadioButton) v.findViewById(R.id.button_female);
        mFemaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRegisterRequest.setGender("f");
            }
        });


        return v;
    }

    private void checkLoginFields() {
        String str = mHostEditText.getText().toString();
        String str1 = mPortEditText.getText().toString();
        String str2 = mUsernameEditText.getText().toString();
        String str3 = mPasswordEditText.getText().toString();

        if(str.isEmpty() || str1.isEmpty() || str2.isEmpty() || str3.isEmpty()) {
            mLoginButton.setEnabled(false);
        } else {
            mLoginButton.setEnabled(true);
        }
    }

    private void checkRegisterFields() {
        String str = mHostEditText.getText().toString();
        String str1 = mPortEditText.getText().toString();
        String str2 = mUsernameEditText.getText().toString();
        String str3 = mPasswordEditText.getText().toString();
        String str4 = mFirstNameEditText.getText().toString();
        String str5 = mLastNameEditText.getText().toString();
        String str6 = mEmailEditText.getText().toString();


        boolean genderButtonNotClicked = false;
        if(mGenderButton.getCheckedRadioButtonId() == -1) {
            genderButtonNotClicked = true;
        }


        if(str.isEmpty() || str1.isEmpty() || str2.isEmpty() || str3.isEmpty() ||
          str4.isEmpty() || str5.isEmpty() || str6.isEmpty() || genderButtonNotClicked) {
            mRegisterButton.setEnabled(false);
        } else {
            mRegisterButton.setEnabled(true);
        }
    }

    private void onLoginButtonClicked() {
        LoginTask loginTask = new LoginTask(this, sContext);
        loginTask.execute(mLoginRequest);
    }

    private void onRegisterButtonClicked() {
        RegisterTask registerTask = new RegisterTask(this, sContext);
        registerTask.execute(mRegisterRequest);
    }

    private class LoginTextWatcher implements TextWatcher {

        private View view;

        private LoginTextWatcher(View view) {
            this.view = view;
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void afterTextChanged(Editable s) {
            String text = s.toString();
            checkLoginFields();
            switch(view.getId()) {
                case R.id.ServerHostField:
                    serverHost = text;
                case R.id.ServerPortField:
                    serverPort = text;
                case R.id.UserNameField:
                    mLoginRequest.setUsername(text);
                case R.id.PasswordField:
                    mLoginRequest.setPassword(text);
            }
        }
    }

    private class RegisterTextWatcher implements TextWatcher {

        private View view;
        private RegisterTextWatcher(View view) {
            this.view = view;
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void afterTextChanged(Editable s) {
            String text = s.toString();
            checkRegisterFields();
            switch(view.getId()) {
                case R.id.ServerHostField:
                    serverHost = text;
                case R.id.ServerPortField:
                    serverPort = text;
                case R.id.UserNameField:
                    mRegisterRequest.setUsername(text);
                case R.id.PasswordField:
                    mRegisterRequest.setPassword(text);
                case R.id.FirstNameField:
                    mRegisterRequest.setFirstName(text);
                case R.id.LastNameField:
                    mRegisterRequest.setLastName(text);
                case R.id.EmailField:
                    mRegisterRequest.setEmail(text);
            }
        }
    }


    private class RegisterTask extends AsyncTask<RegisterRequest, Void, RegisterResponse> {

        private RegisterResponse mRegisterResponse;
        private Fragment mFragment;
        private Context mContext;

        public RegisterTask(Fragment oldFragment, Context oldContext) {
            mRegisterResponse = new RegisterResponse();
            this.mFragment = oldFragment;
            this.mContext = oldContext;
        }

        @Override
        protected RegisterResponse doInBackground(RegisterRequest... registerRequests) {
            try {
                ServerProxy mProxy = new ServerProxy();

                URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/register");


                mRegisterResponse = mProxy.register(url, registerRequests[0]);


                return mRegisterResponse;
            } catch (MalformedURLException e) {
                mRegisterResponse.setSuccess(false);
                mRegisterResponse.setMessage("Error in Register Task");
                return mRegisterResponse;
            }
        }

        @Override
        protected void onPostExecute(RegisterResponse result) {
            if(result.isSuccess()) {
                FamilyData fd = FamilyData.getInstance();
                fd.setAuthToken(result.getAuthToken());

                GetEventsTask eventsTask = new GetEventsTask(mFragment, mContext);
                eventsTask.execute(fd.getAuthToken());

                Toast.makeText(mContext, "Register Succeeded!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "Register Failed!", Toast.LENGTH_LONG).show();
            }
        }
    }


    private class LoginTask extends AsyncTask<LoginRequest, Void, LoginResponse> {

        private LoginResponse mLoginResponse;
        private Fragment mFragment;
        private Context mContext;

        public LoginTask(Fragment oldFragment, Context oldContext) {
            mLoginResponse = new LoginResponse();
            this.mFragment = oldFragment;
            this.mContext = oldContext;
        }

        @Override
        protected LoginResponse doInBackground(LoginRequest... loginRequests) {
            try {
                ServerProxy mProxy = new ServerProxy();

                URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/login");


                mLoginResponse = mProxy.login(url, loginRequests[0]);


                return mLoginResponse;
            } catch (MalformedURLException e) {
                mLoginResponse.setSuccess(false);
                mLoginResponse.setMessage("Error in Login Task");
                return mLoginResponse;
            }
        }

        @Override
        protected void onPostExecute(LoginResponse result) {
            if(result.isSuccess()) {

                FamilyData fd = FamilyData.getInstance();
                fd.setAuthToken(result.getAuthToken());

                GetEventsTask eventsTask = new GetEventsTask(mFragment, mContext);
                eventsTask.execute(fd.getAuthToken());

                Toast.makeText(mContext, "Login Succeeded!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "Login Failed!", Toast.LENGTH_LONG).show();
            }
        }
    }


    private class GetEventsTask extends AsyncTask<String, Void, EventAllResponse> {

        private EventAllResponse mEventAllResponse;
        private Fragment mFragment;
        private Context mContext;

        public GetEventsTask(Fragment oldFragment, Context oldContext) {
            mEventAllResponse = new EventAllResponse();
            this.mFragment = oldFragment;
            this.mContext = oldContext;
        }

        @Override
        protected EventAllResponse doInBackground(String... authTokenID) {
            try {
                ServerProxy mProxy = new ServerProxy();

                URL url = new URL("http://" + serverHost + ":" + serverPort + "/event/");


                mEventAllResponse = mProxy.events(url, authTokenID[0]);


                return mEventAllResponse;
            } catch (MalformedURLException e) {
                mEventAllResponse.setSuccess(false);
                mEventAllResponse.setMessage("Error in GetEvents Task");
                return mEventAllResponse;
            }
        }

        @Override
        protected void onPostExecute(EventAllResponse result) {
            if(result.isSuccess()) {

                FamilyData fd = FamilyData.getInstance();
                fd.setEvents(result.getData());

                GetPeopleTask peopleTask = new GetPeopleTask(mFragment, mContext);
                peopleTask.execute(fd.getAuthToken());

                Toast.makeText(mContext, "Getting Events Succeeded!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "Getting Events Failed!", Toast.LENGTH_LONG).show();
            }
        }
    }


    private class GetPeopleTask extends AsyncTask<String, Void, PersonAllResponse> {

        private PersonAllResponse mPersonAllResponse;
        private Fragment mFragment;
        private Context mContext;

        public GetPeopleTask(Fragment oldFragment, Context oldContext) {
            mPersonAllResponse = new PersonAllResponse();
            this.mFragment = oldFragment;
            this.mContext = oldContext;
        }

        @Override
        protected PersonAllResponse doInBackground(String... authTokenID) {
            try {
                ServerProxy mProxy = new ServerProxy();

                URL url = new URL("http://" + serverHost + ":" + serverPort + "/person/");


                mPersonAllResponse = mProxy.people(url, authTokenID[0]);


                return mPersonAllResponse;
            } catch (MalformedURLException e) {
                mPersonAllResponse.setSuccess(false);
                mPersonAllResponse.setMessage("Error in GetPeople Task");
                return mPersonAllResponse;
            }
        }

        @Override
        protected void onPostExecute(PersonAllResponse result) {
            if(result.isSuccess()) {

                FamilyData fd = FamilyData.getInstance();
                fd.setPeople(result.getData());

                MainActivity mainActivity = (MainActivity) mContext;
                mainActivity.loginSuccess();

                Toast.makeText(mContext, "Getting People Succeeded!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "Getting People Failed!", Toast.LENGTH_LONG).show();
            }
        }
    }

    public LoginFragment() {

    }
}

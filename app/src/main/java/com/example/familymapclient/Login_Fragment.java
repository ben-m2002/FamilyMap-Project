package com.example.familymapclient;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Data.DataCache;
import Models.Person;
import Request.LoginRequest;
import Request.RegisterRequest;
import Result.RegisterResult;
import Tasks.LoginTask;
import Tasks.RegisterTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Login_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Login_Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Listener listener;

    public interface Listener {
        void notifyDone();
    }

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Login_Fragment() {
        // Required empty public constructor
    }

    public void registerListener (Listener listener){this.listener = listener;}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Login_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Login_Fragment newInstance(String param1, String param2) {
        Login_Fragment fragment = new Login_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login_, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstance){
        // View Objects
        Button register = view.findViewById(R.id.register);
        Button login= view.findViewById(R.id.login);
        EditText server_field = (EditText) view.findViewById(R.id.server_field);
        EditText server_port = (EditText) view.findViewById(R.id.server_port_field);
        EditText username = (EditText) view.findViewById(R.id.server_user_name);
        EditText password = (EditText) view.findViewById(R.id.sever_password);
        EditText firstName = (EditText) view.findViewById(R.id.server_first_name);
        EditText lastName = (EditText) view.findViewById(R.id.server_last_name);
        EditText email = (EditText) view.findViewById(R.id.server_email);
        RadioButton male = (RadioButton) view.findViewById(R.id.male_button);
        RadioButton female = (RadioButton)view.findViewById(R.id.female_button);

        // turn off buttons
        register.setEnabled(false);
        login.setEnabled(false);
        final boolean[] registerFieldsChecked = {false,false}; // 1 is for EditTexts, 2 is for buttons
        // turn on the buttons

        TextWatcher watch = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty()
                && !server_field.getText().toString().isEmpty() && !server_port.getText().toString().isEmpty() ){
                    login.setEnabled(true);
                }
                if (!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty()
                        && !server_field.getText().toString().isEmpty() && !server_port.getText().toString().isEmpty() &&
                        !firstName.getText().toString().isEmpty() && !lastName.getText().toString().isEmpty() &&
                        !email.getText().toString().isEmpty()){
                    registerFieldsChecked[0] = true;
                    if (registerFieldsChecked[0] == true && registerFieldsChecked[1] == true){
                        register.setEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (username.getText().toString().isEmpty() || password.getText().toString().isEmpty()
                        || server_field.getText().toString().isEmpty() || server_port.getText().toString().isEmpty() ){
                    login.setEnabled(false);
                }
                if (firstName.getText().toString().isEmpty() || lastName.getText().toString().isEmpty()
                        || email.getText().toString().isEmpty() || username.getText().toString().isEmpty() || password.getText().toString().isEmpty()
                        || server_field.getText().toString().isEmpty() || server_port.getText().toString().isEmpty() ){
                    registerFieldsChecked[0] = false;
                    register.setEnabled(false);
                }
            }

        };

        server_field.addTextChangedListener(watch);
        server_port.addTextChangedListener(watch);
        username.addTextChangedListener(watch);
        password.addTextChangedListener(watch);
        firstName.addTextChangedListener(watch);
        lastName.addTextChangedListener(watch);
        email.addTextChangedListener(watch);
        // register button


        register.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v){
                // Handler

                @SuppressLint("HandlerLeak") Handler uiThreadMessageHandler = new Handler(){
                    @Override
                    public void handleMessage (Message message){
                        // check for error
                        String err = message.getData().getString("message");
                        if (err == null || !err.contains("Error")) {
                            DataCache dc = DataCache.getInstance();
                            Person mainPerson = dc.getUserPerson();
                            Toast t = Toast.makeText(getContext(), mainPerson.getFirstName() + " " + mainPerson.getLastName() + " has logged in!",
                                    Toast.LENGTH_LONG);

                            if (listener != null){
                                listener.notifyDone();
                            }
                        }
                        else{
                            Toast t = Toast.makeText(getContext(),err,
                                    Toast.LENGTH_LONG);
                            t.show();
                        }
                    }
                };

                // create a Register Request

                RegisterRequest request = new RegisterRequest(
                        username.getText().toString(),
                        password.getText().toString(),
                        email.getText().toString(),
                        firstName.getText().toString(),
                        lastName.getText().toString(),
                        "m" // this will be overridden
                );
                // change to female if female was selected
                if (!male.isActivated()){
                    request.setGender("f");
                }
                // Create and Execute a Task

                RegisterTask t = new RegisterTask(uiThreadMessageHandler, request);
                ExecutorService exec = Executors.newSingleThreadExecutor();
                exec.submit(t);
            }
        });

        // login button

        login.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v){
                @SuppressLint("HandlerLeak") Handler uiThreadMessageHandler = new Handler(){
                  @Override
                  public void handleMessage (Message message){
                      System.out.println("HERE");
                      String err = message.getData().getString("message");
                      if (err == null || !err.contains("Error")) {

                          DataCache dc = DataCache.getInstance();
                          Person mainPerson = dc.getUserPerson();
                          Toast t = Toast.makeText(getContext(), mainPerson.getFirstName() + " " + mainPerson.getLastName() + " has logged in!",
                                  Toast.LENGTH_LONG);
                          t.show();
                          if (listener != null){
                              listener.notifyDone();
                          }
                      }
                      else{
                          Toast t = Toast.makeText(getContext(),err,
                                  Toast.LENGTH_LONG);
                          t.show();
                      }
                  }
                };
                LoginRequest request = new LoginRequest(
                        username.getText().toString(),
                        password.getText().toString()
                );
                LoginTask t = new LoginTask(uiThreadMessageHandler,request);
                ExecutorService exec = Executors.newSingleThreadExecutor();
                exec.submit(t);
            }
        });

        // male button
        male.setOnClickListener(new View.OnClickListener(){
           public void onClick (View v){
                // uncheck the other button
               RadioButton o = (RadioButton)view.findViewById(R.id.female_button);
               o.setChecked(false);
               registerFieldsChecked[1] = true;
               if (registerFieldsChecked[0] == true && registerFieldsChecked[1] == true){
                   register.setEnabled(true);
               }
               else{
                   register.setEnabled(false);
               }
           }
        });

        // female button
        female.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v){
                // uncheck the other button
                RadioButton o = (RadioButton)view.findViewById(R.id.male_button);
                o.setChecked(false);
                registerFieldsChecked[1] = true;
                if (registerFieldsChecked[0] == true && registerFieldsChecked[1] == true){
                    register.setEnabled(true);
                }else{
                    register.setEnabled(false);
                }
            }
        });


    }
}
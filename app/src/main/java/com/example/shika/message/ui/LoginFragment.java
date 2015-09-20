package com.example.shika.message.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.shika.message.MessageApplication;
import com.example.shika.message.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoginFragment extends Fragment {
    private EditText mUserName;
    private EditText mPassword;
    private Button mSignUp;

    public LoginFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        TextView textView=(TextView)rootView.findViewById(R.id.textView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),Sign_Up.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });


        mUserName=(EditText)rootView.findViewById(R.id.username);
        mPassword=(EditText)rootView.findViewById(R.id.password);
        mSignUp=(Button)rootView.findViewById(R.id.loginButton);
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUserName.getText().toString();
                String password = mPassword.getText().toString();
                username = username.trim();
                password = password.trim();
                if (username.isEmpty() || password.isEmpty()) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("OOP!!");
                    builder.setMessage("Please fill all fields ");
                    builder.setPositiveButton("OK", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else {
                    getActivity().setProgressBarVisibility(true);

                  ParseUser.logInInBackground(username,password,new LogInCallback() {
                      @Override
                      public void done(ParseUser parseUser, ParseException e) {
                          getActivity().setProgressBarVisibility(false);
                          if (e == null) {

                              MessageApplication.updateParseInstallation(
                                      parseUser);

                              Intent intent = new Intent(getActivity(), MainActivity.class);
                              intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                              intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                              startActivity(intent);


                          } else {
                              AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                              builder.setTitle("OOP!!");
                              builder.setMessage(e.getMessage());
                              builder.setPositiveButton("OK", null);
                              AlertDialog dialog = builder.create();
                              dialog.show();
                          }

                      }
                  });


                }
            }
        });
        return rootView;
    }
}

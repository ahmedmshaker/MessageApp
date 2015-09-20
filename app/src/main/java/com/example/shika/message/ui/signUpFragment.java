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

import com.example.shika.message.MessageApplication;
import com.example.shika.message.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * A placeholder fragment containing a simple view.
 */
public class signUpFragment extends Fragment {
    private EditText mUserName;
    private EditText mEmail;
    private EditText mPassword;
    private Button mSignUp;
    private Button mCancel;

    public signUpFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sign__up, container, false);

        mUserName=(EditText)rootView.findViewById(R.id.username);
        mEmail=(EditText)rootView.findViewById(R.id.email);
        mPassword=(EditText)rootView.findViewById(R.id.password);

        mCancel=(Button)rootView.findViewById(R.id.cancel);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        mSignUp=(Button)rootView.findViewById(R.id.signup);
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username=mUserName.getText().toString();
                String e_mail=mEmail.getText().toString();
                String password=mPassword.getText().toString();
                username=username.trim();
                e_mail=e_mail.trim();
                password=password.trim();
                if (username.isEmpty() || e_mail.isEmpty() || password.isEmpty()){

                    AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                    builder.setTitle("OOP!!");
                    builder.setMessage("Please fill all fields ");
                    builder.setPositiveButton("OK",null);
                    AlertDialog dialog=builder.create();
                    dialog.show();

                }else{
                    ParseUser NewUser=new ParseUser();
                    NewUser.setUsername(username);
                    NewUser.setEmail(e_mail);
                    NewUser.setPassword(password);
                    NewUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e==null){
                                MessageApplication.updateParseInstallation(
                                        ParseUser.getCurrentUser());


                                Intent intent=new Intent(getActivity() , MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                            }else {
                                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                                builder.setTitle("OOP!!");
                                builder.setMessage(e.getMessage());
                                builder.setPositiveButton("OK",null);
                                AlertDialog dialog=builder.create();
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

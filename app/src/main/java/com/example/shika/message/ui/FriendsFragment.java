package com.example.shika.message.ui;


import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.example.shika.message.R;
import com.example.shika.message.adapters.UserAdapter;
import com.example.shika.message.utils.UserConstant;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by shika on 7/31/2015.
 */
public class FriendsFragment extends Fragment {
    private List<ParseUser> mFriends;
    private ParseRelation<ParseUser> mFriendsRelation;
    private ParseUser mCurrentUser;
    private GridView mGridView;
    public FriendsFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_grid, container, false);
        mGridView = (GridView)rootView.findViewById(R.id.friendsGrid);
        TextView textView=(TextView)rootView.findViewById(android.R.id.empty);
        mGridView.setEmptyView(textView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCurrentUser=ParseUser.getCurrentUser();
        mFriendsRelation=mCurrentUser.getRelation(UserConstant.KET_USER_RELATION);
        mFriendsRelation.getQuery().addAscendingOrder(UserConstant.KEY_USERNAME);
        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if (e==null){

                    mFriends=parseUsers;
                    String[] people_array=new String[mFriends.size()];
                    int i=0;
                    for (ParseUser user : parseUsers){
                        people_array[i]=user.getUsername();
                        i++;
                    }
                    if (mGridView.getAdapter() == null) {
                        UserAdapter adapter = new UserAdapter(getActivity(), mFriends);
                        mGridView.setAdapter(adapter);
                    }
                    else {
                        ((UserAdapter)mGridView.getAdapter()).refill(mFriends);
                    }


                }else{
                    Log.e("FriendsActivity",e.getMessage());
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

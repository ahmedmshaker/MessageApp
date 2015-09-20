package com.example.shika.message.ui;

import android.app.AlertDialog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shika.message.R;
import com.example.shika.message.adapters.UserAdapter;
import com.example.shika.message.utils.UserConstant;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class AllPeopleFragment extends Fragment {
    private List<ParseUser> mUser;
    private ParseRelation<ParseUser> mFriendsRelation;
    private ParseUser mCurrentUser;
    private GridView mGridView;

    public AllPeopleFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_grid, container, false);

        mGridView=(GridView)rootView.findViewById(R.id.friendsGrid);
        TextView textView=(TextView)rootView.findViewById(android.R.id.empty);
        mGridView.setOnItemClickListener(mOnItemClickListener);
        mGridView.setEmptyView(textView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        mCurrentUser=ParseUser.getCurrentUser();
        mFriendsRelation=mCurrentUser.getRelation(UserConstant.KET_USER_RELATION);
        ParseQuery<ParseUser> query=ParseUser.getQuery();
        query.addAscendingOrder(UserConstant.KEY_USERNAME);
        query.setLimit(1000);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if (e==null){
                    mUser=parseUsers;
                    String[] people_array=new String[mUser.size()];
                    int i=0;
                    for (ParseUser user : parseUsers){
                        people_array[i]=user.getUsername();
                        i++;
                    }
                    if (mGridView.getAdapter() == null) {
                        UserAdapter adapter = new UserAdapter(getActivity(), mUser);
                        mGridView.setAdapter(adapter);
                    }
                    else {
                        ((UserAdapter)mGridView.getAdapter()).refill(mUser);
                    };
                    addCheckMarks();
                }else{
                    Log.e("ALLPeopleActivity",e.getMessage());
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

 /*   @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (getListView().isItemChecked(position)) {
            mFriendsRelation.add(mUser.get(position));
        }else{
            mFriendsRelation.remove(mUser.get(position));
        }
        mCurrentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e("AllPeople", e.getMessage());
                }
            }
        });

    }*/

    private void addCheckMarks(){
        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if (e==null){
                    for (int i = 0; i <parseUsers.size() ; i++) {
                        ParseUser User=mUser.get(i);
                        for (ParseUser friend : parseUsers){
                            if (friend.getObjectId().equals(User.getObjectId())){
                                mGridView.setItemChecked(i, true);
                            }
                        }
                    }

                }else{
                    Log.e("AllPeople" , e.getMessage());
                }
            }
        });
    }

    protected AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            ImageView checkImageView = (ImageView)view.findViewById(R.id.checkImageView);

            if (mGridView.isItemChecked(position)) {
                // add the friend
                mFriendsRelation.add(mUser.get(position));
                checkImageView.setVisibility(View.VISIBLE);
            }
            else {
                // remove the friend
                mFriendsRelation.remove(mUser.get(position));
                checkImageView.setVisibility(View.INVISIBLE);
            }

            mCurrentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e("all people", e.getMessage());
                    }
                }
            });

        }
    };

}

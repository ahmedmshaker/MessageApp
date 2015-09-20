package com.example.shika.message.ui;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.shika.message.adapters.MessageAdapter;
import com.example.shika.message.R;
import com.example.shika.message.utils.UserConstant;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by shika on 7/31/2015.
 */
public class InboxFragment extends ListFragment {

    protected List<ParseObject> mMessages;
    private Uri mMediaUri;
    SwipeRefreshLayout mSwipeRefreshLayout;
    public InboxFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.inbox_fragment, container, false);
        mSwipeRefreshLayout=(SwipeRefreshLayout)rootView.findViewById(R.id.swipRefershLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                retrieveAll();
            }
        });
        mSwipeRefreshLayout.setColorScheme(
                R.color.swipeRefresh1,
                R.color.swipeRefresh2,
                R.color.swipeRefresh3,
                R.color.swipeRefresh4);

        return rootView;
    }
    @Override
    public void onResume() {
        super.onResume();

        //getActivity().setProgressBarIndeterminateVisibility(true);

        retrieveAll();
    }

    private void retrieveAll() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(UserConstant.CLASS_MESSAGES);
        query.whereEqualTo(UserConstant.KEY_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());
        query.addDescendingOrder(UserConstant.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {
               // getActivity().setProgressBarIndeterminateVisibility(false);

                if (mSwipeRefreshLayout.isRefreshing() ){
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                if (e == null) {
                    // We found messages!
                    mMessages = messages;

                    String[] usernames = new String[mMessages.size()];
                    int i = 0;
                    for(ParseObject message : mMessages) {
                        usernames[i] = message.getString(UserConstant.KEY_SENDER_NAME);
                        i++;
                    }
                    MessageAdapter adapter = new MessageAdapter(
                            getListView().getContext(),
                            mMessages);
                    setListAdapter(adapter);

                }
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ParseObject mparse=mMessages.get(position);
        String filType=mparse.getString(UserConstant.KEY_FILE_TYPE);
        ParseFile file=mparse.getParseFile(UserConstant.KEY_FILE);
        mMediaUri=Uri.parse(file.getUrl());
        if (filType.equals(UserConstant.TYPE_IMAGE)){
            Intent mIntent=new Intent(getActivity(),ViewImageMessage.class);
            mIntent.setData(mMediaUri);
            startActivity(mIntent);
        }else{

            Intent intent = new Intent(Intent.ACTION_VIEW, mMediaUri);
            intent.setDataAndType(mMediaUri, "video/*");
            startActivity(intent);

        }
    }
}

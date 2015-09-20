package com.example.shika.message.ui;

import android.app.AlertDialog;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shika.message.utils.FileHelper;
import com.example.shika.message.R;
import com.example.shika.message.adapters.UserAdapter;
import com.example.shika.message.utils.UserConstant;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


public class SendMessageActivity extends ActionBarActivity{
   // private MenuItem mMenuItem;
    //SendMessageFragment mySend;
   public static final String TAG = SendMessageActivity.class.getSimpleName();

    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected List<ParseUser> mFriends;
    protected MenuItem mSendMenuItem;
    protected Uri mMediaUri;
    protected String mFileType;
    GridView mGridView;

    TextView emptyTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       //requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.user_grid);
        // Show the Up button in the action bar.
///        setupActionBar();
        mGridView=(GridView)findViewById(R.id.friendsGrid);
        emptyTextView=(TextView)findViewById(android.R.id.empty);

        mGridView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mGridView.getCheckedItemCount() > 0) {
                    mSendMenuItem.setVisible(true);
                }
                else {
                    mSendMenuItem.setVisible(false);
                }
                ImageView checkImageView = (ImageView)view.findViewById(R.id.checkImageView);

                if (mGridView.isItemChecked(position)) {
                    // add the recipient
                    checkImageView.setVisibility(View.VISIBLE);
                }
                else {
                    // remove the recipient
                    checkImageView.setVisibility(View.INVISIBLE);
                }


            }
        });

        mMediaUri = getIntent().getData();

        mFileType = getIntent().getExtras().getString(UserConstant.KEY_FILE_TYPE);
        Toast.makeText(this,mMediaUri.toString()+mFileType,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(UserConstant.KET_USER_RELATION);

        //setProgressBarIndeterminateVisibility(true);

        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
        query.addAscendingOrder(UserConstant.KEY_USERNAME);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
          //      setProgressBarIndeterminateVisibility(false);

                if (e == null) {
                    mFriends = friends;

                    String[] usernames = new String[mFriends.size()];
                    int i = 0;
                    for(ParseUser user : mFriends) {
                        usernames[i] = user.getUsername();
                        i++;
                    }
                    if (mGridView.getAdapter() == null) {
                        UserAdapter adapter = new UserAdapter(SendMessageActivity.this, mFriends);
                        mGridView.setAdapter(adapter);
                    }
                    else {
                        ((UserAdapter)mGridView.getAdapter()).refill(mFriends);
                    }
                }
                else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(SendMessageActivity.this);
                    builder.setMessage(e.getMessage())
                            .setTitle("Error")
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    /**
     * Set up the {@link android.app.ActionBar}.
     */
    private void setupActionBar() {

///        getActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipincept, menu);
        mSendMenuItem = menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send:
                createMessage();
               /* if (message == null) {
                    // error
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Message not Found")
                            .setTitle("Error Message")
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
                    message.saveInBackground();
                 //   send(message);
                    finish();
                }*/
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    protected void createMessage() {


        ParseObject message = new ParseObject(UserConstant.CLASS_MESSAGES);
        message.put(UserConstant.KEY_SENDER_ID, ParseUser.getCurrentUser().getObjectId());
        message.put(UserConstant.KEY_SENDER_NAME, ParseUser.getCurrentUser().getUsername());
        message.put(UserConstant.KEY_RECIPIENT_IDS, getRecipientIds());
        message.put(UserConstant.KEY_FILE_TYPE, mFileType);



        byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);

        if (fileBytes == null) {
            return ;
        }
        else {
            if (mFileType.equals(UserConstant.TYPE_IMAGE)) {
                fileBytes = FileHelper.reduceImageForUpload(fileBytes);
            }

            String fileName = FileHelper.getFileName(this, mMediaUri, mFileType);
            ParseFile file = new ParseFile(fileName, fileBytes);
            message.put(UserConstant.KEY_FILE, file);
            if (message == null) {
                // error
                AlertDialog.Builder builder = new AlertDialog.Builder(SendMessageActivity.this);
                builder.setMessage("Message not Found")
                        .setTitle("Error Message")
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else {
                message.saveInBackground();
                Toast.makeText(SendMessageActivity.this,"Message send",Toast.LENGTH_LONG).show();
                sendPushNotifications();
                //   send(message);
                finish();
            }



        }
    }

    protected ArrayList<String> getRecipientIds() {
        ArrayList<String> recipientIds = new ArrayList<String>();
        for (int i = 0; i < mGridView.getCount(); i++) {
            if (mGridView.isItemChecked(i)) {
                recipientIds.add(mFriends.get(i).getObjectId());
            }
        }
        return recipientIds;
    }

    protected void send(ParseObject message) {
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // success!
                    Toast.makeText(SendMessageActivity.this,"message send successful", Toast.LENGTH_LONG).show();
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SendMessageActivity.this);
                    builder.setMessage(e.getMessage())
                            .setTitle("Oops")
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }


    protected void sendPushNotifications() {
        ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
        query.whereContainedIn(UserConstant.KEY_USER_ID, getRecipientIds());

        // send push notification
        ParsePush push = new ParsePush();
        push.setQuery(query);

        push.setMessage(getString(R.string.push_message,
                ParseUser.getCurrentUser().getUsername()));
        push.sendInBackground();
    }

}

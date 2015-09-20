package com.example.shika.message.ui;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;


import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.shika.message.R;
import com.example.shika.message.adapters.SectionsPagerAdapter;
import com.example.shika.message.utils.UserConstant;
import com.parse.ParseAnalytics;
import com.parse.ParseUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends FragmentActivity implements ActionBar.TabListener {


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    private static final int KEY_Take_CAMERA=0;
    private static final int KEY_Take_video=1;
    private static final int KEY_Choose_CAMERA=2;
    private static final int KEY_Choose_Video=3;
    private static final int KEY_TYPE_IMAGES=4;
    private static final int KEY_TYPE_VIDEOS=5;
    public static final int FILE_SIZE_LIMIT = 1024*1024*10;

    private static Uri mMediaUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        ParseUser CurrentUser=ParseUser.getCurrentUser();




        if(CurrentUser==null){

            NavigationOfIntent();
        }
        // Set up the action bar.
        final ActionBar actionBar = getActionBar();

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setIcon(mSectionsPagerAdapter.getIcon(i))

                            .setTabListener(this));
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // add it to the Gallery

            if (requestCode == KEY_Choose_CAMERA || requestCode == KEY_Choose_Video) {
                if (data == null) {
                    Toast.makeText(this, "file not found", Toast.LENGTH_LONG).show();
                }
                else {
                    mMediaUri = data.getData();
                }
                if (requestCode == KEY_Choose_Video) {
                    // make sure the file is less than 10 MB
                    int fileSize = 0;
                    InputStream inputStream = null;

                    try {
                        inputStream = getContentResolver().openInputStream(mMediaUri);
                        fileSize = inputStream.available();
                    }
                    catch (FileNotFoundException e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    catch (IOException e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    finally {
                        try {
                            inputStream.close();
                        } catch (IOException e) { /* Intentionally blank */ }
                    }

                    if (fileSize >= FILE_SIZE_LIMIT) {
                        Toast.makeText(this, "file more than 10 m must be less than 10 m", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

            }
            else{
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(mMediaUri);


                sendBroadcast(mediaScanIntent);
            }
            Intent intent=new Intent(MainActivity.this , SendMessageActivity.class);
            intent.setData(mMediaUri);
            String fileType;
            if (requestCode == KEY_Choose_CAMERA || requestCode == KEY_Take_CAMERA) {
                fileType = UserConstant.TYPE_IMAGE;
            }
            else {
                fileType = UserConstant.TYPE_VIDEO;
            }

            intent.putExtra(UserConstant.KEY_FILE_TYPE, fileType);
            startActivity(intent);
        }


        else if (resultCode != RESULT_CANCELED) {
            Toast.makeText(this, "Oops!!!" ,Toast.LENGTH_LONG).show();
        }
    }



    private void NavigationOfIntent() {
        Intent intent=new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.logout :
                ParseUser.logOut();
                NavigationOfIntent();
                break;
            case R.id.all_people:
                Intent intent = new Intent(MainActivity.this, AllPeople.class);
                startActivity(intent);
                break;
            case R.id.camera:
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setItems(R.array.choose_of_camera,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       switch (which){
                           case 0:
                               Intent TakePhotoCamera=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                               mMediaUri=getOutputMediaUri(KEY_TYPE_IMAGES);
                               if (mMediaUri==null){

                               }
                               TakePhotoCamera.putExtra(MediaStore.EXTRA_OUTPUT,mMediaUri);

                               startActivityForResult(TakePhotoCamera,KEY_Take_CAMERA);
                               break;
                           case 1:
                               Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                               mMediaUri = getOutputMediaUri(KEY_TYPE_VIDEOS);
                               if (mMediaUri == null) {
                                   // display an error
                                   Toast.makeText(MainActivity.this, "error in external storage",
                                           Toast.LENGTH_LONG).show();
                               }
                               else {
                                   videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                                   videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
                                   videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0); // 0 = lowest res
                                   startActivityForResult(videoIntent, KEY_Take_video);
                               }

                               break;
                           case 2:
                               Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                               choosePhotoIntent.setType("image/*");
                               startActivityForResult(choosePhotoIntent, KEY_Choose_CAMERA);

                               break;
                           case 3:
                               Intent chooseVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                               chooseVideoIntent.setType("video/*");
                               Toast.makeText(MainActivity.this, "make sure file size less than 10", Toast.LENGTH_LONG).show();
                               startActivityForResult(chooseVideoIntent, KEY_Choose_Video);

                               break;
                       }
                    }


                    private Uri getOutputMediaUri(int mediaType){
                        if (isExternalSotrageAvialble()){
                            String appName = MainActivity.this.getString(R.string.app_name);
                            File mediaStorageDir = new File(
                                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                                    appName);

                            // 2. Create our subdirectory
                            if (! mediaStorageDir.exists()) {
                                if (! mediaStorageDir.mkdirs()) {
                                    Log.e("main", "Failed to create directory.");
                                    return null;
                                }
                            }

                            // 3. Create a file name
                            // 4. Create the file
                            File mediaFile=null;
                            Date now = new Date();
                            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now);

                            String path = mediaStorageDir.getPath() + File.separator;
                            if (mediaType == KEY_TYPE_IMAGES) {
                                mediaFile = new File(path + "IMG_" + timestamp + ".jpg");
                            }
                            else if (mediaType == KEY_TYPE_VIDEOS) {
                                mediaFile = new File(path + "VID_" + timestamp + ".mp4");
                            }

                            Log.d("main", "File: " + Uri.fromFile(mediaFile));

                            // 5. Return the file's URI
                            return Uri.fromFile(mediaFile);
                        }
                        else {
                            return null;
                        }



                    }
                    private Boolean isExternalSotrageAvialble(){
                        String state= Environment.getExternalStorageState();
                        if (state.equals(Environment.MEDIA_MOUNTED)){
                            return  true;
                        }else{
                            return false;
                        }
                    }
                });
                AlertDialog dialog=builder.create();
                dialog.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }


    /**
     * A placeholder fragment containing a simple view.
     */


}

package digital.starein.com.galleryapp;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TabLayout mtabLayout;
    private ViewPager mViewPager;
    private PagerAdapter viewPagerAdapter;
    public static ArrayList<String> imageLocations,foldersWithImages,thumbnailForFolders,locationWithoutImagePart,savedImage;
    public static ArrayList<Integer> imagesCountInFolder;
    public static int totalNumberOfPictures;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL = 1;
    private TextView navUsername;
    public static int tab=1;
    private static final int REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navUsername = (TextView) headerView.findViewById(R.id.totalPhotos);
        navUsername.setText("0");
        savedImage=getArrayList("savedImage");
        if (savedImage == null) {
            savedImage=new ArrayList<>();
        }
        navigationView.setNavigationItemSelectedListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // Show an explanation to the user *asynchronously* -- don't block

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL);
                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL);
                }
            } else {
                // Permission has already been granted
                getImagesPathToFileLocation();
                Tab2.alter=true;
                Tab3.alter=true;
                Tab4.alter=true;
                Tab1.alter=true;

            }
        }else{
            getImagesPathToFileLocation();
            Tab2.alter=true;
            Tab3.alter=true;
            Tab4.alter=true;
            Tab1.alter=true;
        }
        resetStatus();
        setViewPager();
    }

    public ArrayList<String> getArrayList(String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }




    void resetStatus(){
        SharedPreferences saved_values = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor=saved_values.edit();
        editor.putString("isPortrait","f");
        editor.putInt("locc",0);
        editor.apply();
    }

    private void setViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(viewPagerAdapter);
        mViewPager.setCurrentItem(tab);
        mtabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mtabLayout.setupWithViewPager(mViewPager);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    getImagesPathToFileLocation();
                    Intent i=new Intent(MainActivity.this,MainActivity.class);
                    finish();
                    startActivity(i);

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL);
                    Toast.makeText(this,"Storage permission is necessary",Toast.LENGTH_LONG).show();

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public void getImagesPathToFileLocation() {     //Method to return all available images on device


        Uri uri;
        Cursor cursor;
        int column_index_data;
        String PathOfImage;

        imageLocations = new ArrayList<>();
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        cursor = getApplication().getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        while (cursor.moveToNext()) {
            PathOfImage = cursor.getString(column_index_data);
            imageLocations.add(PathOfImage);
        }

        Collections.reverse(imageLocations);
        totalNumberOfPictures = imageLocations.size();

        getFoldersContainingImages();
        navUsername.setText(" "+totalNumberOfPictures);


    }

    void getFoldersContainingImages(){
        foldersWithImages=new ArrayList<>();
        thumbnailForFolders=new ArrayList<>();
        imagesCountInFolder=new ArrayList<>();
        locationWithoutImagePart=new ArrayList<>();

        for(String str:imageLocations){
            String tempStr=str.substring(0,str.lastIndexOf("/")); ///storage/emulated/0/DCIM/Camera
            locationWithoutImagePart.add(tempStr);
            String tempFolder=tempStr.substring(tempStr.lastIndexOf("/")+1); //Camera

            int flag=0;
            int foldersWithImagesSize=foldersWithImages.size();
            for(int i=0;i<foldersWithImagesSize;i++){
                if (tempFolder.equals(foldersWithImages.get(i))){
                    flag++;
                }
            }
            if(flag==0){
                foldersWithImages.add(tempFolder);
                thumbnailForFolders.add(str);
                imagesCountInFolder.add(0);
            }
        }
        countImagesCount();

    }

    void countImagesCount() {
        for (String str : locationWithoutImagePart) {
            String testStr = str.substring(str.lastIndexOf("/") + 1);

            for (int i = 0; i < foldersWithImages.size(); i++) {
                if (testStr.equals(foldersWithImages.get(i)))
                    imagesCountInFolder.set(i, imagesCountInFolder.get(i) + 1);
            }
        }
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            finishAffinity();
            System.exit(0);
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}

package digital.starein.com.galleryapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yalantis.ucrop.UCrop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class PhotoView extends AppCompatActivity {

    private static final double IMAGE_MAX_SIZE =1200 ;
    ViewPager viewPager;
    public static ArrayList<String> imagesInAlbum;
    Toolbar toolbar;
    private ArrayList<String> savedImageUri=new ArrayList<>();
    private BottomNavigationView bottomBar;
    public static Uri currentImageUri;
    public static int currentImagePos;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(PhotoView.this,MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.photo_view_menu, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.share_btn:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");
                File file=new File(currentImageUri.getPath());
                Uri uri=FileProvider.getUriForFile(PhotoView.this, BuildConfig.APPLICATION_ID + ".provider",file);
                share.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(share, "Share Image"));
                break;

            case R.id.fav_btn:
                    if(savedImageUri.contains(currentImageUri.getPath())){
                        savedImageUri.remove(currentImageUri.getPath());
                        Toast.makeText(PhotoView.this,"Removed from Fav",Toast.LENGTH_LONG).show();
                        saveArrayList(savedImageUri,"savedImage");


                    }else{
                        savedImageUri.add(currentImageUri.getPath());
                        Toast.makeText(PhotoView.this,"Added To Fav",Toast.LENGTH_LONG).show();
                        saveArrayList(savedImageUri,"savedImage");
                    }

                break;

            case R.id.back_btn:
                Intent i=new Intent(PhotoView.this,MainActivity.class);
                startActivity(i);
                finish();
                break;
        }
        return true;
    }

    public void saveArrayList(ArrayList<String> list, String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(PhotoView.this);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public ArrayList<String> getArrayList(String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(PhotoView.this);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);
        Intent intent = getIntent();
        int currentImageLocation = intent.getIntExtra("CURRENT_IMAGE_LOC", 0);
        viewPager = findViewById(R.id.viewpager_in_imageViewActivity);
        viewPager.setAdapter(new myPagerAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(currentImageLocation);
        currentImagePos=currentImageLocation;
        savedImageUri=getArrayList("savedImage");
        if(savedImageUri==null){
            savedImageUri=new ArrayList<>();
        }
        bottomBar = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomBar.getMenu().getItem(0).setCheckable(false);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
               currentImagePos=i;
               currentImageUri=Uri.fromFile(new File(imagesInAlbum.get(currentImagePos)));
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        currentImageUri= Uri.fromFile(new File(imagesInAlbum.get(currentImageLocation)));

      bottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
              switch (menuItem.getItemId()){
                  case R.id.autofix:
                      Intent i=new Intent(PhotoView.this,AutoFix.class);
                      i.putExtra("uri",currentImageUri.toString());
                      i.putExtra("position",currentImagePos);
                      startActivity(i);
                      finish();
                      break;

                  case R.id.crop:
                    /*  Intent in=new Intent(PhotoView.this,CropActivity.class);
                      in.putExtra("uri",currentImageUri.toString());
                      in.putExtra("position",currentImagePos);
                      startActivity(in);
                      finish();*/

                      UCrop.of(currentImageUri,currentImageUri).start(PhotoView.this);
                      File file=new File(currentImageUri.toString());
                      if(file.exists()){

                          file.delete();
                          sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

                      }
                      break;

                  case R.id.edit:
                      Intent inn=new Intent(PhotoView.this,EditPhoto.class);
                      inn.putExtra("uri",currentImageUri.toString());
                      inn.putExtra("position",currentImagePos);
                      startActivity(inn);
                      finish();
                      break;

                  case R.id.delete:
                      deleteImage();
                      MainActivity.imageLocations.remove(currentImagePos);
                      Tab2.adapter.notifyDataSetChanged();
                      File dile=new File(currentImageUri.getPath());
                      sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(dile)));
                      Intent in=new Intent(PhotoView.this,PhotoView.class);
                      in.putExtra("currentImagePos",currentImagePos+1);
                      startActivity(in);
                      finish();
                      break;

                  case R.id.text:
                      Intent innn=new Intent(PhotoView.this,TextActivity.class);
                      innn.putExtra("uri",currentImageUri.toString());
                      innn.putExtra("position",currentImagePos);
                      startActivity(innn);
                      finish();
                      break;
              }
              return false;
          }
      });

    }




    public void deleteImage() {
        String file_dj_path = currentImageUri.getPath();
        File fdelete = new File(currentImageUri.getPath());
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                Log.e("-->", "file Deleted :" + file_dj_path);
                callBroadCast();
            } else {
                Log.e("-->", "file not Deleted :" + file_dj_path);
            }
        }
    }

    public void callBroadCast() {
        if (Build.VERSION.SDK_INT >= 14) {
            Log.e("-->", " >= 14");
            MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStorageDirectory().toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                /*
                 *   (non-Javadoc)
                 * @see android.media.MediaScannerConnection.OnScanCompletedListener#onScanCompleted(java.lang.String, android.net.Uri)
                 */
                public void onScanCompleted(String path, Uri uri) {
                    Log.e("ExternalStorage", "Scanned " + path + ":");
                    Log.e("ExternalStorage", "-> uri=" + uri);
                }
            });
        } else {
            Log.e("-->", " < 14");
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse(currentImageUri.toString())));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {

            final Uri resultUri = UCrop.getOutput(data);
            String path = resultUri.getPath(); // "/mnt/sdcard/FileName.mp3"
           // imageView.setImageURI(resultUri);
            Intent in= new Intent(PhotoView.this,PhotoView.class);
            in.putExtra("CURRENT_IMAGE_LOC",currentImagePos);
            startActivity(in);

        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            //Toast.makeText(CropActivity.this,"Press back",Toast.LENGTH_LONG).show();
        }
    }



    class myPagerAdapter extends FragmentPagerAdapter {

        public myPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Bundle bundle = new Bundle();
            bundle.putString("LOC", imagesInAlbum.get(i));
            ImageViewFragment imageViewingFragment = new ImageViewFragment();
            imageViewingFragment.setArguments(bundle);
            return imageViewingFragment;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
          //  currentImagePos=position;
            return super.instantiateItem(container, position);
        }

        @Override
        public long getItemId(int position) {
            //currentImagePos=position;
            return super.getItemId(position);

        }

        @Override
        public int getCount() {
            return imagesInAlbum.size();
        }
    }

    private Bitmap decodeFile(File f){
        Bitmap b = null;

        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        int scale = 1;
        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
            scale = (int)Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE /
                    (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
        }

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        try {
            fis = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        b = BitmapFactory.decodeStream(fis, null, o2);
        try {
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return b;
    }

/*
    public static void setTranslucentStatusBar(Window window) {
        if (window == null) return;
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= Build.VERSION_CODES.LOLLIPOP) {
            setTranslucentStatusBarLollipop(window);
        } else if (sdkInt >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatusBarKiKat(window);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static void setTranslucentStatusBarLollipop(Window window) {
        window.setStatusBarColor(
                window.getContext()
                        .getResources()
                        .getColor(R.color.colorAccent));
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static void setTranslucentStatusBarKiKat(Window window) {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }
    */
}
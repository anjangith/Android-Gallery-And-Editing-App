package digital.starein.com.galleryapp;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import java.util.ArrayList;

public class AlbumActivity extends AppCompatActivity {
    public static ArrayList<String> imagesPaths,imagesToSend;
    String nameOfAlbum;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.common_menu, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.back_btn:
                finish();
                break;

        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        setRequestedOrientation(getResources().getConfiguration().orientation);
        final RecyclerView recyclerView=findViewById(R.id.recyclerView_in_albumActivity);
        imagesPaths=new ArrayList<>();
        Intent intent=getIntent();
        nameOfAlbum=intent.getStringExtra("FOLDER_NAME");
        char whoCalled=intent.getCharExtra("WHO_CALLED",'p');


        recyclerView.setLayoutManager(new GridLayoutManager(this,3));

        if (whoCalled=='v')
            recyclerView.setAdapter(new AdapterPhotoGrid(getApplication(),imagesToSend,R.layout.video_grid,R.id.imageView_in_item));
        else
            recyclerView.setAdapter(new AdapterPhotoGrid(getApplication(),imagesToSend,R.layout.photo_grid,R.id.imageView_in_item));
    }
}

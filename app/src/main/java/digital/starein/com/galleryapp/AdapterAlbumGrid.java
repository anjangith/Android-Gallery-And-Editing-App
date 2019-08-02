package digital.starein.com.galleryapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.io.File;
import java.util.ArrayList;

public class AdapterAlbumGrid extends RecyclerView.Adapter<AdapterAlbumGrid.myVH> {
    private Context context;
    private ArrayList<String> itemList,folderName;
    private int parentLayout,eachItem,itemName,itemCount;
    char whoCalledMe;
    private ArrayList<Integer> imagesCount;

    public AdapterAlbumGrid(Context context, ArrayList<String> itemList,ArrayList<String> folderName,ArrayList<Integer> imagesCount, int parentLayout, int eachItem,int itemName,int itemCount,char whoCalledMe) {
        this.context = context;
        this.itemList = itemList;
        this.parentLayout = parentLayout;
        this.eachItem = eachItem;
        this.folderName=folderName;
        this.itemName=itemName;
        this.imagesCount=imagesCount;
        this.itemCount=itemCount;
        this.whoCalledMe=whoCalledMe;
    }

    @NonNull
    @Override
    public myVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        SharedPreferences saved_values = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor=saved_values.edit();
        editor.putString("isPortrait","f");
        editor.putInt("locc",0);
        editor.apply();
        View v= LayoutInflater.from(context).inflate(parentLayout,viewGroup,false);
        return new myVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull myVH myVH, int i) {
        /*File file=new File(itemList.get(i));
        Bitmap bitmap= BitmapFactory.decodeFile(file.getAbsolutePath());
        myVH.imageView.setImageBitmap(bitmap);*/
        Glide.with(context)
                .load(Uri.fromFile(new File(itemList.get(i))))
                .centerCrop()
                .override(200,200 )
                .into(myVH.imageView);

        myVH.textView.setText(folderName.get(i));
        myVH.textView1.setText(String.valueOf(imagesCount.get(i)));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class myVH extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView textView,textView1;
        private myVH(@NonNull final View itemView) {
            super(itemView);
            imageView=itemView.findViewById(eachItem);
            textView=itemView.findViewById(itemName);
            textView1=itemView.findViewById(itemCount);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(whoCalledMe=='p')
                        prepareImages(textView.getText().toString());
                    else if(whoCalledMe=='v')
                        prepareVideos(textView.getText().toString());
                    if (imagesCount.get(getLayoutPosition())>1)
                        startTheActivity((String)textView.getText());
                    else
                        itsOneImageOnly(getLayoutPosition());
                }
            });
        }
    }

    private void startTheActivity(String str){
        Intent intent = new Intent(context, AlbumActivity.class);
        intent.putExtra("FOLDER_NAME", str);
        intent.putExtra("WHO_CALLED", whoCalledMe);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    private void itsOneImageOnly(int pos){

        if (whoCalledMe=='p') {
            Intent intent = new Intent(context, PhotoView.class);
            intent.putExtra("CURRENT_IMAGE_LOC", pos);
            PhotoView.imagesInAlbum = itemList;
            MainActivity.tab=2;
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        else {
            Intent intent = new Intent(context, VideoPlayerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            MainActivity.tab=3;
        }
    }

    private void prepareImages(String nameOfAlbum){
        AlbumActivity.imagesToSend=new ArrayList<>();
        for(int i=0;i<MainActivity.totalNumberOfPictures;i++){
            String str=MainActivity.locationWithoutImagePart.get(i);
            String testStr=str.substring(str.lastIndexOf("/")+1);
            if (testStr.equals(nameOfAlbum)) {
                AlbumActivity.imagesToSend.add(MainActivity.imageLocations.get(i));
                //Log.d("myTest",MainActivity.imageLocations.get(i));
            }
        }
    }
    private void prepareVideos(String nameOfAlbum){

        Log.d("myTest","ME CALLED");
        AlbumActivity.imagesToSend=new ArrayList<>();
        for(int i = 0; i< Tab4.videoList.size(); i++){
            String str=Tab4.locationWithoutVideoPart.get(i);
            String testStr=str.substring(str.lastIndexOf("/")+1);
            if (testStr.equals(nameOfAlbum)) {
                AlbumActivity.imagesToSend.add(Tab4.videoList.get(i));
                //Log.d("myTest",MainActivity.imageLocations.get(i));
            }
        }
    }
}

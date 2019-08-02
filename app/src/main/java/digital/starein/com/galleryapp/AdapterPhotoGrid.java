package digital.starein.com.galleryapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class AdapterPhotoGrid extends RecyclerView.Adapter<AdapterPhotoGrid.myVH> {

    private Context context;
    public  static ArrayList<String> itemList;
    private int parentLayout,eachItem;

    public AdapterPhotoGrid(Context context, ArrayList<String> itemList, int parentLayout, int eachItem) {
        this.context = context;
        this.itemList = itemList;
        this.parentLayout = parentLayout;
        this.eachItem = eachItem;
    }

    @NonNull
    @Override
    public myVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v= LayoutInflater.from(context).inflate(parentLayout,viewGroup,false);
        return new myVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull myVH myVH, int i) {
        Glide.with(context)
                .load(Uri.fromFile(new File(itemList.get(i))))
                .centerCrop()
                .override(200,200 )
                .into(myVH.imageView);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class myVH extends RecyclerView.ViewHolder{

        ImageView imageView;
        public myVH(@NonNull final View itemView) {
            super(itemView);
            imageView=itemView.findViewById(eachItem);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(parentLayout==R.layout.video_grid) {

                        VideoPlayerActivity.currentVideoIndex=getLayoutPosition();
                        VideoPlayerActivity.videoLocations=itemList;
                        VideoPlayerActivity.maxLimit=itemList.size();
                        MainActivity.tab=3;
                        Intent intent=new Intent(context, VideoPlayerActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                    else
                    {
                        Intent intent = new Intent(context, PhotoView.class);
                        MainActivity.tab=2;
                        intent.putExtra("CURRENT_IMAGE_LOC", getLayoutPosition());
                        PhotoView.imagesInAlbum = itemList;
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                    }
                }
            });
        }
    }

}

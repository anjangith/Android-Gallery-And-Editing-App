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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import java.io.File;
import java.util.ArrayList;


public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.myVH> {

    private Context context;
    private Activity activity;
    public  ArrayList<String> itemList=new ArrayList<>();
    private int parentLayout,eachItem;

    public PhotoAdapter(Context context,Activity ctx, ArrayList<String> itemList, int parentLayout, int eachItem) {
        this.context = context;
        this.activity=ctx;
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
                .error(R.drawable.ic_error_outline_black_24dp).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(myVH.imageView);

      /*  Glide.with(context)
                .load(Uri.fromFile(new File(itemList.get(i))))
                .centerCrop()
                .override(200,200 ).signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                .into(myVH.imageView);*/
    }

    @Override
    public int getItemCount() {
        return (itemList == null) ? 0 : itemList.size();
    }


    class myVH extends RecyclerView.ViewHolder{

        ImageView imageView;
        public myVH(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(eachItem);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, PhotoView.class);
                    intent.putExtra("CURRENT_IMAGE_LOC", getLayoutPosition());
                    PhotoView.imagesInAlbum = itemList;
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    MainActivity.tab=1;
                    ((Activity)context).finish();

                }
            });
        }
    }

    /*private ArrayList<String> getAllShownImagesPath(Activity activity) {
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {

            absolutePathOfImage = cursor.getString(column_index_data);
            listOfAllImages.add(absolutePathOfImage);
        }

        Collections.reverse(listOfAllImages);
        return listOfAllImages;
    }*/
}



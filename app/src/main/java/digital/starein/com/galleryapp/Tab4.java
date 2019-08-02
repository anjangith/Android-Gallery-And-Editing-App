package digital.starein.com.galleryapp;


import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashSet;


/**
 * A simple {@link Fragment} subclass.
 */
public class Tab4 extends Fragment {


    public static ArrayList<String> videoList,foldesWithVideos,videoThumbnails,locationWithoutVideoPart;
    public  static ArrayList<Integer> videoCountInFolders;
    public static boolean alter=false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        videoList=new ArrayList<>();
        videoList=getAllMedia();
        getFoldersContainingVideos();

        View v=LayoutInflater.from(getContext()).inflate(R.layout.fragment_tab4,container,false);
        RecyclerView recyclerView=v.findViewById(R.id.recyclerView_in_video_fragment);
        if(alter) {
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            recyclerView.setAdapter(new AdapterAlbumGrid(getActivity().getApplicationContext(), videoThumbnails, foldesWithVideos, videoCountInFolders, R.layout.album_grid,
                    R.id.imageView_in_item_album, R.id.folder_name_item, R.id.images_count_item, 'v'));

        }
        return v;
    }



    void getFoldersContainingVideos(){


        foldesWithVideos=new ArrayList<>();
        videoThumbnails=new ArrayList<>();
        locationWithoutVideoPart=new ArrayList<>();
        videoCountInFolders=new ArrayList<>();

        for(String str:videoList){
            String tempStr=str.substring(0,str.lastIndexOf("/")); ///storage/emulated/0/DCIM/Camera
            locationWithoutVideoPart.add(tempStr);
            String tempFolder=tempStr.substring(tempStr.lastIndexOf("/")+1); //Camera

            int flag=0;
            int foldersWithImagesSize=foldesWithVideos.size();
            for(int i=0;i<foldersWithImagesSize;i++){
                if (tempFolder.equals(foldesWithVideos.get(i))){
                    flag++;
                }
            }
            if(flag==0){
                foldesWithVideos.add(tempFolder);
                videoThumbnails.add(str);

                videoCountInFolders.add(0);
            }
        }
        countVideoCount();
    }

    private void countVideoCount(){
        for(String str:locationWithoutVideoPart){
            String testStr=str.substring(str.lastIndexOf("/")+1);

            for (int i=0;i<foldesWithVideos.size();i++){
                if (testStr.equals(foldesWithVideos.get(i)))
                    videoCountInFolders.set(i,videoCountInFolders.get(i)+1);
            }
        }
    }

    private ArrayList<String> getAllMedia() {
        HashSet<String> videoItemHashSet = new HashSet<>();
        String[] projection = {MediaStore.Video.VideoColumns.DATA, MediaStore.Video.Media.DISPLAY_NAME};
        Cursor cursor = getContext().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
        try {
            cursor.moveToFirst();
            do {
                videoItemHashSet.add((cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))));
            } while (cursor.moveToNext());

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<String> downloadedList = new ArrayList<>(videoItemHashSet);
        return downloadedList;
    }

    public static Tab4 getInstance () {
        return new Tab4();
    }
}

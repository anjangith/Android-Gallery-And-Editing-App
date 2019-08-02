package digital.starein.com.galleryapp;



import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collections;


/**
 * A simple {@link Fragment} subclass.
 */
public class Tab2 extends Fragment {

    public RecyclerView recyclerView;
    public  static PhotoAdapter adapter;
    public static boolean alter=false;

    public Tab2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_tab2, container, false);
        recyclerView=(RecyclerView)view.findViewById(R.id.photoRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        if(alter){
            adapter=new PhotoAdapter(getContext(),getActivity(),MainActivity.imageLocations,R.layout.photo_grid,R.id.imageView_in_item);
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
        }

        return view;
    }



    public static Tab2 getInstance(){
        return new Tab2();
    }





}

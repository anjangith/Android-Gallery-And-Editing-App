package digital.starein.com.galleryapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class Tab1 extends Fragment {

    private RecyclerView recyclerView;
    private FavAdapter adapter;
    private TextView emptyView;

    public static boolean alter=false;
    public Tab1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_tab1, container, false);
        recyclerView=(RecyclerView)view.findViewById(R.id.recycler_view);
        emptyView=(TextView)view.findViewById(R.id.empty_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        if(alter){
            if (MainActivity.savedImage.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }
            else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                adapter=new FavAdapter(getContext(),getActivity(),MainActivity.savedImage,R.layout.photo_grid,R.id.imageView_in_item);
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
            }

        }

        return view;
    }

    public static Tab1 getInstance(){
        return new Tab1();
    }

}

package digital.starein.com.galleryapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class Tab3 extends Fragment {

    private RecyclerView recyclerView;
    public static boolean alter=false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab3, container, false);
        recyclerView = view.findViewById(R.id.recyclerView_in_album_fragment);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        if(alter) {
            recyclerView.setAdapter(new AdapterAlbumGrid(getActivity(), MainActivity.thumbnailForFolders,
                    MainActivity.foldersWithImages, MainActivity.imagesCountInFolder, R.layout.album_grid,
                    R.id.imageView_in_item_album, R.id.folder_name_item, R.id.images_count_item, 'p'));

        }
        return view;
    }

    public static Tab3 getInstance () {
        return new Tab3();
    }
}

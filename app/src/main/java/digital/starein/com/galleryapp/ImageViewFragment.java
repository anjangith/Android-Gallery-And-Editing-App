package digital.starein.com.galleryapp;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageViewFragment extends Fragment implements View.OnClickListener,PhotoViewAttacher.OnViewTapListener {


    Context context;
    String str;
    Boolean isHidden;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = getContext();
        isHidden=false;
        View view = inflater.inflate(R.layout.fragment_image_view, container, false);
        str = getArguments() != null ? getArguments().getString("LOC") : null;
       // PhotoView.currentImageUri=Uri.fromFile(new File(str));
        ImageView imageView = view.findViewById(R.id.imageView_fragment);

        Glide.with(context)
                .load(Uri.fromFile(new File(str))).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)
                //.centerCrop()
                .into(imageView);
        PhotoViewAttacher pAttacher;
        pAttacher = new PhotoViewAttacher(imageView);
        pAttacher.setOnViewTapListener(this);
        pAttacher.update();
        return view;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onViewTap(View view, float x, float y) {
        if (!isHidden) {
         //   getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
            isHidden = true;
        }
        else {
          //  getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            isHidden=false;
        }
    }
}

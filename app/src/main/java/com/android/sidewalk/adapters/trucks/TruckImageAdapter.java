package com.android.sidewalk.adapters.trucks;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.android.sidewalk.R;
import com.android.sidewalk.views.trucks.TruckDetailActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;


public class TruckImageAdapter extends PagerAdapter {

    private ArrayList<String> bannerList;
    private LayoutInflater inflater;
    private TruckDetailActivity context;

    public TruckImageAdapter(TruckDetailActivity context, ArrayList<String> bannerList) {
        this.context = context;
        this.bannerList = bannerList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return bannerList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.truck_image_item, view, false);
        assert imageLayout != null;
        final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.imgTruck);


        Glide.with(context)
                .load(bannerList.get(position))
                // .transform(RoundedCorners(10))
                .transition(DrawableTransitionOptions.withCrossFade())
//            .apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
                .into(imageView);


        view.addView(imageLayout, 0);
//        }


        /*parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!bannerList.get(position).getLinked_category().isEmpty()) {
                    Intent intent = new Intent(context, ProductListingFilterActivity.class);
                    intent.putExtra("category_id", bannerList.get(position).getLinked_category());
                    context.startActivity(intent);
                }

            }
        });*/

        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

}
package ico.ico.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import ico.ico.constant.ImageLoaderPrefixConstant;

/**
 * ViewPager的适配器,用于显示图片,一般用于广告横幅
 */
public class ImagePagerAdapter extends PagerAdapter {
    String[] photo;
    ImageView[] imageViews;

    Context mContext;
    ImageLoader imageLoader;
    int loadImage;
    int failImage;
    DisplayImageOptions displayImage;
    OnItemClickListener onItemClickListener;

    public ImagePagerAdapter(Context context, int loadImage, int failImage) {
        this.mContext = context;
        this.loadImage = loadImage;
        this.failImage = failImage;
        imageLoader = ImageLoader.getInstance();
    }

    public ImagePagerAdapter(Context context, int loadImage, int failImage, String... photo) {
        this(context, loadImage, failImage);
        displayImage = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageOnLoading(loadImage)
                .showImageOnFail(failImage)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        setPhoto(photo);
    }

    @Override
    public int getCount() {
        if (photo == null) return 0;
        return photo.length;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (imageViews[position] == null) {
            imageViews[position] = new ImageView(mContext);
            imageViews[position].setScaleType(ImageView.ScaleType.FIT_XY);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT);
            imageViews[position].setLayoutParams(lp);
        }
        if (TextUtils.isEmpty(photo[position])) {
            imageLoader.displayImage(ImageLoaderPrefixConstant.DRAWABLE + loadImage, imageViews[position], displayImage);
        } else {
            imageLoader.displayImage(photo[position], imageViews[position]);
        }
        imageViews[position].setTag(position + "");
        container.addView(imageViews[position]);
        if (onItemClickListener != null) {
            imageViews[position].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = Integer.valueOf(v.getTag().toString());
                    onItemClickListener.onItemClick(v, position);
                }
            });
        }
        return imageViews[position];
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(imageViews[position]);

    }

    public String[] getPhoto() {
        return photo;
    }

    public ImagePagerAdapter setPhoto(String... photo) {
        this.photo = photo;
        this.imageViews = null;
        if (photo != null) {
            this.imageViews = new ImageView[photo.length];
        }
        return this;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public ImagePagerAdapter setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }

    public interface OnItemClickListener {
        public void onItemClick(View v, int position);
    }
}

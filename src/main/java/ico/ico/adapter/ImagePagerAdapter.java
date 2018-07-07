package ico.ico.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import ico.ico.constant.ImageLoaderPrefixConstant;

/**
 * ViewPager的适配器,用于显示图片,一般用于广告横幅
 */
public class ImagePagerAdapter extends PagerAdapter {
    String[] mImages;
    Bitmap[] mBitmaps;
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
        setImages(photo);
    }

    public ImagePagerAdapter(Context mContext) {
        this.mContext = mContext;
        imageLoader = ImageLoader.getInstance();
    }

    public ImagePagerAdapter(Context context, Bitmap... bitmaps) {
        this(context);
        setBitmaps(bitmaps);
    }

    @Override
    public int getCount() {
        if (mBitmaps != null) return mBitmaps.length;
        if (mImages != null) return mImages.length;
        return 0;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (imageViews[position] == null) {
            imageViews[position] = new ImageView(mContext);
            imageViews[position].setScaleType(ImageView.ScaleType.FIT_XY);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT);
            imageViews[position].setLayoutParams(lp);
        }
        if (mBitmaps != null && mBitmaps[position] != null) {//如果有bitmap
            imageViews[position].setImageBitmap(mBitmaps[position]);
        } else if (mImages != null && mImages[position] != null) {//如果有图片资源，仅限url的图片资源
            imageLoader.displayImage(mImages[position], imageViews[position], displayImage);
        } else if (failImage > 0) {
            imageLoader.displayImage(ImageLoaderPrefixConstant.DRAWABLE + failImage, imageViews[position], displayImage);
        }
        //设置tag，设置单击事件
        imageViews[position].setTag(position + "");
        if (onItemClickListener != null) {
            imageViews[position].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = Integer.valueOf(v.getTag().toString());
                    onItemClickListener.onItemClick(v, position);
                }
            });
        }
        //将图片控件添加到父容器中
        container.addView(imageViews[position]);
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

    public String[] getImages() {
        return mImages;
    }

    public ImagePagerAdapter setImages(List<String> images) {
        this.mImages = new String[images.size()];
        images.toArray(this.mImages);
        this.imageViews = null;
        if (images != null) {
            this.imageViews = new ImageView[images.size()];
        }
        return this;
    }

    public ImagePagerAdapter setImages(String... images) {
        this.mImages = images;
        this.imageViews = null;
        if (mImages != null) {
            this.imageViews = new ImageView[mImages.length];
        }
        return this;
    }

    public Bitmap[] getBitmaps() {
        return mBitmaps;
    }

    public void setBitmaps(Bitmap... bitmaps) {
        this.mBitmaps = bitmaps;
        this.imageViews = null;
        if (bitmaps != null) {
            this.imageViews = new ImageView[bitmaps.length];
        }
    }

    public void setBitmaps(List<Bitmap> bitmaps) {
        this.mBitmaps = new Bitmap[bitmaps.size()];
        bitmaps.toArray(this.mBitmaps);
        this.imageViews = null;
        if (bitmaps != null) {
            this.imageViews = new ImageView[bitmaps.size()];
        }
    }

    public void notifyImageRefresh(int position) {
        if (imageViews[position] != null) imageViews[position].postInvalidate();
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

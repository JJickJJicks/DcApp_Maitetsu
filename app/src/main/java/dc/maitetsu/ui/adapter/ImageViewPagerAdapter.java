package dc.maitetsu.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.github.chrisbanes.photoview.OnViewTapListener;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import dc.maitetsu.R;
import dc.maitetsu.utils.ContentUtils;

/**
 * @since 2017-05-12
 */
public class ImageViewPagerAdapter extends PagerAdapter {
  private SparseArray<byte[]> imageBytes;
  private Activity activity;
  private LayoutInflater layoutInflater;
  private LinearLayout toolLayout;
  private ImageView.ScaleType scaleType;

  public ImageViewPagerAdapter(Activity activity, LinearLayout toolLayout, SparseArray<byte[]>  imageBytes, ImageView.ScaleType scaleType) {
    this.imageBytes = imageBytes;
    this.activity = activity;
    this.toolLayout = toolLayout;
    this.scaleType = scaleType;
    this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  }

  @Override
  public int getCount() {
    return imageBytes.size();
  }

  @Override
  public boolean isViewFromObject(View view, Object object) {
    return view == object;
  }

  @Override
  public Object instantiateItem(ViewGroup container, int position) {
    View itemView = layoutInflater.inflate(R.layout.image_view_item, container, false);
    final ImageView imageView = (ImageView) itemView.findViewById(R.id.image_view_item_image);
    imageView.setScaleType(ImageView.ScaleType.MATRIX);

    PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(imageView);
    photoViewAttacher.setMaximumScale(9);
    photoViewAttacher.setMinimumScale(1f);

    photoViewAttacher.setOnViewTapListener(new OnViewTapListener() {
      @Override
      public void onViewTap(View view, float x, float y) {
        if (toolLayout.getVisibility() == View.INVISIBLE) {
          toolLayout.setVisibility(View.VISIBLE);
        } else {
          toolLayout.setVisibility(View.INVISIBLE);
        }
      }
    });

    ContentUtils.loadBitMapFromBytes(activity, imageBytes.get(position), imageView, photoViewAttacher, scaleType);
    container.addView(itemView);
    return itemView;
  }



  @Override
  public void destroyItem(ViewGroup container, int position, Object object) {
    container.removeView((View) object);
  }

}

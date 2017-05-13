package dc.maitetsu.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import dc.maitetsu.R;
import dc.maitetsu.utils.ContentUtils;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

import java.util.Map;

/**
 * @since 2017-05-12
 */
public class ImageViewPagerAdapter extends PagerAdapter {
  private Map<Integer, byte[]> imageBytes;
  private Activity activity;
  private LayoutInflater layoutInflater;
  private LinearLayout toolLayout;
  private ImageView.ScaleType scaleType;

  public ImageViewPagerAdapter(Activity activity, LinearLayout toolLayout, Map<Integer, byte[]> imageBytes, ImageView.ScaleType scaleType) {
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
    final PhotoView imageView = (PhotoView) itemView.findViewById(R.id.image_view_item_image);
    imageView.setMaximumScale(9f);
    imageView.setMinimumScale(1f);
    imageView.setScaleType(scaleType);
    ContentUtils.loadBitMap(activity, imageBytes.get(position), imageView);

    imageView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
      @Override
      public void onViewTap(View view, float x, float y) {
          if (toolLayout.getVisibility() == View.INVISIBLE) {
            toolLayout.setVisibility(View.VISIBLE);
          } else {
            toolLayout.setVisibility(View.INVISIBLE);
          }
      }
    });

    container.addView(itemView);
    return itemView;
  }


  @Override
  public void destroyItem(ViewGroup container, int position, Object object) {
    container.removeView((View) object);
  }

}

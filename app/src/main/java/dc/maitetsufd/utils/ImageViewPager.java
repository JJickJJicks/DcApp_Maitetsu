package dc.maitetsufd.utils;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * PhotoView 버그성 예외 핸들링을 위한 뷰페이저
 *
 * @since 2017-05-12
 */
public class ImageViewPager extends ViewPager{
  public ImageViewPager(Context context) {
    super(context);
  }

  public ImageViewPager(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    try {
      return super.onTouchEvent(ev);
    } catch (Exception e) {
    }
    return false;
  }


  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    try {
      return super.onInterceptTouchEvent(ev);
    } catch (Exception e) {
    }
    return false;
  }
}

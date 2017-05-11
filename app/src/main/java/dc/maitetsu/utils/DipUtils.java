package dc.maitetsu.utils;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * @author Park Hyo Jun
 * @since 2017-04-27
 *
 * Pixel <ㅡ> Dip를 변환하는 메소드
 *
 */
public class DipUtils {
  /**
   * pixel을 dip로 바꿔주는 메소드.
   *
   * @param res the res
   * @param px  the px
   * @return the dp
   */
// pixel to dp
  public static int getDp(Resources res, int px) {
    DisplayMetrics displayMetrics = res.getDisplayMetrics();
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, displayMetrics);
  }

  public static int getPixel(Resources res, int dip) {
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, res.getDisplayMetrics());
  }
}

package dc.maitetsufd.utils;

import android.content.Context;
import android.os.Vibrator;

/**
 * @since 2017-04-26
 *
 * 부르르 진동
 */
public class VibrateUtils {
  public static long VIBRATE_DURATION = 80;
  public static long VIBRATE_DURATION_SHORT = 60;

    public static void call(Context ctx, long duration) {
      Vibrator vibrator = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
      long[] pattern = {0, duration};
      vibrator.vibrate(pattern, -1);
    }
}

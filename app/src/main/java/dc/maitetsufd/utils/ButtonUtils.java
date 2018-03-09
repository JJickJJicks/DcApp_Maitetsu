package dc.maitetsufd.utils;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.widget.Button;
import dc.maitetsufd.R;
import dc.maitetsufd.data.CurrentData;
import dc.maitetsufd.data.CurrentDataManager;

/**
 * @since 2017-04-27
 *
 * 백버튼 두번눌러야 꺼지게하는 메소드
 */
public class ButtonUtils {

  public static long backButtnFinish(Activity activity, long backKeyPressed) {
    long systemCurrentTime = System.currentTimeMillis();
    long time = 2000;
    if (systemCurrentTime > backKeyPressed + time) {
      MainUIThread.showToast(activity, activity.getString(R.string.exit_app));
    } else {
      CurrentDataManager.save(activity);
      MainUIThread.clearToast(activity);
      activity.finishAffinity();
      System.exit(0);
    }
    return systemCurrentTime;
  }


  public static void setBtnTheme(Activity activity, CurrentData currentData, Button btn){
    if (currentData.isDarkTheme()) {
      btn.setBackgroundColor(ContextCompat.getColor(activity, R.color.darkThemeLightBackground));
      btn.setTextColor(ContextCompat.getColor(activity, R.color.colorWhite));
    } else {
      btn.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorPrimary));
      btn.setTextColor(ContextCompat.getColor(activity, R.color.colorWhite));
    }
  }

}

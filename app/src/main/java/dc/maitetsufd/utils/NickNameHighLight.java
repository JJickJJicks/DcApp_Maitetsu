package dc.maitetsufd.utils;

import android.graphics.Color;
import android.widget.TextView;
import dc.maitetsufd.models.UserInfo;

import java.util.Arrays;
import java.util.List;

/**
 * @since 2018-10-06
 */
public class NickNameHighLight {
  private static List<String> names = Arrays.asList();
  private static int RADIUS = 9;

  public static void set(UserInfo userInfo, TextView nickname, int alpha) {
    if (names.contains(userInfo.getGallogId())) {
      // 하이라이팅 닉네임
      nickname.setShadowLayer(RADIUS, 0, 0, Color.argb(alpha, 255, 255, 30));

    } else if (userInfo.getUserType() == UserInfo.UserType.MANAGER_GALLOG) { // 매니저
      nickname.setShadowLayer(RADIUS, 0, 0, Color.argb(alpha, 255, 255, 30));

    } else if (userInfo.getUserType() == UserInfo.UserType.SUBMANAGER_GALLOG) { // 부매니저
      nickname.setShadowLayer(RADIUS, 0, 0, Color.argb(alpha, 30, 30, 255));

    } else { // 제거
      nickname.setShadowLayer(0, 0, 0, Color.rgb(0, 0, 0));

    }


  }

}

package dc.maitetsu.utils;

import android.content.res.Resources;
import android.widget.ImageView;
import dc.maitetsu.R;
import dc.maitetsu.models.UserInfo;

/**
 * @author Park Hyo Jun
 * @since 2017-04-24
 *
 * 유저 타입을 설정해줌
 */
public class UserTypeManager {

  public static void set(Resources res, UserInfo userInfo, ImageView imageView){
    if(userInfo.getUserType() == UserInfo.UserType.FIX_GALLOG) {
      imageView.setImageDrawable(res.getDrawable(R.drawable.fix_gallog));
    }else if(userInfo.getUserType() == UserInfo.UserType.FLOW_GALLOG) {
      imageView.setImageDrawable(res.getDrawable(R.drawable.flow_gallog));
    }else
      imageView.setImageDrawable(res.getDrawable(R.drawable.flow));
  }
}

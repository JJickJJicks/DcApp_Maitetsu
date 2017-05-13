package dc.maitetsu;

import android.app.Application;
import com.bumptech.glide.Glide;

/**
 * @since 2017-04-29
 *
 * 어플리케이션 설정 클래스.
 * Glide 메모리 정리를 다시 유도함.
 */
public class BaseApplication extends Application {

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    Glide.get(this).clearMemory();
  }

  @Override
  public void onTrimMemory(int level) {
    super.onTrimMemory(level);
    Glide.get(this).trimMemory(level);
  }
}

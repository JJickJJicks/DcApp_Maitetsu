package dc.maitetsu.ui.listener;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

/**
 * @since 2017-04-28
 * 이미지를 클릭하면 기본 갤러리 앱으로 여는 리스너
 */
public class ImageClickListener {
  public static View.OnClickListener get(final Activity activity, final String url) {
    return new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(url), "image/*");
        activity.startActivity(intent);
      }
    };
  }
}

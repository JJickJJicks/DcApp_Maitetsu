package dc.maitetsu.ui.listener;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import dc.maitetsu.data.ImageData;
import dc.maitetsu.ui.ImageViewActivity;

import java.util.Map;

/**
 * @since 2017-05-12
 */
public class ImageViewerListener {

  public static View.OnClickListener get(final Activity activity,
                                         final String name,
                                         final int imagePostion,
                                         final Map<Integer, byte[]> imageBytes,
                                         final boolean hideStatusBar) {
    return new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(activity, ImageViewActivity.class);
        intent.putExtra("position", imagePostion);
        intent.putExtra("name", name);
        intent.putExtra("hideStatusBar", hideStatusBar);
        ImageData.setImageBytes(imageBytes);
        activity.startActivity(intent);
      }
    };
  }


}

package dc.maitetsufd.ui.listener;

import android.view.KeyEvent;
import android.view.View;

/**
 * @since 2017-04-30
 *
 * 엔터키 눌렀을 때 특정 뷰 클릭되게 하는 메소드
 *
 */
public class EnterKeyListener {

  public static View.OnKeyListener get(final View clickBtn) {
    return new View.OnKeyListener() {
      @Override
      public boolean onKey(View view, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
          clickBtn.performClick();
          return true;
        }
        return false;
      }
    };

  }

}

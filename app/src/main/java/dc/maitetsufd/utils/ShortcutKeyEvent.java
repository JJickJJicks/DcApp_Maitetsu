package dc.maitetsufd.utils;

import android.view.KeyEvent;
import android.widget.ListView;
import android.widget.ScrollView;
import dc.maitetsufd.ui.ArticleDetailActivity;
import dc.maitetsufd.ui.listener.ArticleListBottomListener;
import dc.maitetsufd.ui.listener.ArticleListSwipeRefreshListener;
import dc.maitetsufd.ui.viewmodel.HasViewModelFragment;

/**
 * @author Park Hyo Jun
 * @since 2017-08-20
 */
public class ShortcutKeyEvent {

  private static long lastDownTime = 0;

  public static void computeSimpleArticleKeyEvent(final HasViewModelFragment fragment,
                                                  final ListView listView,
                                                  final KeyEvent keyEvent) {

    if(keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
      lastDownTime = keyEvent.getDownTime();

    } else if (keyEvent.getAction() == KeyEvent.ACTION_UP && lastDownTime <= keyEvent.getDownTime()) {

      switch (keyEvent.getKeyCode()) {
        case KeyEvent.KEYCODE_F: // F
          ArticleListSwipeRefreshListener.refreshing(fragment);
          break;

        case KeyEvent.KEYCODE_N: // N
          ArticleListBottomListener.action(fragment.getHasAdapterViewModel().getSwipeRefreshLayout(),
                  fragment, false);
          break;

        case KeyEvent.KEYCODE_SPACE: // Space
          listView.post(new Runnable() {
            @Override
            public void run() {
              int p = listView.getFirstVisiblePosition();
              listView.setSelection(p + 3);
            }
          });
          break;

        case KeyEvent.KEYCODE_I: // I
          listView.post(new Runnable() {
            @Override
            public void run() {
              int p = listView.getFirstVisiblePosition() - 2;
              if(p < 0) p = 0;
              listView.setSelection(p);
            }
          });
          break;

        case KeyEvent.KEYCODE_Q: // Q
          clickItemPosition(listView, 0);
          break;

        case KeyEvent.KEYCODE_W: // W
          clickItemPosition(listView, 1);
          break;

        case KeyEvent.KEYCODE_E: // E
          clickItemPosition(listView, 2);
          break;

        case KeyEvent.KEYCODE_R: // R
          clickItemPosition(listView, 3);
          break;

        case KeyEvent.KEYCODE_T: // T
          listView.post(new Runnable() {
            @Override
            public void run() {
              listView.setSelectionAfterHeaderView();
            }
          });
          break;

        case KeyEvent.KEYCODE_B: // B
          listView.post(new Runnable() {
            @Override
            public void run() {
              listView.setSelection(listView.getChildCount()-1);
            }
          });
          break;

        default:
          break;
      }
    }
  }


  private static void clickItemPosition(final ListView listView, final int i) {
    listView.post(new Runnable() {
      @Override
      public void run() {
        listView.getChildAt(i).performClick();
      }
    });
  }


  public static boolean computeArticleDetailKeyEvent(final ArticleDetailActivity activity,
                                                     final ScrollView scrollView,
                                                     final KeyEvent keyEvent) {
    boolean isDoneEvent = false;
//    if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_SPACE)

    if(keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
      lastDownTime = keyEvent.getDownTime();

    }else if (!activity.isFocusedCommentText()
                && keyEvent.getAction() == KeyEvent.ACTION_UP
                && lastDownTime <= keyEvent.getDownTime()) {
      switch (keyEvent.getKeyCode()) {
        case KeyEvent.KEYCODE_Z : // Z
        case KeyEvent.KEYCODE_P : // P
            activity.scrollToFinishActivity();
          isDoneEvent = true;
          break;

        case KeyEvent.KEYCODE_DEL: // Backspace
          activity.focusCommentText();
          break;

        case KeyEvent.KEYCODE_SPACE: // Space
          scrollView.computeScroll();
          scrollView.smoothScrollTo(0, scrollView.getScrollY()
                  + DipUtils.getDp(activity.getResources(), 400));
          isDoneEvent = true; // 스페이스 입력 시 무조건 무시
          break;

        case KeyEvent.KEYCODE_I: // I
          scrollView.computeScroll();
          scrollView.smoothScrollTo(0, scrollView.getScrollY()
                  - DipUtils.getDp(activity.getResources(), 400));
          isDoneEvent = true;
          break;

        case KeyEvent.KEYCODE_T: // T
          scrollView.computeScroll();
          scrollView.setScrollY(0);
          break;

        case KeyEvent.KEYCODE_B: // B
          scrollView.computeScroll();
          scrollView.setScrollY(scrollView.getHeight());
          break;

      }
    }
    return isDoneEvent;
  }


}

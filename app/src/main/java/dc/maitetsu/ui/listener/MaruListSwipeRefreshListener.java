package dc.maitetsu.ui.listener;

import android.support.v4.widget.SwipeRefreshLayout;
import dc.maitetsu.utils.MainUIThread;
import dc.maitetsu.ui.fragment.MaruViewerFragment;

/**
 * @author Park Hyo Jun
 * @since 2017-04-22
 *
 * 마루 리스트 상단 스와이프 갱신
 *
 */
public class MaruListSwipeRefreshListener {

  public static SwipeRefreshLayout.OnRefreshListener newInstance(final MaruViewerFragment fragment) {
    return new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        MainUIThread.refreshMaruListView(fragment, true);
      }
    };
  }

}

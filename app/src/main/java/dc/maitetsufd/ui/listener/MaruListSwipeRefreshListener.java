package dc.maitetsufd.ui.listener;

import android.support.v4.widget.SwipeRefreshLayout;
import dc.maitetsufd.utils.MainUIThread;
import dc.maitetsufd.ui.fragment.MangaViewerFragment;

/**
 * @since 2017-04-22
 *
 * 마루 리스트 상단 스와이프 갱신
 *
 */
public class MaruListSwipeRefreshListener {

  public static SwipeRefreshLayout.OnRefreshListener newInstance(final MangaViewerFragment fragment) {
    return new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        MainUIThread.refreshMaruListView(fragment, true);
      }
    };
  }

}

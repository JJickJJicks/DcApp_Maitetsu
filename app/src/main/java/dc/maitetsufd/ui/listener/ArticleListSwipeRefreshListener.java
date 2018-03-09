package dc.maitetsufd.ui.listener;

import android.support.v4.widget.SwipeRefreshLayout;
import dc.maitetsufd.utils.MainUIThread;
import dc.maitetsufd.ui.viewmodel.HasViewModelFragment;

/**
 * @since 2017-04-22
 *
 * 게시물 목록 상단을 끌었을 때 갱신되는 리스너
 *
 */
public class ArticleListSwipeRefreshListener {

  public static SwipeRefreshLayout.OnRefreshListener newInstance(final HasViewModelFragment fragment) {
    return new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        refreshing(fragment);
      }
    };
  }

  public static void refreshing(final HasViewModelFragment fragment) {
        fragment.getHasAdapterViewModel().hideSearchContinueBtn();
        MainUIThread.refreshArticleListView(fragment, true);
  }
}

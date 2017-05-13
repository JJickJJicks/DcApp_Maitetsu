package dc.maitetsu.ui.listener;

import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.AbsListView;
import dc.maitetsu.data.CurrentData;
import dc.maitetsu.data.CurrentDataManager;
import dc.maitetsu.service.ServiceProvider;
import dc.maitetsu.ui.viewmodel.HasViewModelFragment;

/**
 * @since 2017-04-22
 *
 * 게시물 하단 다음 페이지 리스너
 *
 */
public class ArticleListBottomListener {

  public static AbsListView.OnScrollListener newInstance(final HasViewModelFragment fragment,
                                                         final SwipeRefreshLayout swipeRefreshLayout) {

    return new AbsListView.OnScrollListener() {
      boolean lastItemVisibleFlag = false;

      @Override
      public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag) {
          action(swipeRefreshLayout, fragment, false);
        }
      }

      @Override
      public void onScroll(AbsListView lw, final int firstVisibleItem,
                           final int visibleItemCount, final int totalItemCount) {
        lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
      }
    };
  }

  public static void action(SwipeRefreshLayout swipeRefreshLayout, HasViewModelFragment fragment, boolean refreshSerPos) {
    swipeRefreshLayout.setRefreshing(true);
    final CurrentData currentData = CurrentDataManager.getInstance(fragment.getActivity().getApplicationContext());
    if(!refreshSerPos) {
      currentData.setPage(currentData.getPage() + 1);
    } else
      currentData.setPage(1);
    ServiceProvider.getInstance().getSimpleArticles(fragment, refreshSerPos);
  }
}

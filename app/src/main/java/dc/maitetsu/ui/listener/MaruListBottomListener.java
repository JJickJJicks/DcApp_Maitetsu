package dc.maitetsu.ui.listener;

import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.AbsListView;
import dc.maitetsu.data.CurrentData;
import dc.maitetsu.data.CurrentDataManager;
import dc.maitetsu.service.MaruServiceProvider;
import dc.maitetsu.ui.fragment.MangaViewerFragment;

/**
 * @since 2017-04-22
 *
 * 마루뷰어 하단, 다음페이지
 *
 */
public class MaruListBottomListener {

  public static AbsListView.OnScrollListener newInstance(final MangaViewerFragment fragment,
                                                         final SwipeRefreshLayout swipeRefreshLayout) {

    return new AbsListView.OnScrollListener() {
      boolean lastItemVisibleFlag = false;

      @Override
      public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag) {
          action(swipeRefreshLayout, fragment);
        }
      }

      @Override
      public void onScroll(AbsListView lw, final int firstVisibleItem,
                           final int visibleItemCount, final int totalItemCount) {
        lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
      }
    };
  }

  public static void action(SwipeRefreshLayout swipeRefreshLayout, MangaViewerFragment fragment) {
    swipeRefreshLayout.setRefreshing(true);
    final CurrentData currentData = CurrentDataManager.getInstance(fragment.getActivity().getApplicationContext());
      currentData.setPage(currentData.getPage() + 1);
//      CurrentDataManager.save(currentData, fragment.getActivity().getApplicationContext());
    MaruServiceProvider.getInstance().getMaruSimpleModels(fragment, currentData.getPage(),
            currentData.getSearchWord(), false);
  }
}

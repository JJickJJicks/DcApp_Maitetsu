package dc.maitetsufd.ui.viewmodel;

import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ListView;

import java.util.List;

/**
 * @since 2017-04-27
 */
public interface HasAdapterViewModel<SimpleArticle> {
  void clearItem();

  void addItems(List<SimpleArticle> simpleArticles);

  void notifyDataChanged();

  void showSearchContinueBtn();

  void hideSearchContinueBtn();

  void startRefreshing();

  void stopRefreshing();

  SwipeRefreshLayout getSwipeRefreshLayout();

  ListView getListView();
}

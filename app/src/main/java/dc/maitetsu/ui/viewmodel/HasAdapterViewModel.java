package dc.maitetsu.ui.viewmodel;

import java.util.List;

/**
 * @since 2017-04-27
 */
public interface HasAdapterViewModel<T> {
  void clearItem();

  void addItems(List<T> simpleArticles);

  void notifyDataChanged();

  void showSearchContinueBtn();

  void hideSearchContinueBtn();

  void startRefreshing();

  void stopRefreshing();

}

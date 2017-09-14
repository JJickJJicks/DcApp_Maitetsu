package dc.maitetsu.ui.viewmodel;

import android.app.Activity;
import android.view.View;
import dc.maitetsu.ui.adapter.SimpleArticleListAdapter;

/**
 * @since 2017-04-27
 */
public interface HasViewModelFragment {
  Activity getActivity();
  HasAdapterViewModel getHasAdapterViewModel();
  View getView();
}

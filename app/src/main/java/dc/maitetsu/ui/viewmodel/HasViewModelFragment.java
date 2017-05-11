package dc.maitetsu.ui.viewmodel;

import android.app.Activity;
import android.view.View;

/**
 * @author Park Hyo Jun
 * @since 2017-04-27
 */
public interface HasViewModelFragment {
  Activity getActivity();
  HasAdapterViewModel getHasAdapterViewModel();
  View getView();
}

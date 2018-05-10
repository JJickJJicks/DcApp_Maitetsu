package dc.maitetsufd.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import dc.maitetsufd.R;
import dc.maitetsufd.data.CurrentData;
import dc.maitetsufd.data.CurrentDataManager;
import dc.maitetsufd.service.MaruServiceProvider;
import dc.maitetsufd.ui.viewmodel.MaruViewerViewModel;

/**
 * 에러 메시지 출력용 프래그먼트.
 */
public class MangaViewerFragment extends Fragment{
  private static MangaViewerFragment fragment;
  private MaruViewerViewModel maruViewerViewModel;

  public MangaViewerFragment() {
    fragment = this;
  }

  public static MangaViewerFragment instance() {
    if (fragment == null) {
      fragment = new MangaViewerFragment();
    }
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_maru, container, false);
    CurrentData currentData = CurrentDataManager.getInstance(this.getContext());
    maruViewerViewModel = new MaruViewerViewModel(this, view, currentData);
    MaruServiceProvider.getInstance().setContentCookies(currentData.getMaruCookies());
    return view;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
  }

  public void clearSearchKeyword() {
    final EditText keyword = (EditText) this.getActivity().
            findViewById(R.id.maru_keyword);
//    keyword.setText("");
  }

  public MaruViewerViewModel getPresenter() {
    return maruViewerViewModel;
  }

}

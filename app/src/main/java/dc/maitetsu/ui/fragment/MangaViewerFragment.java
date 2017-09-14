package dc.maitetsu.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import dc.maitetsu.R;
import dc.maitetsu.data.CurrentData;
import dc.maitetsu.data.CurrentDataManager;
import dc.maitetsu.ui.viewmodel.MaruViewerViewModel;

/**
 * 에러 메시지 출력용 프래그먼트.
 */
public class MangaViewerFragment extends Fragment{

  private MaruViewerViewModel maruViewerViewModel;

  public MangaViewerFragment() {
  }

  public static MangaViewerFragment newInstance() {
    return new MangaViewerFragment();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_maru, container, false);
    CurrentData currentData = CurrentDataManager.getInstance(this.getContext());
    maruViewerViewModel = new MaruViewerViewModel(this, view, currentData);
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
    keyword.setText("");
  }

  public MaruViewerViewModel getPresenter() {
    return maruViewerViewModel;
  }

}

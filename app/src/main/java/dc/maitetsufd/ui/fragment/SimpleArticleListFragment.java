package dc.maitetsufd.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import dc.maitetsufd.R;
import dc.maitetsufd.ui.viewmodel.HasAdapterViewModel;
import dc.maitetsufd.ui.viewmodel.HasViewModelFragment;
import dc.maitetsufd.ui.viewmodel.SimpleArticleListViewModel;

/**
 * 게시글 목록 프래그먼트.
 */
public class SimpleArticleListFragment extends Fragment implements HasViewModelFragment {
  private SimpleArticleListViewModel viewModel;
  private static SimpleArticleListFragment fragment;

  public SimpleArticleListFragment() {
    fragment = this;
  }

  public static SimpleArticleListFragment instance() {
    if (fragment == null) {
      fragment = new SimpleArticleListFragment();
    }
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_article_list, container, false);
    final SimpleArticleListFragment thisFragment = this;
    viewModel = new SimpleArticleListViewModel(thisFragment, view);
    return view;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
  }

  @Override
  public HasAdapterViewModel getHasAdapterViewModel() {
    return viewModel;
  }


}

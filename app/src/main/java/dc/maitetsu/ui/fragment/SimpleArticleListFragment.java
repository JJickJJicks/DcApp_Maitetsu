package dc.maitetsu.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import dc.maitetsu.R;
import dc.maitetsu.ui.viewmodel.HasAdapterViewModel;
import dc.maitetsu.ui.viewmodel.HasViewModelFragment;
import dc.maitetsu.ui.viewmodel.SimpleArticleListViewModel;

/**
 * 게시글 목록 프래그먼트.
 */
public class SimpleArticleListFragment extends Fragment implements HasViewModelFragment {
  private SimpleArticleListViewModel viewModel;

  public SimpleArticleListFragment() {
  }

  public static SimpleArticleListFragment newInstance() {
    return new SimpleArticleListFragment();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_article_list, container, false);
    viewModel = new SimpleArticleListViewModel(this, view);
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

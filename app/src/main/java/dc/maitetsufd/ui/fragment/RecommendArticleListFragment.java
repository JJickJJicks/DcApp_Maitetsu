package dc.maitetsufd.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import dc.maitetsufd.R;
import dc.maitetsufd.data.CurrentData;
import dc.maitetsufd.data.CurrentDataManager;
import dc.maitetsufd.ui.viewmodel.HasAdapterViewModel;
import dc.maitetsufd.ui.viewmodel.HasViewModelFragment;
import dc.maitetsufd.ui.viewmodel.RecommendArticleListViewModel;

/**
 * 개념글 프래그먼트.
 */
public class RecommendArticleListFragment extends Fragment implements HasViewModelFragment {
  private RecommendArticleListViewModel presenter;

  public RecommendArticleListFragment() {
  }

  public static RecommendArticleListFragment newInstance() {
    return new RecommendArticleListFragment();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_recommend_article_list, container, false);
    CurrentData currentData = CurrentDataManager.getInstance(this.getContext());
    presenter = new RecommendArticleListViewModel(this, view, currentData);
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
    return presenter;
  }
}

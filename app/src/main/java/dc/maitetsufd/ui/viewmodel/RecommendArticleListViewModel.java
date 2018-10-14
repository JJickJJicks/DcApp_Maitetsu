package dc.maitetsufd.ui.viewmodel;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import dc.maitetsufd.R;
import dc.maitetsufd.data.CurrentData;
import dc.maitetsufd.models.SimpleArticle;
import dc.maitetsufd.ui.adapter.SimpleArticleListAdapter;
import dc.maitetsufd.ui.fragment.RecommendArticleListFragment;
import dc.maitetsufd.ui.fragment.SimpleArticleListFragment;
import dc.maitetsufd.ui.listener.ArticleListBottomListener;
import dc.maitetsufd.ui.listener.ArticleListSwipeRefreshListener;
import dc.maitetsufd.utils.ShortcutKeyEvent;

import java.util.List;

/**
 * @since 2017-04-23
 *
 * 개념글 목록 프레젠터
 */
public class RecommendArticleListViewModel implements HasAdapterViewModel<SimpleArticle> {
  private SimpleArticleListAdapter simpleArticleListAdapter;
  private SwipeRefreshLayout swipeRefreshLayout;
  private FloatingActionButton searchContinueBtn;
  private RecommendArticleListFragment fragment;
  private ListView listView;

  public RecommendArticleListViewModel(final RecommendArticleListFragment fragment, View view, CurrentData currentData) {
    this.fragment = fragment;
    this.simpleArticleListAdapter = new SimpleArticleListAdapter(fragment);

    // 게시물 리스트뷰 처리
    listView = (ListView) view.findViewById(R.id.recommend_article_list);
    listView.setAdapter(simpleArticleListAdapter);
    listView.setOnKeyListener(new View.OnKeyListener() {
      @Override
      public boolean onKey(View view, int i, KeyEvent keyEvent) {
        ShortcutKeyEvent.computeSimpleArticleKeyEvent(fragment, listView, keyEvent);
        return false;
      }
    });

    // 상단을 끌면 새로고침
    swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.recommend_article_list_swipe_layout);
    swipeRefreshLayout.setOnRefreshListener(
            ArticleListSwipeRefreshListener.newInstance(fragment));

    // 스크롤을 마지막까지 내렸을 때 다음 페이지를 로드
    listView.setOnScrollListener(ArticleListBottomListener.newInstance(fragment,
            swipeRefreshLayout));

    // 계속 검색 버튼 핸들링
    searchContinueBtn = (FloatingActionButton) view.findViewById(R.id.search_recommend_article_continue);
    searchContinueBtn.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.colorWhite));
    setSearchContinueButton(searchContinueBtn);

    hideSearchContinueBtn();
  }

  private void setSearchContinueButton(FloatingActionButton searchContinueBtn) {
    searchContinueBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        ArticleListBottomListener.action(swipeRefreshLayout, fragment, true);
      }
    });
  }

  @Override
  public void clearItem() {
    simpleArticleListAdapter.clearItem();
    notifyDataChanged();
    swipeRefreshLayout.setRefreshing(false);
  }

  @Override
  public void addItems(List<SimpleArticle> simpleArticles) {
    simpleArticleListAdapter.loadCurrentData();
    for (SimpleArticle simpleArticle : simpleArticles) {
      simpleArticleListAdapter.addItem(simpleArticle);
    }
    notifyDataChanged();
    swipeRefreshLayout.setRefreshing(false);
  }

  @Override
  public void notifyDataChanged() {
    simpleArticleListAdapter.notifyDataSetChanged();
  }


  @Override
  public void showSearchContinueBtn() {
    searchContinueBtn.setVisibility(View.VISIBLE);
  }

  @Override
  public void hideSearchContinueBtn() {
    final EditText searchEditText = (EditText) fragment.getActivity().findViewById(R.id.toolbar_search_edit);
    final ImageView searchClose = (ImageView) fragment.getActivity().findViewById(R.id.toolbar_search_close);
    searchEditText.setText("");
    searchEditText.setVisibility(View.INVISIBLE);
    searchClose.setVisibility(View.INVISIBLE);
    searchContinueBtn.setVisibility(View.GONE);
  }

  @Override
  public void startRefreshing() {
    swipeRefreshLayout.setRefreshing(true);
  }

  @Override
  public void stopRefreshing() {
    swipeRefreshLayout.setRefreshing(false);
  }

  @Override
  public SwipeRefreshLayout getSwipeRefreshLayout() {
    return swipeRefreshLayout;
  }

  @Override
  public ListView getListView() {
    return this.listView;
  }

}

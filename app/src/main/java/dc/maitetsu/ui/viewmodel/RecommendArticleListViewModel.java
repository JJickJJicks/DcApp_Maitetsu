package dc.maitetsu.ui.viewmodel;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;
import dc.maitetsu.R;
import dc.maitetsu.data.CurrentData;
import dc.maitetsu.models.SimpleArticle;
import dc.maitetsu.ui.adapter.SimpleArticleListAdapter;
import dc.maitetsu.ui.fragment.RecommendArticleListFragment;
import dc.maitetsu.ui.listener.ArticleListBottomListener;
import dc.maitetsu.ui.listener.ArticleListSwipeRefreshListener;

import java.util.List;

/**
 * @author Park Hyo Jun
 * @since 2017-04-23
 *
 * 개념글 목록 프레젠터
 */
public class RecommendArticleListViewModel implements HasAdapterViewModel<SimpleArticle> {
  private SimpleArticleListAdapter simpleArticleListAdapter;
  private SwipeRefreshLayout swipeRefreshLayout;

  public RecommendArticleListViewModel(RecommendArticleListFragment fragment, View view, CurrentData currentData) {
    this.simpleArticleListAdapter = new SimpleArticleListAdapter(fragment);

    // 게시물 리스트뷰 처리
    ListView listView = (ListView) view.findViewById(R.id.recommend_article_list);
    listView.setAdapter(simpleArticleListAdapter);

    // 상단을 끌면 새로고침
    swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.recommend_article_list_swipe_layout);
    swipeRefreshLayout.setOnRefreshListener(
            ArticleListSwipeRefreshListener.newInstance(fragment));

    // 스크롤을 마지막까지 내렸을 때 다음 페이지를 로드
    listView.setOnScrollListener(ArticleListBottomListener.newInstance(fragment,
            swipeRefreshLayout));
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
    //TODO 개념글 검색
  }

  @Override
  public void hideSearchContinueBtn() {
    //TODO 개념글 검색
  }

  @Override
  public void startRefreshing() {
    swipeRefreshLayout.setRefreshing(true);
  }

  @Override
  public void stopRefreshing() {
    swipeRefreshLayout.setRefreshing(false);
  }

}

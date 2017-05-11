package dc.maitetsu.ui.viewmodel;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import dc.maitetsu.R;
import dc.maitetsu.data.CurrentData;
import dc.maitetsu.data.CurrentDataManager;
import dc.maitetsu.enums.RequestCodes;
import dc.maitetsu.models.SimpleArticle;
import dc.maitetsu.ui.ArticleWriteActivity;
import dc.maitetsu.ui.adapter.SimpleArticleListAdapter;
import dc.maitetsu.ui.fragment.SimpleArticleListFragment;
import dc.maitetsu.ui.listener.ArticleListBottomListener;
import dc.maitetsu.ui.listener.ArticleListSwipeRefreshListener;

import java.util.List;

/**
 * @author Park Hyo Jun
 * @since 2017-04-22
 *
 * 게시글 목록 프레젠터
 *
 */
public class SimpleArticleListViewModel implements HasAdapterViewModel<SimpleArticle> {
  private SimpleArticleListAdapter simpleArticleListAdapter;
  private SwipeRefreshLayout swipeRefreshLayout;
  private SimpleArticleListFragment fragment;
  private View view;
  private FloatingActionButton searchContinueBtn;

  public SimpleArticleListViewModel(SimpleArticleListFragment fragment, View view) {
    CurrentData currentData = CurrentDataManager.getInstance(fragment.getContext());
    this.simpleArticleListAdapter = new SimpleArticleListAdapter(fragment);
    this.fragment = fragment;
    this.view = view;

    // 게시물 리스트뷰
    ListView listView = (ListView) view.findViewById(R.id.article_list);
    listView.setAdapter(simpleArticleListAdapter);

    // 상단 끌어서 갱신
    swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.article_list_swipe_layout);
    swipeRefreshLayout.setOnRefreshListener(
            ArticleListSwipeRefreshListener.newInstance(fragment));

    // 검색 시 검색 스킵버튼 활성화
    searchContinueBtn = (FloatingActionButton) view.findViewById(R.id.search_article_continue);
    searchContinueBtn.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.colorWhite));
    setSearchContinueButton(searchContinueBtn);

    setWriteButton(currentData);
    listView.setOnScrollListener(ArticleListBottomListener.newInstance(fragment, swipeRefreshLayout));
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

  private void setSearchContinueButton(FloatingActionButton searchContinueBtn) {
    searchContinueBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        ArticleListBottomListener.action(swipeRefreshLayout, fragment, true);
      }
    });
  }


  @Override
  public void addItems(List<SimpleArticle> simpleArticleList) {
    simpleArticleListAdapter.loadCurrentData();
    for (SimpleArticle simpleArticle : simpleArticleList) {
      simpleArticleListAdapter.addItem(simpleArticle);
    }
    notifyDataChanged();
    swipeRefreshLayout.setRefreshing(false);
  }

  @Override
  public void clearItem() {
    simpleArticleListAdapter.clearItem();
    notifyDataChanged();
    swipeRefreshLayout.setRefreshing(false);
  }


  private void setWriteButton(final CurrentData currentData) {
    FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
    fab.getDrawable().setColorFilter(
            ContextCompat.getColor(fragment.getContext(), R.color.colorWhite),
            PorterDuff.Mode.SRC_IN);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), ArticleWriteActivity.class);
        intent.putExtra("currentData", currentData);
        fragment.getActivity()
                .startActivityForResult(intent, RequestCodes.ARTICLE.ordinal());
      }
    });
  }

  @Override
  public void notifyDataChanged() {
    simpleArticleListAdapter.notifyDataSetChanged();
  }
}

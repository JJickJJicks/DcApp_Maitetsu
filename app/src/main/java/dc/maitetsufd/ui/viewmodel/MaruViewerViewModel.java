package dc.maitetsufd.ui.viewmodel;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import dc.maitetsufd.R;
import dc.maitetsufd.data.CurrentData;
import dc.maitetsufd.data.CurrentDataManager;
import dc.maitetsufd.models.MangaSimpleModel;
import dc.maitetsufd.utils.MainUIThread;
import dc.maitetsufd.ui.adapter.MaruListAdapter;
import dc.maitetsufd.ui.fragment.MangaViewerFragment;
import dc.maitetsufd.ui.listener.EnterKeyListener;
import dc.maitetsufd.ui.listener.MaruListBottomListener;
import dc.maitetsufd.ui.listener.MaruListSwipeRefreshListener;
import dc.maitetsufd.utils.ContentUtils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 2017-04-29
 *
 * 마루 만화 뷰 프레젠터
 *
 */
public class MaruViewerViewModel {
  private MangaViewerFragment fragment;
  private MaruListAdapter adapter;
  private SwipeRefreshLayout swipeRefreshLayout;
  private View view;
  @Getter
  private List<ImageView> thumbList = new ArrayList<>();

  public MaruViewerViewModel(MangaViewerFragment fragment, View view, CurrentData currentData) {
    this.fragment = fragment;
    this.view = view;
    ListView listView = (ListView) view.findViewById(R.id.dcmys_maru_listview);
    adapter = new MaruListAdapter(fragment, this, currentData);
    listView.setAdapter(adapter);

    swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.dcmys_swipe_layout);
    swipeRefreshLayout.setOnRefreshListener(
            MaruListSwipeRefreshListener.newInstance(fragment));
    listView.setOnScrollListener(MaruListBottomListener.newInstance(fragment, swipeRefreshLayout));

    setSearchTool();
  }

  private void setSearchTool() {

    final EditText keyword = (EditText) view
            .findViewById(R.id.maru_keyword);
    final ImageButton clearButton = (ImageButton) view
            .findViewById(R.id.maru_clear_button);
    final ImageButton searchButton = (ImageButton) view
            .findViewById(R.id.maru_search_button);

    searchButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        CurrentData currentData = CurrentDataManager.getInstance(fragment.getContext());
        currentData.setSearchWord(keyword.getText().toString());
//        CurrentDataManager.save(currentData, fragment.getContext());
        MainUIThread.refreshMaruListView(fragment, false);
      }
    });


    clearButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        keyword.setText("");
      }
    });

    keyword.setOnKeyListener(EnterKeyListener.get(searchButton));


  }

  public void clearItem() {
    adapter.clearItems();
    adapter.notifyDataSetChanged();
    for (ImageView imageView : thumbList) {
      imageView.setImageDrawable(null);
    }
    ContentUtils.clearContext(fragment.getContext());
  }

  public void addItems(List<MangaSimpleModel> simpleArticles) {
    adapter.refreshCurrentData();

    for (MangaSimpleModel simpleArticle : simpleArticles) {
      adapter.addItem(simpleArticle);
    }
    adapter.notifyDataSetChanged();
  }

  public void stopRefreshing() {
    swipeRefreshLayout.setRefreshing(false);
  }

  public void startRefreshing() {
    swipeRefreshLayout.setRefreshing(true);
  }

}

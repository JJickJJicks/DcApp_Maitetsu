package dc.maitetsu.ui.viewmodel;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import dc.maitetsu.R;
import dc.maitetsu.data.CurrentData;
import dc.maitetsu.data.CurrentDataManager;
import dc.maitetsu.models.MaruModel;
import dc.maitetsu.utils.MainUIThread;
import dc.maitetsu.ui.adapter.MaruListAdapter;
import dc.maitetsu.ui.fragment.MaruViewerFragment;
import dc.maitetsu.ui.listener.EnterKeyListener;
import dc.maitetsu.ui.listener.MaruListBottomListener;
import dc.maitetsu.ui.listener.MaruListSwipeRefreshListener;
import dc.maitetsu.utils.ContentUtils;
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
  private MaruViewerFragment fragment;
  private MaruListAdapter adapter;
  private SwipeRefreshLayout swipeRefreshLayout;
  private View view;
  @Getter
  private List<ImageView> thumbList = new ArrayList<>();

  public MaruViewerViewModel(MaruViewerFragment fragment, View view, CurrentData currentData) {
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

  public void addItems(List<MaruModel> simpleArticles) {
    adapter.refreshCurrentData();

    for (MaruModel simpleArticle : simpleArticles) {
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

package dc.maitetsu.ui.viewmodel;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import dc.maitetsu.R;
import dc.maitetsu.data.CurrentData;
import dc.maitetsu.models.GalleryInfo;
import dc.maitetsu.ui.adapter.VisitedGalleryListAdapter;
import dc.maitetsu.ui.adapter.SearchGalleryListAdapter;
import dc.maitetsu.data.CurrentDataManager;
import dc.maitetsu.ui.apperance.GalleryListStaticAppearance;
import dc.maitetsu.ui.fragment.GalleryListFragment;

import java.util.List;

/**
 * @since 2017-04-22
 */
public class GalleryListViewModel {
  private VisitedGalleryListAdapter visitedGalleryListAdapter;
  private SearchGalleryListAdapter searchGalleryListAdapter;
  private ListView searchGalleryList;
  private EditText searchName;
  private View view;
  private ImageButton closeButton;

  public GalleryListViewModel(GalleryListFragment fragment, View view, CurrentData currentData) {
    this.visitedGalleryListAdapter = new VisitedGalleryListAdapter(fragment, this,
                                                          view.getContext(), currentData);
    this.searchGalleryListAdapter = new SearchGalleryListAdapter(fragment, this);
    this.searchGalleryList = (ListView) view.findViewById(R.id.search_gallery_name_list);
    closeButton = (ImageButton) view.findViewById(R.id.gallery_search_result_close);
    this.searchName = (EditText) view.findViewById(R.id.search_gallery_name);
    this.view = view;
    new GalleryListStaticAppearance(fragment, this, view,
                                  visitedGalleryListAdapter,
                                  searchGalleryListAdapter).invoke();
  }

  /**
   * 갤러리 검색 결과창 닫기 메소드
   */
  public void closeSearchGalleryList(){
    closeButton.setVisibility(View.INVISIBLE);
    searchGalleryList.setVisibility(View.GONE);
  }

  /**
   * 갤러리 검색 결과 추가 메소드
   *
   * @param galleryList the gallery list
   */
  public void addGallerySearchResult(List<GalleryInfo> galleryList) {
    searchGalleryList.setVisibility(View.VISIBLE);
    closeButton.setVisibility(View.VISIBLE);
    searchName.setText("");
    searchGalleryListAdapter.addItems(galleryList);
  }

  /**
   * 방문한 갤러리가 없으면 메시지 출력
   */
  public void setVisitedGalleryMessage() {
    TextView msg = (TextView) view.findViewById(R.id.gallery_list_not_visited_gallery);
    int myGallSize = CurrentDataManager.getInstance(view.getContext()).getMyGalleryList().size();
    if (myGallSize == 0)
      msg.setVisibility(View.VISIBLE);
    else
      msg.setVisibility(View.GONE);
  }


  /**
   * 갤러리 정보를 방문 갤러리로 추가
   *
   * @param galleryInfo the gallery info
   */
  public void addVisitedGallery(GalleryInfo galleryInfo){
    CurrentData currentData = CurrentDataManager.getInstance(view.getContext());
    List<GalleryInfo> visitedGallery = currentData.getMyGalleryList();
    visitedGallery.remove(galleryInfo);
    visitedGallery.add(0, galleryInfo);
    currentData.getMyGalleryList().remove(galleryInfo);
    currentData.getMyGalleryList().add(0, galleryInfo);
    visitedGalleryListAdapter.saveVisitedGallerys();
    visitedGalleryListAdapter.removeItem(galleryInfo);
    visitedGalleryListAdapter.addItem(galleryInfo);
    setVisitedGalleryMessage();
    CurrentDataManager.save(view.getContext());
  }

}

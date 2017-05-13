package dc.maitetsu.ui.apperance;

import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import dc.maitetsu.R;
import dc.maitetsu.service.ServiceProvider;
import dc.maitetsu.ui.adapter.VisitedGalleryListAdapter;
import dc.maitetsu.ui.adapter.SearchGalleryListAdapter;
import dc.maitetsu.ui.viewmodel.GalleryListViewModel;

/**
 * @since 2017-04-25
 *
 * 갤러리 리스트의 비 동적인 부분을 처리하는 클래스
 *
 */
public class GalleryListStaticApperance {
  private GalleryListViewModel galleryListViewModel;
  private View view;
  private VisitedGalleryListAdapter visitedGalleryListAdapter;
  private SearchGalleryListAdapter searchGalleryListAdapter;
  private EditText searchName;

  public GalleryListStaticApperance(GalleryListViewModel galleryListViewModel,
                                    View view,
                                    VisitedGalleryListAdapter visitedGalleryListAdapter,
                                    SearchGalleryListAdapter searchGalleryListAdapter) {
    this.galleryListViewModel = galleryListViewModel;
    this.visitedGalleryListAdapter = visitedGalleryListAdapter;
    this.searchGalleryListAdapter = searchGalleryListAdapter;
    this.view = view;
    this.searchName = (EditText) view.findViewById(R.id.search_gallery_name);
  }

  public void invoke() {
    setVisitedGalleryList(view);
    setSearchGalleryList(view);
    setSearchName(searchName);
    setSearchButton(view);
    galleryListViewModel.setVisitedGalleryMessage();
    setCloseButon(view);
  }

  private void setVisitedGalleryList(View view) {
    ListView visitedGalleryList = (ListView) view.findViewById(R.id.visited_gallery_name_list);
    visitedGalleryList.setAdapter(visitedGalleryListAdapter);
  }

  private void setSearchGalleryList(View view) {
    ListView searchGalleryList = (ListView) view.findViewById(R.id.search_gallery_name_list);
    searchGalleryList.setAdapter(searchGalleryListAdapter);
  }

  private void setSearchName(final EditText searchName) {
    searchName.setOnKeyListener(new View.OnKeyListener() {
      @Override
      public boolean onKey(View view, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
          ServiceProvider.getInstance().searchGalleryName(searchName.getText().toString());
          return true;
        }
        return false;
      }
    });
  }

  private void setSearchButton(View view) {
    ImageButton searchButton = (ImageButton) view.findViewById(R.id.gallery_search_button);
    searchButton.getDrawable().setColorFilter(ContextCompat.getColor(view.getContext(), R.color.colorDarkGray),
            PorterDuff.Mode.SRC_IN);
    searchButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        ServiceProvider.getInstance().searchGalleryName(searchName.getText().toString());
      }
    });
  }

  private void setCloseButon(View view) {
    ImageButton closeButton = (ImageButton) view.findViewById(R.id.gallery_search_result_close);
    closeButton.getDrawable().setColorFilter(ContextCompat.getColor(view.getContext(), R.color.colorDarkGray),
            PorterDuff.Mode.SRC_IN);
    closeButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        searchGalleryListAdapter.clearItems();
        galleryListViewModel.closeSearchGalleryList();
      }
    });
  }
}

package dc.maitetsu.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import dc.maitetsu.R;
import dc.maitetsu.models.GalleryInfo;
import dc.maitetsu.ui.viewmodel.GalleryListViewModel;
import dc.maitetsu.utils.SelectViewPage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Park Hyo Jun
 * @since 2017-04-23
 *
 * 갤러리명을 검색했을 때 나오는 리스트 어댑터
 */
public class SearchGalleryListAdapter extends BaseAdapter {
  private List<GalleryInfo> galleryInfos = new ArrayList<>();
  private GalleryListViewModel galleryListViewModel;
  private Fragment fragment;

  public SearchGalleryListAdapter(Fragment fragment, GalleryListViewModel galleryListViewModel) {
    this.galleryListViewModel = galleryListViewModel;
    this.fragment = fragment;
  }

  @Override
  public int getCount() {
    return galleryInfos.size();
  }

  @Override
  public Object getItem(int i) {
    return galleryInfos.get(i);
  }

  @Override
  public long getItemId(int i) {
    return i;
  }

  @Override
  public View getView(int i, View view, ViewGroup viewGroup) {
    final Context context = viewGroup.getContext();

    if (view == null) {
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      view = inflater.inflate(R.layout.search_gallery_list_item, viewGroup, false);
    }

    setGalleryNameSearchResult(i, view);
    return view;
  }

  private void setGalleryNameSearchResult(final int i, View view) {
    TextView galleryName = (TextView) view.findViewById(R.id.search_gallery_list_title_item);
    galleryName.setText(galleryInfos.get(i).getGalleryName());
    galleryName.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        GalleryInfo target = galleryInfos.get(i);
        galleryListViewModel.addVisitedGallery(target);
        galleryListViewModel.closeSearchGalleryList();

        SelectViewPage.select(fragment.getActivity(), 1);
//        TabLayoutViewModel.getInstance().setTabAndTitle(1, currentData);
      }
    });
  }


  public void clearItems() {
    galleryInfos.clear();
    notifyDataSetChanged();
  }

  public void addItems(List<GalleryInfo> newGalleryInfos) {
    clearItems();
    galleryInfos.addAll(newGalleryInfos);
    notifyDataSetChanged();
  }
}

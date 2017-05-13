package dc.maitetsu.ui.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import dc.maitetsu.R;
import dc.maitetsu.data.CurrentData;
import dc.maitetsu.data.CurrentDataManager;
import dc.maitetsu.models.GalleryInfo;
import dc.maitetsu.ui.viewmodel.GalleryListViewModel;
import dc.maitetsu.utils.SelectViewPage;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 2017-04-23
 *
 * 내가 방문한 갤러리 리스트에 사용되는 어댑터
 *
 */
public class VisitedGalleryListAdapter extends BaseAdapter {
  private List<GalleryInfo> galleryInfos;
  private GalleryListViewModel galleryListViewModel;
  private Context context;
  private Fragment fragment;

  public VisitedGalleryListAdapter(Fragment fragment, GalleryListViewModel galleryListViewModel,
                                    Context context, CurrentData currentData) {
    this.fragment = fragment;
    this.galleryListViewModel = galleryListViewModel;
    this.context = context;
    this.galleryInfos = new ArrayList<>();
    galleryInfos.addAll(currentData.getMyGalleryList());
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
  public View getView(final int i, View view, ViewGroup viewGroup) {
    final Context context = viewGroup.getContext();

    if (view == null) {
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      view = inflater.inflate(R.layout.gallery_list_item, viewGroup, false);
    }
    setGalleryNameAndClickEvent(i, view);
    setGalleryDeleteButton(i, view);
    notifyDataSetChanged();
    return view;
  }

  // 방문 갤러리의 삭제 버튼 핸들링
  private void setGalleryDeleteButton(final int i, View view) {
    ImageView galleryDelete = (ImageView) view.findViewById(R.id.maru_list_item_thumb);
    galleryDelete.getDrawable().setColorFilter(ContextCompat.getColor(view.getContext(), R.color.colorGray),
            PorterDuff.Mode.SRC_IN);

    // 클릭 이벤트
    galleryDelete.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if(galleryInfos.size() > 0) {
          CurrentData currentData = CurrentDataManager.getInstance(view.getContext());
          galleryInfos.remove(i);
          currentData.getMyGalleryList().remove(i);
          saveVisitedGallerys();
          galleryListViewModel.setVisitedGalleryMessage();
        }
      }
    });
  }

  // 갤러리 이름과 이벤트 핸들링
  private void setGalleryNameAndClickEvent(final int i, View view) {
    TextView galleryName = (TextView) view.findViewById(R.id.maru_list_item_title);
    galleryName.setText(galleryInfos.get(i).getGalleryName());

    // 클릭 이벤트
    galleryName.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        CurrentData currentData = CurrentDataManager.getInstance(view.getContext());
        GalleryInfo target = galleryInfos.get(i);
        galleryInfos.remove(i);
        galleryInfos.add(0, target);
        currentData.getMyGalleryList().remove(i);
        currentData.getMyGalleryList().add(0, target);
        saveVisitedGallerys();
        SelectViewPage.select(fragment.getActivity(), 1);
      }
    });
  }

  /**
   * 방문 갤러리 어댑터 리스트에 추가하는 메소드
   *
   * @param galleryInfo the gallery info
   */
  public void addItem(GalleryInfo galleryInfo){
    this.galleryInfos.add(0, galleryInfo);
    notifyDataSetChanged();
  }

  /**
   * 방문 갤러리 어댑터 리스트에서 삭제하는 메소드
   *
   * @param galleryInfo the gallery info
   */
  public void removeItem(GalleryInfo galleryInfo) {
    galleryInfos.remove(galleryInfo);
    notifyDataSetChanged();
  }


  /**
   * 방문 갤러리 목록을 저장하고 목록을 갱신하는 메소드
   *
   */
  public void saveVisitedGallerys(){
    CurrentDataManager.save(context);
    notifyDataSetChanged();
  }



}

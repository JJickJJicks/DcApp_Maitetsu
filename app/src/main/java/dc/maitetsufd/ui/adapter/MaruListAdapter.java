package dc.maitetsufd.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import dc.maitetsufd.R;
import dc.maitetsufd.data.CurrentData;
import dc.maitetsufd.data.CurrentDataManager;
import dc.maitetsufd.models.MangaSimpleModel;
import dc.maitetsufd.ui.MaruViewerDetailActivity;
import dc.maitetsufd.ui.viewmodel.MaruViewerViewModel;
import dc.maitetsufd.utils.ContentUtils;
import dc.maitetsufd.utils.KeywordUtils;
import dc.maitetsufd.utils.VibrateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 2017-04-29
 */
public class MaruListAdapter extends BaseAdapter {
  private List<MangaSimpleModel> mangaSimpleModels = new ArrayList<>();
  private MaruViewerViewModel viewModel;
  private Fragment fragment;
  private CurrentData currentData;

  public MaruListAdapter(Fragment fragment, MaruViewerViewModel viewModel, CurrentData currentData) {
    this.viewModel = viewModel;
    this.fragment = fragment;
    this.currentData = currentData;
  }

  @Override
  public int getCount() {
    return mangaSimpleModels.size();
  }

  @Override
  public Object getItem(int i) {
    return mangaSimpleModels.get(i);
  }

  @Override
  public long getItemId(int i) {
    return i;
  }

  public void addItem(MangaSimpleModel mangaSimpleModel) {
    mangaSimpleModels.add(mangaSimpleModel);
  }

  public void clearItems() {
    mangaSimpleModels.clear();
  }

  public void refreshCurrentData(){
    currentData = CurrentDataManager.getInstance(fragment.getContext());
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    final Context context = parent.getContext();

    if (convertView == null) {
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      convertView = inflater.inflate(R.layout.maru_list_item, parent, false);
    }

    ImageView thumb = (ImageView) convertView.findViewById(R.id.maru_list_item_thumb);
    TextView title = (TextView) convertView.findViewById(R.id.maru_list_item_title);
    TextView date = (TextView) convertView.findViewById(R.id.maru_list_item_date);

    final MangaSimpleModel model = mangaSimpleModels.get(position);

    // 섬네일 로드
    ContentUtils.loadBitmapFromUrl(fragment.getActivity(), 0, null, model.getThumbUrl(),
                            model.getThumbUrl(), thumb, currentData);
    viewModel.getThumbList().add(thumb);

    // 제목 날짜
    title.setText(KeywordUtils.getBuilder(model.getTitle(), currentData.getSearchWord(), null),
            TextView.BufferType.SPANNABLE);
    date.setText(model.getDate());



    convertView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(fragment.getActivity(), MaruViewerDetailActivity.class);
        //추가정보
        intent.putExtra("simpleData", model);

        if(CurrentDataManager.getInstance(fragment.getActivity().getApplicationContext()).isArticleTabVib())
          VibrateUtils.call(fragment.getActivity().getApplicationContext(), VibrateUtils.VIBRATE_DURATION_SHORT);

        fragment.startActivity(intent);
      }
    });

    return convertView;
  }
}

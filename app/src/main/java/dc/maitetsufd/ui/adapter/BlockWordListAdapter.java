package dc.maitetsufd.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import dc.maitetsufd.R;
import dc.maitetsufd.models.UserInfo;
import dc.maitetsufd.utils.UserTypeManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 2017-04-25
 *
 * 설정의 차단 유저 관리 액티비티에서 사용되는 어댑터.
 *
 */
public class BlockWordListAdapter extends BaseAdapter{
  private List<String> blockWords = new ArrayList<>();

  @Override
  public int getCount() {
    return blockWords.size();
  }

  @Override
  public Object getItem(int i) {
    return blockWords.get(i);
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
      view = inflater.inflate(R.layout.filter_user_list_item, viewGroup, false);
    }

    final Resources res = view.getResources();

    // 차단 단어
    String word = blockWords.get(i);

    TextView wordView = (TextView) view.findViewById(R.id.maru_list_item_title);
    wordView.setText(word);

    // 아이콘 감추기
    ImageView userType = (ImageView) view.findViewById(R.id.filter_user_list_item_userType);
    userType.setVisibility(View.INVISIBLE);

    // 삭제버튼
    ImageView delButton = (ImageView) view.findViewById(R.id.maru_list_item_thumb);
    delButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        blockWords.remove(i);
        notifyDataSetChanged();
      }
    });
    return view;
  }

  /**
   * 유저 정보를 입력하는 메소드
   *
   * @param blockwords the user infos
   */
  public void addAllBlockWords(List<String> blockwords) {
    this.blockWords.addAll(blockwords);
    notifyDataSetChanged();
  }

  public void addBlockWord(String word) {
    this.blockWords.add(word);
    notifyDataSetChanged();
  }

  /**
   * 유저 정보를 반환하는 메소드
   *
   * @return the list
   */
  public List<String> getBlockWords(){
    return this.blockWords;
  }

}

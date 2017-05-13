package dc.maitetsu.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import dc.maitetsu.R;
import dc.maitetsu.models.UserInfo;
import dc.maitetsu.utils.UserTypeManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 2017-04-25
 *
 * 설정의 차단 유저 관리 액티비티에서 사용되는 어댑터.
 *
 */
public class FilterUserListAdapter extends BaseAdapter{
  private List<UserInfo> filterUsers = new ArrayList<>();

  @Override
  public int getCount() {
    return filterUsers.size();
  }

  @Override
  public Object getItem(int i) {
    return filterUsers.get(i);
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

    // 유저 닉네임과 유저타입
    UserInfo userInfo = filterUsers.get(i);
    ImageView userType = (ImageView) view.findViewById(R.id.filter_user_list_item_userType);
    UserTypeManager.set(res, userInfo, userType);

    TextView nickname = (TextView) view.findViewById(R.id.maru_list_item_title);
    nickname.setText(userInfo.getNickname());

    // 삭제버튼
    ImageView delButton = (ImageView) view.findViewById(R.id.maru_list_item_thumb);
    delButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        filterUsers.remove(i);
        notifyDataSetChanged();
      }
    });
    return view;
  }

  /**
   * 유저 정보를 입력하는 메소드
   *
   * @param userInfos the user infos
   */
  public void addAllUserInfo(List<UserInfo> userInfos) {
    filterUsers.addAll(userInfos);
    notifyDataSetChanged();
  }

  /**
   * 유저 정보를 반환하는 메소드
   *
   * @return the list
   */
  public List<UserInfo> getFilterUsers(){
    return this.filterUsers;
  }

}

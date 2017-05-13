package dc.maitetsu.ui.viewmodel;

import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import dc.maitetsu.R;
import dc.maitetsu.data.CurrentData;
import dc.maitetsu.data.CurrentDataManager;
import dc.maitetsu.ui.FilterUserListActivity;
import dc.maitetsu.utils.MainUIThread;
import dc.maitetsu.ui.adapter.FilterUserListAdapter;

/**
 * @since 2017-04-25
 *
 * 차단 유저 리스트 뷰모델.
 */
public class FilterUserListViewModel {
  private FilterUserListActivity activity;
  private FilterUserListAdapter adapter;
  private CurrentData currentData;

  public FilterUserListViewModel(FilterUserListActivity activity, CurrentData currentData) {
    this.activity = activity;
    this.currentData = currentData;
    this.adapter = new FilterUserListAdapter();
    ListView filterUserListView = (ListView) activity.findViewById(R.id.filter_user_list);
    filterUserListView.setAdapter(this.adapter);
    this.adapter.addAllUserInfo(currentData.getFilterUserList());
    setCloseAndSaveButton();
  }

  // 닫기버튼, 저장버튼
  private void setCloseAndSaveButton() {
    Button closeButton = (Button) activity.findViewById(R.id.filter_user_cancle);
    closeButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        activity.finish();
      }
    });

    Button saveButton = (Button) activity.findViewById(R.id.filter_user_save);
    saveButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        currentData.setFilterUserList(adapter.getFilterUsers());
        CurrentDataManager.save(activity.getApplicationContext());
        MainUIThread.showToast(activity, activity.getString(R.string.save_msg));
        activity.finish();
      }
    });

  }


}

package dc.maitetsu.ui.listener;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import dc.maitetsu.data.CurrentData;
import dc.maitetsu.data.CurrentDataManager;
import dc.maitetsu.ui.viewmodel.HasViewModelFragment;
import dc.maitetsu.utils.MainUIThread;

/**
 * @author Park Hyo Jun
 * @since 2017-04-28
 *
 * 검색 툴바 세터.
 * 개념글 검색이 필요 할 수 있으므로 분리
 *
 */
public class SearchBtnListener {

  public static View.OnClickListener get(final HasViewModelFragment fragment,
                         final ImageView searchClose,
                         final ImageView searchOpen,
                         final EditText searchEdit) {

    // 엔터키 누르면 검색
    searchEdit.setOnKeyListener(EnterKeyListener.get(searchOpen));

    return new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (searchClose.getVisibility() == View.INVISIBLE) {
          searchClose.setVisibility(View.VISIBLE);
          searchEdit.setVisibility(View.VISIBLE);
          searchEdit.requestFocus();
          if(fragment.getActivity() != null)
          MainUIThread.showKeyboard(fragment.getActivity().getCurrentFocus());
        } else {
          String msg = searchEdit.getText().toString();
          if(!msg.trim().isEmpty()) {
            fragment.getHasAdapterViewModel().showSearchContinueBtn();
            MainUIThread.hideKeyboard(fragment.getView());
            CurrentData currentData = CurrentDataManager.getInstance(fragment.getActivity().getApplicationContext());
            currentData.setSearchWord(msg);
            MainUIThread.refreshArticleListView(fragment, false);
          }
        }
      }
    };

  }

}

package dc.maitetsu.ui.listener;

import android.view.View;
import dc.maitetsu.data.CurrentDataManager;
import dc.maitetsu.models.SimpleArticle;
import dc.maitetsu.service.ServiceProvider;
import dc.maitetsu.ui.viewmodel.HasViewModelFragment;
import dc.maitetsu.utils.VibrateUtils;

/**
 * @since 2017-04-23
 *
 * 게시물 목록을 눌렀을 때 이벤트 처리
 *
 */
public class SimpleArticleClickListener {

  public static View.OnClickListener get(final HasViewModelFragment fragment,
                                         final SimpleArticle simpleArticle) {
    return new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        fragment.getHasAdapterViewModel().startRefreshing();
        if(CurrentDataManager.getInstance(fragment.getActivity().getApplicationContext()).isArticleTabVib())
          VibrateUtils.call(fragment.getActivity().getApplicationContext(), VibrateUtils.VIBRATE_DURATION_SHORT);
        ServiceProvider.getInstance().openArticleDetail(fragment, simpleArticle.getUrl());
      }
    };
  }

}

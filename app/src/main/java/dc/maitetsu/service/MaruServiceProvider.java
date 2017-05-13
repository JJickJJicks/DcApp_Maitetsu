package dc.maitetsu.service;

import android.widget.ImageView;
import android.widget.LinearLayout;
import dc.maitetsu.R;
import dc.maitetsu.data.CurrentData;
import dc.maitetsu.models.MaruModel;
import dc.maitetsu.models.MaruSimpleModel;
import dc.maitetsu.utils.MainUIThread;
import dc.maitetsu.ui.MaruViewerDetailActivity;
import dc.maitetsu.ui.fragment.MaruViewerFragment;
import dc.maitetsu.utils.ThreadPoolManager;

import java.util.List;

/**
 * @since 2017-04-29
 */
public class MaruServiceProvider {
  // 갤럭시 S6 Useragent
  private static String USER_AGENT = "Mozilla/5.0 (Linux; Android 5.1.1; SM-G925F Build/LMY47X) " +
          "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.94 Mobile Safari/537.36";
  private static MaruServiceProvider serviceProvider = null;


  private MaruServiceProvider() {}

  /**
   * service provider 객체를 리턴하는 메소드
   *
   * @return the service provider
   */
  public static MaruServiceProvider getInstance() {

    if (serviceProvider == null) {
      serviceProvider = new MaruServiceProvider();
    }
    return serviceProvider;
  }


  public void getMaruSimpleModels(final MaruViewerFragment fragment,
                                         final int page, final String keyword,
                                  final boolean doClear) {
    ThreadPoolManager.getServiceEc().submit(new Runnable() {
      @Override
      public void run() {
        try {
          List<MaruSimpleModel> maruSimpleModels
                  = MaruService.getInstance.getMaruSimpleModels(USER_AGENT, page, keyword);
          MainUIThread.setMaruSearchResult(maruSimpleModels, fragment, doClear);
          MainUIThread.showSnackBar(fragment.getView(), fragment.getActivity().getString(R.string.article_list_ok));
        }catch(Exception e) {
          MainUIThread.showToast(fragment.getActivity(), fragment.getActivity()
                  .getString(R.string.article_load_failure));
        }
      }
    });
  }


  public void addMaruImages(final String no,
                            final MaruViewerDetailActivity activity,
                            final CurrentData currentData,
                            final LinearLayout layout,
                            final List<ImageView> imageViews) {
    ThreadPoolManager.getServiceEc().submit(new Runnable() {
      @Override
      public void run() {
        try {
          MaruModel maruModel = MaruService.getInstance.getImageUrls(USER_AGENT, no);
          maruModel.setNo(no);
          MainUIThread.addMaruImage(activity, currentData, layout, imageViews, maruModel);
        } catch (Exception e) {
          MainUIThread.showToast(activity, activity.getString(R.string.image_load_failure));
        }

      }
    });
  }




}

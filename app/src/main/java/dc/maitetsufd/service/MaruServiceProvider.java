package dc.maitetsufd.service;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.*;
import dc.maitetsufd.R;
import dc.maitetsufd.data.CurrentData;
import dc.maitetsufd.data.CurrentDataManager;
import dc.maitetsufd.models.MangaContentModel;
import dc.maitetsufd.models.MangaSimpleModel;
import dc.maitetsufd.ui.MaruViewerDetailActivity;
import dc.maitetsufd.ui.fragment.MangaViewerFragment;
import dc.maitetsufd.utils.MainUIThread;
import dc.maitetsufd.utils.ThreadPoolManager;
import dc.maitetsufd.utils.VibrateUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @since 2017-04-29
 */
public class MaruServiceProvider {
  // 갤럭시 S6 Useragent
  private static String USER_AGENT = "Mozilla/5.0 (Linux; Android 5.1.1; SM-G925F Build/LMY47X) " +
          "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.94 Mobile Safari/537.36";
  private static MaruServiceProvider serviceProvider = null;
  private static IMangaService mangaService = MaruService.getInstance;


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


  public void getMaruSimpleModels(final MangaViewerFragment fragment,
                                         final int page, final String keyword,
                                  final boolean doClear) {
    ThreadPoolManager.getServiceEc().submit(new Runnable() {
      @Override
      public void run() {
        try {
          List<MangaSimpleModel> mangaSimpleModels = mangaService.getSimpleModels(USER_AGENT, page, keyword);
          MainUIThread.setMaruSearchResult(mangaSimpleModels, fragment, doClear);
          MainUIThread.showSnackBar(fragment.getView(), fragment.getActivity().getString(R.string.article_list_ok));
        }catch(Exception e) {
//          val intent = new Intent(fragment.getActivity(), SeleniumActivity.class);
//          fragment.startActivity(intent);
          MainUIThread.showToast(fragment.getActivity(), fragment.getActivity()
                  .getString(R.string.article_load_failure));
        }
      }
    });
  }


  public void addMaruImages(final String no,
                            final MaruViewerDetailActivity activity,
                            final CurrentData currentData,
                            final ScrollView scrollView,
                            final LinearLayout layout,
                            final List<ImageView> imageViews,
                            final boolean isViewerModel) {
    ThreadPoolManager.getServiceEc().submit(new Runnable() {
      @Override
      public void run() {
        for(int i=0; i<5; i++) {
          try {
            MangaContentModel mangaContentModel = mangaService.getContentModel(USER_AGENT, no, isViewerModel, 0);
            setMaruActivityDetail(activity, mangaContentModel);
            MainUIThread.addMaruImage(activity, currentData, scrollView, layout, imageViews, mangaContentModel, 0);
            break;
          } catch (Exception e) {
            if(i == 4) {
              MainUIThread.showToast(activity, activity.getString(R.string.image_load_failure));
            }
          }
        }
      }
    });
  }

  private void setMaruActivityDetail(final MaruViewerDetailActivity activity,
                                     final MangaContentModel mangaContentModel) {
    final TextView pageTitle = (TextView) activity.findViewById(R.id.maru_detail_view_title);
    final Button nextEpisode = (Button) activity.findViewById(R.id.maru_detail_next);
    final Spinner episodesSpinner = (Spinner) activity.findViewById(R.id.maru_detail_episodes);

    activity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        // 다음화 버튼
        pageTitle.setText(mangaContentModel.getTitle());
        if(mangaContentModel.getEpisodes().size() > mangaContentModel.getEpisodeNum() + 1) {
          nextEpisode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              MangaSimpleModel model = new MangaSimpleModel();
              MangaContentModel.MaruEpisode nextEpisode = mangaContentModel.getEpisodes().get(mangaContentModel.getEpisodeNum() + 1);
              model.setNo(nextEpisode.getEpisodeNo());
              model.setTitle(nextEpisode.getEpisodeName());
              model.setViewerModel(true);
              maruDetailView(activity, model);
            }
          });
        } else {
          nextEpisode.setVisibility(View.INVISIBLE);
        }

      // 에피소드 목록 스피너
        final String[] episodeNames = new String[mangaContentModel.getEpisodes().size()];
        for (int i=0; i<episodeNames.length; i++) {
          episodeNames[i] = mangaContentModel.getEpisodes().get(i).getEpisodeName();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity,
                android.R.layout.simple_dropdown_item_1line, episodeNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        episodesSpinner.setAdapter(adapter);
        episodesSpinner.setSelection(mangaContentModel.getEpisodeNum());
        episodesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if(i == mangaContentModel.getEpisodeNum()) return;
            MangaSimpleModel model = new MangaSimpleModel();
            MangaContentModel.MaruEpisode nextEpisode = mangaContentModel.getEpisodes().get(i);
            model.setNo(nextEpisode.getEpisodeNo());
            model.setTitle(nextEpisode.getEpisodeName());
            model.setViewerModel(true);
            maruDetailView(activity, model);
          }

          @Override
          public void onNothingSelected(AdapterView<?> adapterView) {

          }
        });
      }
    });
  }


  public void postCaptch(final String url, final String captcha) throws IOException {
    ThreadPoolManager.getContentEc().submit(new Runnable() {
      @Override
      public void run() {
        try {
          MaruService.getInstance.postCaptcha(USER_AGENT, url, captcha);
        } catch (IOException e) { }
      }
    });
  }

  public void setContentCookies(Map<String, String> cookies) {
    MaruService.contentCookies.putAll(cookies);
  }
  public Map<String, String> getContentCookies() {
    return MaruService.contentCookies;
  }

private void maruDetailView(Activity activity, MangaSimpleModel model) {
  Intent intent = new Intent(activity, MaruViewerDetailActivity.class);
  intent.putExtra("simpleData", model);
  if(CurrentDataManager.getInstance(activity.getApplicationContext()).isArticleTabVib())
    VibrateUtils.call(activity.getApplicationContext(), VibrateUtils.VIBRATE_DURATION_SHORT);
  activity.startActivity(intent);
  activity.finish();
}

}

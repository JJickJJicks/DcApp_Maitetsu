package dc.maitetsu.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import dc.maitetsu.R;
import dc.maitetsu.data.CurrentData;
import dc.maitetsu.data.CurrentDataManager;
import dc.maitetsu.enums.RequestCodes;
import dc.maitetsu.enums.ResultCodes;
import dc.maitetsu.models.*;
import dc.maitetsu.ui.ArticleWriteActivity;
import dc.maitetsu.ui.MainActivity;
import dc.maitetsu.ui.SplashActivity;
import dc.maitetsu.ui.fragment.GalleryListFragment;
import dc.maitetsu.ui.fragment.RecommendArticleListFragment;
import dc.maitetsu.ui.viewmodel.ArticleDetailViewModel;
import dc.maitetsu.ui.viewmodel.HasViewModelFragment;
import dc.maitetsu.utils.MainUIThread;
import dc.maitetsu.utils.ThreadPoolManager;
import dc.maitetsu.utils.UserFilter;

import java.io.File;
import java.util.List;

/**
 * @author Park Hyo Jun
 * @since 2017-04-21
 */
public class ServiceProvider {

  // 갤럭시 S6 Useragent
  private static String USER_AGENT = "Mozilla/5.0 (Linux; Android 5.1.1; SM-G925F Build/LMY47X) " +
          "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.94 Mobile Safari/537.36";

  private static ServiceProvider serviceProvider = null;


  private ServiceProvider() {
  }

  /**
   * service provider 객체를 리턴하는 메소드
   *
   * @return the service provider
   */
  public static ServiceProvider getInstance() {

    if (serviceProvider == null) {
      serviceProvider = new ServiceProvider();
    }
    return serviceProvider;
  }


  /**
   * 로그인하고 정보를 얻어오는 메소드
   *
   * @param activity 결과를 띄울 액티비티
   */
  public void login(final SplashActivity activity, final boolean resetMode) {

    ThreadPoolManager.getServiceEc().submit(new Runnable() {
      @Override
      public void run() {
        CurrentData currentData = CurrentDataManager.load(activity);

        // 빠른 로그인 사용시 이전 정보를 그대로 사용함
        if (!resetMode && currentData.isFastLogin()
                && currentData.getLoginCookies().get("mc_enc") != null) {

          if (isLoginCookieUseable(currentData)) {
            MainUIThread.setSplashText(activity, activity.getString(R.string.splash_login_keep));
            MainUIThread.finishActivity(activity, ResultCodes.LOGIN_SUCCESS);
            return;
          }
        }


        try {
          MainUIThread.setSplashText(activity, activity.getString(R.string.splash_login_try));
          currentData.setLoginCookies(LoginService.getInstance.login(currentData.getId(), currentData.getPw(), USER_AGENT).cookies());
          MainUIThread.setSplashText(activity, activity.getString(R.string.splash_login_success));
          getDcConList(activity, currentData);
        } catch (Exception e) {
          String appendMsg = "";
          if (e instanceof IllegalAccessException) appendMsg = e.getMessage();
          MainUIThread.showToast(activity, activity.getString(R.string.login_failure) + appendMsg);
          MainUIThread.finishActivity(activity, ResultCodes.LOGIN_FAIL);
        }
      }
    });

  }


  public void openArticleDetail(final HasViewModelFragment fragment,
                                final String articleUrl) {
    ThreadPoolManager.getServiceEc().submit(new Runnable() {
      @Override
      public void run() {
        CurrentData currentData = getCurrentData(fragment.getActivity().getApplicationContext());
        try {
          ArticleDetail articleDetail = ArticleDetailService.getInstance
                                                .getArticleDetail(currentData.getLoginCookies()
                                                                , USER_AGENT, articleUrl);
          articleDetail.setUrl(articleUrl);
          UserFilter.setComments(currentData, articleDetail.getComments());
          MainUIThread.openArticleDetail(fragment, articleDetail, articleUrl);
        } catch (Exception e) {
          fragment.getHasAdapterViewModel().stopRefreshing();
          MainUIThread.showToast(fragment.getActivity(),
                  fragment.getActivity().getString(R.string.article_load_failure));
        }
      }
    });
  }

  public void refreshArticleDetail(final Activity activity,
                                final String articleUrl) {
    ThreadPoolManager.getServiceEc().submit(new Runnable() {
      @Override
      public void run() {
        CurrentData currentData = getCurrentData(activity.getApplicationContext());
        try {
          ArticleDetail articleDetail = ArticleDetailService.getInstance
                  .getArticleDetail(currentData.getLoginCookies()
                          , USER_AGENT, articleUrl);
          articleDetail.setUrl(articleUrl);
          UserFilter.setComments(currentData, articleDetail.getComments());
          MainUIThread.finishActivity(activity, ResultCodes.NONE);
          MainUIThread.openArticleDetail(activity, articleDetail, articleUrl);
        } catch (Exception e) {
          MainUIThread.showToast(activity, activity.getString(R.string.article_load_failure));
        }
      }
    });
  }



  public void refreshComment(final Activity activity,
                             final ArticleDetailViewModel viewModel,
                             final String articleUrl) {
    ThreadPoolManager.getServiceEc().submit(new Runnable() {
      @Override
      public void run() {
        CurrentData currentData = getCurrentData(activity.getApplicationContext());
        try {
          ArticleDetail articleDetail = ArticleDetailService.getInstance
                  .getArticleDetail(currentData.getLoginCookies()
                                  , USER_AGENT, articleUrl);
          UserFilter.setComments(currentData, articleDetail.getComments());
          MainUIThread.refreshComment(activity, viewModel, articleDetail.getComments());
        } catch (Exception e) {
          MainUIThread.showToast(activity, activity.getString(R.string.comment_reload_failure));
        }
      }
    });
  }


  public void searchGalleryName(final String galleryName) {
    ThreadPoolManager.getServiceEc().submit(new Runnable() {
      @Override
      public void run() {
        try {
          List<GalleryInfo> galleryInfos = GalleryListService.getInstance
                                                  .searchGallery(USER_AGENT, galleryName);
          MainUIThread.setGallerySearchResult(galleryInfos, GalleryListFragment.getInstance());
        } catch (Exception e) {
          MainUIThread.showToast(GalleryListFragment.getInstance().getActivity(),
                  GalleryListFragment.getInstance().getString(R.string.gallery_search_failure));
        }
      }
    });
  }

  /**
   * SimpleArticle을 얻어오는 메소드.
   * Fragment의 종류에 따라 일반글/개념글 구분한다.
   *
   * @param fragment the fragment
   */
  public void getSimpleArticles(final HasViewModelFragment fragment,
                                final boolean refreshSerPos) {
    ThreadPoolManager.getServiceEc().submit(new Runnable() {
      @Override
      public void run() {


        try {
          CurrentData currentData = CurrentDataManager.getInstance(fragment.getActivity());
          boolean isRecommend = fragment instanceof RecommendArticleListFragment;
          boolean showSnackBar = true;
          List<SimpleArticle> simpleArticles = SimpleArticleService.getInstance
                                                .getSimpleArticles(currentData,
                                                            USER_AGENT, isRecommend, refreshSerPos);
          UserFilter.setSimpleArticles(currentData, simpleArticles);

          if (simpleArticles.size() == 0) {

            if (currentData.getLoginCookies().get("mc_enc") == null
                    || !isLoginCookieUseable(currentData)) {
              restartApplication(fragment);
              return;
            }
            MainUIThread.showToast(fragment.getActivity(), fragment.getActivity().getString(R.string.article_list_zero));
            currentData.setPage(currentData.getPage() - 1);
            showSnackBar = false;
          }

          currentData.setLastLogin(System.currentTimeMillis());
          MainUIThread.setArticleListView(fragment, simpleArticles, showSnackBar);
        } catch (Exception e1) {
          MainUIThread.showToast(fragment.getActivity(), fragment.getActivity().getString(R.string.article_list_failure));
        }
      }
    });
  }

  private void restartApplication(HasViewModelFragment fragment) {
    MainUIThread.showToast(fragment.getActivity(), fragment.getActivity().getString(R.string.splash_login_expire));
    Intent intent = new Intent(fragment.getActivity(), MainActivity.class);
    intent.putExtra("resetMode", true);
    fragment.getActivity().finish();
    fragment.getActivity().startActivity(intent);
  }

  public void writeArticle(final ArticleWriteActivity activity,
                           final Button writeButton, final String title,
                           final String content,
                           final List<File> files,
                           final ArticleModify articleModify) {
    ThreadPoolManager.getServiceEc().submit(new Runnable() {
      @Override
      public void run() {
        MainUIThread.setViewState(activity, writeButton, false);
        CurrentData currentData = getCurrentData(activity.getApplicationContext());
        try {
          String result = ArticleWriteService.getInstance .write(currentData.getLoginCookies(),
                                                                currentData.getGalleryInfo().getGalleryCode(),
                                                                files, USER_AGENT, title, content, articleModify);
          if (!result.trim().isEmpty()) throw new Exception(result);
          else {
            MainUIThread.showToast(activity, activity.getString(R.string.article_write_complete));
            MainUIThread.finishActivity(activity, ResultCodes.ARTICLE_REFRESH);
          }
        } catch (IllegalAccessException ie) {
          MainUIThread.showToast(activity, activity.getString(R.string.article_write_image_upload_failure));
        } catch (Exception e) {
          MainUIThread.showToast(activity, activity.getString(R.string.article_write_failure));
        }
        MainUIThread.setViewState(activity, writeButton, true);

      }
    });
  }

  public void recommendArticle(final ArticleDetail articleDetail,
                               final Activity activity) {
    ThreadPoolManager.getServiceEc().submit(new Runnable() {
      @Override
      public void run() {
        CurrentData currentData = getCurrentData(activity.getApplicationContext());

        try {
          if (RecommendService.getInstance.recommend(currentData.getLoginCookies(), articleDetail, USER_AGENT)) {
            MainUIThread.showToast(activity, activity.getString(R.string.article_read_recommend_success));
            currentData.getRecommendList().put(articleDetail.getUrl(), System.currentTimeMillis());
            CurrentDataManager.save(activity);
          } else throw new Exception();
        } catch (Exception e) {
          MainUIThread.showToast(activity, activity.getString(R.string.article_read_recommend_failure));
        }
      }
    });
  }


  public void deleteArticle(final View deleteButton,
                            final ArticleDetail articleDetail,
                            final Activity activity) {
    ThreadPoolManager.getServiceEc().submit(new Runnable() {
      @Override
      public void run() {
        try {
          MainUIThread.setViewState(activity, deleteButton, false);
          CurrentData currentData = CurrentDataManager.getInstance(activity.getApplicationContext());
          if (ArticleDeleteService.getInstance
                  .delete(currentData.getLoginCookies(), USER_AGENT, articleDetail)) {
            MainUIThread.finishActivity(activity, ResultCodes.ARTICLE_REFRESH);
            MainUIThread.showToast(activity, activity.getString(R.string.article_read_delete_success));
          } else throw new Exception();
        } catch (Exception e) {
          MainUIThread.showToast(activity, activity.getString(R.string.article_read_delete_failure));
        }
        MainUIThread.setViewState(activity, deleteButton, true);
      }
    });
  }


  public void deleteComment(final ArticleDetail articleDetail,
                            final ArticleDetailViewModel viewModel,
                            final String articleUrl,
                            final String deleteCode,
                            final Activity activity) {
    ThreadPoolManager.getServiceEc().submit(new Runnable() {
      @Override
      public void run() {
        CurrentData currentData = getCurrentData(activity.getApplicationContext());
        try {
          if (CommentDeleteService.getInstance
                  .delete(currentData.getLoginCookies(), USER_AGENT, articleDetail, deleteCode)) {
            MainUIThread.showToast(activity, activity.getString(R.string.comment_delete_success));
            refreshComment(activity, viewModel, articleUrl);
          } else throw new Exception();
        } catch (Exception e) {
          MainUIThread.showToast(activity, activity.getString(R.string.comment_delete_failure));
        }
      }
    });
  }


  /**
   * 댓글 추가 요청
   *
   * @param articleDetail the article detail
   * @param comment       the comment
   * @param button        the button
   * @param activity      the activity
   */
  public void writeComment(final ArticleDetail articleDetail,
                           final ArticleDetailViewModel viewModel,
                           final String articleUrl,
                           final String comment,
                           final View button,
                           final Activity activity) {
    ThreadPoolManager.getServiceEc().submit(new Runnable() {
      @Override
      public void run() {
        MainUIThread.setViewState(activity, button, false);
        CurrentData currentData = getCurrentData(activity.getApplicationContext());
        try {
          if (CommentWriteService.getInstance
                  .write(currentData.getLoginCookies(), USER_AGENT, articleDetail,
                  articleUrl, comment)) {
            MainUIThread.showToast(activity, activity.getString(R.string.comment_submit_success));
            refreshComment(activity, viewModel, articleUrl);
          } else throw new Exception();
        } catch (Exception e) {
          MainUIThread.showToast(activity, activity.getString(R.string.comment_submit_failure));
        }
        MainUIThread.setViewState(activity, button, true);
      }
    });
  }

  public void writeDcconComment(final Activity activity,
                                final ArticleDetailViewModel viewModel,
                                final View blockedView,
                                final ArticleDetail articleDetail,
                                final String articleUrl,
                                final DcConPackage.DcCon dcCon) {
    ThreadPoolManager.getServiceEc().submit(new Runnable() {
      @Override
      public void run() {
        MainUIThread.setViewState(activity, blockedView, false);
        CurrentData currentData = getCurrentData(activity.getApplicationContext());
        try {
          if (CommentWriteService.getInstance
                  .writeDcCon(currentData.getLoginCookies(), USER_AGENT, articleDetail, articleUrl, dcCon)) {
            MainUIThread.showToast(activity, activity.getString(R.string.comment_dccon_submit_success));
            refreshComment(activity, viewModel, articleUrl);
          } else throw new Exception();
        } catch (Exception e) {
          MainUIThread.showToast(activity, activity.getString(R.string.comment_dccon_submit_failure));
        }
        MainUIThread.setViewState(activity, blockedView, true);
      }
    });
  }

  // 디시콘 리스트를 읽어오는 메소드.
  private void getDcConList(final SplashActivity activity, final CurrentData currentData) {
    ThreadPoolManager.getServiceEc().submit(new Runnable() {
      @Override
      public void run() {
        try {
          MainUIThread.setSplashText(activity, activity.getString(R.string.dccon_list_load_try));
          currentData.setDcConPackages(DcConService.getInstance
                  .getDcConList(currentData.getLoginCookies(), USER_AGENT));
          currentData.setLastLogin(System.currentTimeMillis());
          CurrentDataManager.save(activity.getApplicationContext());

          MainUIThread.setSplashText(activity, activity.getString(R.string.dccon_list_load_success));
          MainUIThread.finishActivity(activity, ResultCodes.LOGIN_SUCCESS);
        } catch (Exception e) {
          MainUIThread.showToast(activity, activity.getString(R.string.dccon_list_load_failure));
          MainUIThread.finishActivity(activity, ResultCodes.LOGIN_FAIL);
        }

      }
    });
  }

  public void openArticleModify(final Activity activity,
                                final ArticleDetail articleDetail,
                                final CurrentData currentData) {
    ThreadPoolManager.getServiceEc().submit(new Runnable() {
      @Override
      public void run() {
        try {
          ArticleModify articleModify = ArticleModifyService.getInstance
                                          .getArticleModifyData(USER_AGENT,
                                                                articleDetail,
                                                                currentData.getLoginCookies());

          Intent intent = new Intent(activity, ArticleWriteActivity.class);
          intent.putExtra("articleModify", articleModify);
          activity.startActivityForResult(intent, RequestCodes.ARTICLE.ordinal());
        }catch(Exception e) {
          MainUIThread.showToast(activity, activity.getString(R.string.article_modify_try_failure));
        }
      }
    });
  }





  // 마지막 로그인 시간이 2시간 이내라면 true를 반환함.
  private static boolean isLoginCookieUseable(CurrentData currentData) {
    long lastTime = currentData.getLastLogin();
    long loginKeepTime = lastTime + (1000 * 60) * 60 * 2; // 2시간
    long currentTime = System.currentTimeMillis();
    return currentTime < loginKeepTime;
  }

  private static CurrentData getCurrentData(Context context) {
    return CurrentDataManager.getInstance(context);
  }


}

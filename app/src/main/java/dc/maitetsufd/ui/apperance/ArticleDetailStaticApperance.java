package dc.maitetsufd.ui.apperance;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import dc.maitetsufd.R;
import dc.maitetsufd.data.CurrentData;
import dc.maitetsufd.models.ArticleDetail;
import dc.maitetsufd.models.UserInfo;
import dc.maitetsufd.service.ServiceProvider;
import dc.maitetsufd.ui.ArticleDetailActivity;
import dc.maitetsufd.utils.MainUIThread;
import dc.maitetsufd.ui.fragment.ArticleDeleteDialogFragment;
import dc.maitetsufd.ui.viewmodel.ArticleDetailViewModel;
import dc.maitetsufd.utils.KeywordUtils;
import dc.maitetsufd.utils.UserTypeManager;

/**
 * @since 2017-04-25
 *
 *  댓글과 글 내용을 제외한 비 동적인 요소를 설정하는 클래스
 *
 */
public class ArticleDetailStaticApperance {
  private ArticleDetailActivity articleDetailActivity;
  private ArticleDetailViewModel presenter;
  private ArticleDetail articleDetail;
  private String articleUrl;
  private CurrentData currentData;
  private Resources res;

  public ArticleDetailStaticApperance(ArticleDetailActivity articleDetailActivity,
                                      ArticleDetailViewModel presenter,
                                      ArticleDetail articleDetail,
                                      String articleUrl,
                                      CurrentData currentData) {
    this.articleDetail = articleDetail;
    this.articleUrl = articleUrl;
    this.articleDetailActivity = articleDetailActivity;
    this.currentData = currentData;
    this.res = articleDetailActivity.getResources();
    this.presenter = presenter;
  }

  public void invoke() {
    setTitleAndTitleBar(articleDetailActivity);
    setNicknameAndUserType(articleDetailActivity, articleDetail);
    setDate(articleDetailActivity, articleDetail);
    setViewAndRecommend(articleDetailActivity, articleDetail);
    setRecommendIconAndText(articleDetailActivity, articleDetail);
    setNoRecommendIconAndText(articleDetailActivity, articleDetail);
    setCloseAndDeleteButton(articleDetailActivity, articleDetail);
    setCommentSubmitAndDcconButton(articleDetailActivity, articleDetail, articleUrl);
    setCommentTextClick(articleDetailActivity);
    setOpenBrowserButton(articleDetailActivity, articleDetail);
  }

  private void setOpenBrowserButton(final ArticleDetailActivity activity, final ArticleDetail articleDetail) {
    ImageView openBrowserBtn = (ImageView) activity.findViewById(R.id.article_detail_open_browser);
    openBrowserBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                                  Uri.parse(articleDetail.getUrl()));
        activity.startActivity(intent);
      }
    });
  }

  // 댓글 입력창 핸들링
  private void setCommentTextClick(final ArticleDetailActivity articleDetailActivity) {
    // 댓글 입력창 클릭시 디시콘 레이아웃을 닫게함
    presenter.commentText.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        articleDetailActivity.hideDcconLayout();
      }
    });
  }

  private void clearCommentText() {
    presenter.commentText.setText("");
  }

  // 상단 바 타이틀 핸들링
  private void setTitleAndTitleBar(Activity activity) {
    TextView title = (TextView) activity.findViewById(R.id.article_read_title);
    SpannableStringBuilder builder = KeywordUtils.getBuilder(articleDetail.getTitle(),
                                          currentData.getSearchWord(), null);
    title.setText(builder, TextView.BufferType.SPANNABLE);



    TextView titleBar = (TextView) activity.findViewById(R.id.article_read_title_bar);
    String titleText = res.getString(R.string.read_article_title_bar);
    titleBar.setText(String.format(titleText, currentData.getGalleryInfo().getGalleryName()));
  }

  // 닉네임과 유저 타입 핸들링
  private void setNicknameAndUserType(Activity activity, ArticleDetail articleDetail) {
    UserInfo userInfo = articleDetail.getUserInfo();
    TextView nickname = (TextView) activity.findViewById(R.id.article_read_nickname);
    ImageView userType = (ImageView) activity.findViewById(R.id.article_read_user_type);
    nickname.setText(userInfo.getNickname());
    UserTypeManager.set(res, userInfo, userType);

  }

  // 글 상단 작성일 핸들링
  private void setDate(Activity activity, ArticleDetail articleDetail) {
    TextView date = (TextView) activity.findViewById(R.id.article_read_date);
    date.setText(articleDetail.getDate());
  }

  // 글 상단 조회수와 추천 수 핸들링
  private void setViewAndRecommend(Activity activity, ArticleDetail articleDetail) {
    TextView view = (TextView) activity.findViewById(R.id.article_read_viewCount);
    TextView recommend = (TextView) activity.findViewById(R.id.article_read_recommendCount);
    view.setText(String.format(res.getString(R.string.view), articleDetail.getViewCount()));
    recommend.setText(String.format(res.getString(R.string.recommend), articleDetail.getRecommendCount()));
  }

  // 추천 버튼과 추천 수 핸들링 메소드
  private void setRecommendIconAndText(final Activity activity, final ArticleDetail articleDetail) {

    // 게시물의 추천 수와 추천 버튼을 핸들링
    final ImageView recommendIcon = (ImageView) activity.findViewById(R.id.article_read_recommend_icon);
    final TextView recommendIconCount = (TextView) activity.findViewById(R.id.article_read_recommend_icon_count);
    recommendIconCount.setText(String.format(res.getString(R.string.comment), articleDetail.getRecommendCount()));
    long timeOut = 1000 * 60 * 60 * 24; // 1일
    Long time = currentData.getRecommendList().get(articleDetail.getArticleDeleteData().getNo());

    if(time != null && time + timeOut > System.currentTimeMillis()) {
      // 추천 할 수 없으면 이미 눌린 색상으로 설정함
      recommendIcon.setColorFilter(ContextCompat.getColor(activity.getApplicationContext(), R.color.colorAccentYellow),
              PorterDuff.Mode.SRC_IN);
      return;
    }

    (activity.findViewById(R.id.article_read_recommend_icon)).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        ServiceProvider.getInstance().recommendArticle(articleDetail, activity);
        recommendIcon.setColorFilter(ContextCompat.getColor(activity.getApplicationContext(), R.color.colorAccentYellow),
                PorterDuff.Mode.SRC_IN);
        recommendIconCount.setText(String.format(res.getString(R.string.comment), articleDetail.getRecommendCount() + 1));
      }
    });
  }

  // 비추천 버튼과 추천 수 핸들링 메소드
  private void setNoRecommendIconAndText(final Activity activity, final ArticleDetail articleDetail) {

    // 게시물의 추천 수와 추천 버튼을 핸들링
    final ImageView noRecommendIcon = (ImageView) activity.findViewById(R.id.article_read_norecommend_icon);
    final TextView noRecommendIconCount = (TextView) activity.findViewById(R.id.article_read_norecommend_icon_count);
    noRecommendIconCount.setText(String.format(res.getString(R.string.comment), articleDetail.getNoRecommendCount()));
    long timeOut = 1000 * 60 * 60 * 24; // 1일
    Long time = currentData.getNoRecommendList().get(articleDetail.getArticleDeleteData().getNo());

    if(time != null && time + timeOut > System.currentTimeMillis()) {
      // 추천 할 수 없으면 이미 눌린 색상으로 설정함
      noRecommendIcon.setColorFilter(ContextCompat.getColor(activity.getApplicationContext(), R.color.colorAccentYellow),
              PorterDuff.Mode.SRC_IN);
      return;
    }

    (activity.findViewById(R.id.article_read_norecommend_icon)).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        ServiceProvider.getInstance().noRecommendArticle(articleDetail, activity);
        noRecommendIcon.setColorFilter(ContextCompat.getColor(activity.getApplicationContext(), R.color.colorAccentYellow),
                PorterDuff.Mode.SRC_IN);
        noRecommendIconCount.setText(String.format(res.getString(R.string.comment), articleDetail.getNoRecommendCount() + 1));
      }
    });
  }

  // 닫기 버튼과 삭제 버튼 핸들링 메소드
  private void setCloseAndDeleteButton(final ArticleDetailActivity activity, final ArticleDetail articleDetail) {
    // 닫기 버튼. 액티비티를 종료한다
    Button closeButton = (Button) activity.findViewById(R.id.article_read_close);
    closeButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        activity.scrollToFinishActivity();
      }
    });

    // 삭제 버튼. 글 삭제 여부 확인 다이얼로그를 호출한다.
    final ImageView deleteButton = (ImageView) activity.findViewById(R.id.article_read_delete);
      // 삭제버튼 핸들링
    deleteButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        ArticleDeleteDialogFragment alert = ArticleDeleteDialogFragment.newInstance(articleDetail, activity, currentData);
        alert.show(activity.getFragmentManager(), "alertDialog");
      }
          });

    // 수정 버튼.
    final ImageView modifyButton = (ImageView) activity.findViewById(R.id.article_read_modify);
    if(articleDetail.getModifyUrl() == null || articleDetail.getModifyUrl().isEmpty()) {
      modifyButton.setVisibility(View.GONE);
    } else {
      // 수정버튼 핸들링
      modifyButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          ServiceProvider.getInstance()
                  .openArticleModify(activity, articleDetail, currentData);
        }
      });
    }

  }

  // 댓글 작성 버튼, 디시콘 버튼 핸들링 메소드
  private void setCommentSubmitAndDcconButton(final ArticleDetailActivity activity,
                                              final ArticleDetail articleDetail,
                                              final String articleUrl) {

    // 댓글작성 버튼을 누르면 댓글을 추가한다.
    final ImageButton commentSubmit = (ImageButton) activity.findViewById(R.id.article_detail_comment_submit_btn);
    commentSubmit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String comment = presenter.commentText.getText().toString();
        if(!comment.trim().isEmpty()) {
          ServiceProvider.getInstance().writeComment(articleDetail, presenter, articleUrl, comment,
                                                      commentSubmit, articleDetailActivity);
          clearCommentText();
          MainUIThread.hideKeyboard(view);
        }else{
          MainUIThread.showToast(activity, activity.getString(R.string.comment_submit_length));
        }
      }
    });

    // 댓글 작성창에서 엔터를 눌러도 댓글 추가
    presenter.commentText.setOnKeyListener(new View.OnKeyListener() {
      @Override
      public boolean onKey(View view, int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN) {
          switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
              commentSubmit.performClick();
              return true;

            /*case 59: // SHIFT
              if(presenter.commentText.length() == 0) {
                articleDetailActivity.focusScrollView();
                MainUIThread.hideKeyboard(view);
              }
              return true;*/
          }
        }

        return false;
      }
    });


    // 디시콘 버튼 핸들링.
    // 보이고 있으면 안보이게, 안보이고 있으면 보이게한다.
    final ImageButton dcconMenuButton = (ImageButton) activity.findViewById(R.id.article_detail_dccon_open_btn);
    dcconMenuButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
//        clearCommentText();
        presenter.commentText.requestFocus();
        if (activity.isVisibleDcconlayout()) activity.hideDcconLayout();
        else activity.showDcconLayout();
        MainUIThread.hideKeyboard(view);
      }
    });

  }
}

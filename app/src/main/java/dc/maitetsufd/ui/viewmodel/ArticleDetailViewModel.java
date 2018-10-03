package dc.maitetsufd.ui.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.util.Linkify;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.*;
import dc.maitetsufd.R;
import dc.maitetsufd.data.CurrentData;
import dc.maitetsufd.data.CurrentDataManager;
import dc.maitetsufd.models.ArticleDetail;
import dc.maitetsufd.models.Comment;
import dc.maitetsufd.models.DcConPackage;
import dc.maitetsufd.service.ServiceProvider;
import dc.maitetsufd.ui.ArticleDetailActivity;
import dc.maitetsufd.ui.apperance.ArticleDetailStaticApperance;
import dc.maitetsufd.ui.fragment.CommentDeleteDialogFragment;
import dc.maitetsufd.ui.listener.FilterUserLongClickListener;
import dc.maitetsufd.ui.listener.ImageViewerListener;
import dc.maitetsufd.utils.*;

import java.util.*;

/**
 * @since 2017-04-23
 */
public class ArticleDetailViewModel {
  private ArticleDetailActivity articleDetailActivity;
  private ArticleDetail articleDetail;
  private String articleUrl;
  private Resources res;
  private CurrentData currentData;
  private LinearLayout commentLayout;
  private ArticleDetailViewModel viewModel;
  public EditText commentText;
  public ImageView deleteButton;
  private SparseArray<byte[]> imageBytes;
  private List<ImageView> prevBtns;
  private List<ImageView> imageViews;
  private boolean isImageCheck = false;
  private static final int CONTENT_LOAD_IMAGE_COUNT = 15;

  public ArticleDetailViewModel(ArticleDetailActivity articleDetailActivity,
                                ArticleDetail articleDetail, String articleUrl) {
    this.articleDetailActivity = articleDetailActivity;
    this.articleDetail = articleDetail;
    this.articleUrl = articleUrl;
    this.res = articleDetailActivity.getResources();
    this.deleteButton = (ImageView) articleDetailActivity.findViewById(R.id.article_read_delete);
    this.commentText = (EditText) articleDetailActivity.findViewById(R.id.article_detail_comment);
    this.viewModel = this;
    this.currentData = CurrentDataManager.getInstance(articleDetailActivity);
    this.imageBytes = new SparseArray<>();
    this.prevBtns = Collections.synchronizedList(new ArrayList<ImageView>());
    this.imageViews = new ArrayList<>();
    this.isImageCheck = currentData.isImageCheck();

    setAllImageViewButton(articleDetailActivity);
    setMyDcconList(articleDetailActivity, currentData);
    new ArticleDetailStaticApperance(articleDetailActivity, this, articleDetail, articleUrl, currentData).invoke();
    setContent(articleDetailActivity, 0);

    clearComments();
    addComments(articleDetailActivity, this, articleDetail.getComments());
  }

  private void setContent(final Activity activity, final int start) {
    final LinearLayout contentLayout = (LinearLayout) activity.findViewById(R.id.article_read_image_layout);
    final LinearLayout.LayoutParams webViewParams
            = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            (int) (articleDetailActivity.getWindowManager().getDefaultDisplay().getWidth() * 2.2 / 3));

    int dp15 = DipUtils.getDp(res, 15);
    int dp5 = DipUtils.getDp(res, 5);
    int imagePosition = 0;
    int counter = 0;
    contentLayout.removeAllViews();
    final ScrollView scrollView = (ScrollView) activity.findViewById(R.id.article_detail_scroll);
    scrollView.setScrollY(0);

    for (int i = 0; i + start < articleDetail.getContentDataList().size(); i++) {

      final ArticleDetail.ContentData data = articleDetail.getContentDataList().get(i + start);

      // 내용 추가
      if (!data.getText().toString().isEmpty()) {
        addTextContent(activity, contentLayout, dp15, dp5, data);
      } else if (!data.getImageUrl().isEmpty()) {
        counter++;
        addImageContent(activity, imagePosition++, contentLayout, data);
      } else if (!data.getEmbedUrl().isEmpty()
              && !currentData.isMovieIgnore()) {
        counter++;
        addWebViewContent(activity, contentLayout, webViewParams, data);
      }

      // 이미지가 30개 이상이면 글 내용을 나누어 표시한다.
      if (counter > CONTENT_LOAD_IMAGE_COUNT && currentData.isSplitLoad()) {
        final int nextStart = i + start;
        ThreadPoolManager.getContentEc().submit(new Runnable() {
          @Override
          public void run() {
            LinearLayout.LayoutParams btnLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            btnLayout.setMargins(DipUtils.getDp(res, 40), DipUtils.getDp(res, 20),
                    DipUtils.getDp(res, 40), DipUtils.getDp(res, 20));

            final Button continueBtn = new Button(activity);
            continueBtn.setLayoutParams(btnLayout);
            continueBtn.setGravity(Gravity.CENTER);
            continueBtn.setText(res.getString(R.string.continue_article_load));
            continueBtn.setFocusable(false);
            ButtonUtils.setBtnTheme(activity, currentData, continueBtn);
            continueBtn.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                for (ImageView imageView : imageViews) {
                  imageView.setImageBitmap(null);
                }
                imageViews.clear();
                imageBytes.clear();
                setContent(activity, nextStart);
              }
            });
            activity.runOnUiThread(new Runnable() {
              @Override
              public void run() {
                contentLayout.addView(continueBtn, contentLayout.getChildCount());
                scrollView.requestFocus();
              }
            });
          }
        });
        break;
      }

    }

    // 이미지 전체보기 체크
    ThreadPoolManager.getContentEc().submit(new Runnable() {
      @Override
      public void run() {
        checkAllImageViewButton(articleDetailActivity);
      }
    });
  }

  // 웹뷰가 필요한 영상 내용 추가

  private void addWebViewContent(final Activity activity,
                                 final LinearLayout contentLayout,
                                 final LinearLayout.LayoutParams webViewParams,
                                 final ArticleDetail.ContentData data) {

    ThreadPoolManager.getContentEc().submit(new Runnable() { // 단순히 처리 순서를 위해 쓰레드 사용함
      @Override
      public void run() {
        activity.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            final WebView webView = ContentWebView.get(articleDetailActivity, data.getEmbedUrl());
            webView.setLayoutParams(webViewParams);
            contentLayout.addView(webView);
          }
        });
      }
    });
  }

  // 텍스트 처리
  private void addTextContent(final Activity activity,
                              final LinearLayout contentLayout,
                              final int dp15,
                              final int dp5,
                              final ArticleDetail.ContentData data) {

    ThreadPoolManager.getContentEc().submit(new Runnable() {
      @Override
      public void run() {
        final TextView textView = new TextView(activity);
        textView.setAutoLinkMask(Linkify.WEB_URLS);
        if (Build.VERSION.SDK_INT > 18) {
          textView.setTextIsSelectable(true);
        }
        textView.setTextAppearance(activity, R.style.List_TitleText);
        SpannableStringBuilder builder = KeywordUtils.getBuilder(TextUtils.replaceHTMLText(data.getText().toString()),
                currentData.getSearchWord(),
                null);
        textView.setText(builder, TextView.BufferType.SPANNABLE);
        textView.setPadding(dp15, dp5, dp15, dp5);
        textView.setLineSpacing(dp5, 1.0f);

        String textMsg = textView.getText().toString();
        String linkUrl = data.getLinkUrl();
        if (!linkUrl.isEmpty()
                && !textMsg.contains(linkUrl)) {
          textView.setText(textMsg + " : " + linkUrl);
        }

        activity.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            contentLayout.addView(textView);
          }
        });
      }
    });

  }


  // 이미지 처리
  private void addImageContent(final Activity activity,
                               final int imagePosition,
                               final LinearLayout layout,
                               final ArticleDetail.ContentData data) {

    ThreadPoolManager.getContentEc().submit(new Runnable() {
      @Override
      public void run() {

        final LayoutInflater inflater = (LayoutInflater) layout.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final String imageUrl = data.getImageUrl();

        final View view = inflater.inflate(R.layout.article_image_item, null);
        view.setDuplicateParentStateEnabled(true);

        final ImageView realImageView = (ImageView) view.findViewById(R.id.article_item_real_img);
        imageViews.add(realImageView);
        realImageView.setDuplicateParentStateEnabled(true);

        imageBytes.put(imagePosition, null); // 이미지 갯수 설정

        // 이미지 뷰어 클릭 리스너
        realImageView.setOnClickListener(ImageViewerListener.get(activity,
                                        articleDetail.getCommentWriteData().getNo(),
                                        imagePosition, imageBytes, false));

        final ImageView prevImage = (ImageView) view.findViewById(R.id.article_item_prev_img);
        prevImage.setDuplicateParentStateEnabled(true);
        if (isImageCheck) { // 이미지 바로보지않기 체크가 되어있으면
          prevBtns.add(prevImage);
          prevImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              prevBtns.remove(prevImage);
              checkAllImageViewButton(articleDetailActivity);
              prevImage.setVisibility(View.GONE);
              realImageView.setVisibility(View.VISIBLE);
              ContentUtils.loadBitmapFromUrl(articleDetailActivity, imagePosition, imageBytes, imageUrl, articleDetail.getUrl(), realImageView, currentData);
            }
          });
        } else {
          prevImage.setVisibility(View.GONE);
          realImageView.setVisibility(View.VISIBLE);
          ContentUtils.loadBitmapFromUrl(articleDetailActivity, imagePosition, imageBytes, imageUrl, articleDetail.getUrl(), realImageView, currentData);
        }

        activity.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            layout.addView(view);
          }
        });
      }
    });

  }

  // 이미지 전체 보기 버튼 체크
  private void checkAllImageViewButton(final Activity activity) {
    activity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        final ImageView allImageBtn = (ImageView) activity.findViewById(R.id.article_read_image_all);
        if (prevBtns.size() > 1 && isImageCheck) {
          isImageCheck = false;
          allImageBtn.setVisibility(View.VISIBLE);
        } else
          allImageBtn.setVisibility(View.GONE);

      }
    });
  }

  // 이미지 전체 보기 버튼 핸들링
  private void setAllImageViewButton(Activity activity) {
    final ImageView allImageBtn = (ImageView) activity.findViewById(R.id.article_read_image_all);
    allImageBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        int count = prevBtns.size();
        for (int i = 0; i < count; i++) {
          prevBtns.get(0).performClick();
        }
      }
    });
  }


  // 디시콘 카테고리, 디시콘 버튼 핸들링을 시작하는 메소드
  private void setMyDcconList(final ArticleDetailActivity activity, CurrentData currentData) {
    final LinearLayout categoryLayout = (LinearLayout) activity.findViewById(R.id.article_detail_dccon_category);
    final GridLayout gridLayout = (GridLayout) activity.findViewById(R.id.article_detail_dccon_grid_layout);
    final int dp60 = DipUtils.getDp(res, 60);

    for (int i = 0; i < currentData.getDcConPackages().size(); i++) {
      final DcConPackage dcConPackage = currentData.getDcConPackages().get(i);
      // 카테고리버튼
      final ImageButton categoryButton = createDcconCategoryButton(activity, dcConPackage);
      onCategoryButtonClicked(activity, gridLayout, dcConPackage, categoryButton, dp60);
      categoryLayout.addView(categoryButton, DipUtils.getDp(res, 80), DipUtils.getDp(res, 40));
      if (i == 0) categoryButton.performClick();
    }
  }

  // 디시콘 카테고리 버튼 핸들링 메소드
  private void onCategoryButtonClicked(final ArticleDetailActivity activity, final GridLayout gridLayout,
                                       final DcConPackage dcConPackage, final ImageButton categoryButton,
                                       final int dp60) {

    categoryButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        gridLayout.removeAllViews();
        gridLayout.scrollTo(0, 0);
        gridLayout.invalidate();
        for (final DcConPackage.DcCon dcCon : dcConPackage.getDcCons()) {
          ImageView dcconBtn = createDcconButton(activity, gridLayout, dcCon);
          gridLayout.addView(dcconBtn, dp60, dp60);
        }
      }
    });
  }

  // 디시콘 카테고리 버튼을 만드는 메소드
  private ImageButton createDcconCategoryButton(Activity activity, DcConPackage dcConPackage) {
    ImageButton dcconCategoryBtn = new ImageButton(activity);
    ContentUtils.loadBitmapFromUrlWithLocalCheck(activity, dcConPackage.getDccon_package_src(), dcconCategoryBtn, currentData);
    dcconCategoryBtn.setBackgroundColor(Color.TRANSPARENT);
    dcconCategoryBtn.setAdjustViewBounds(true);
    dcconCategoryBtn.setScaleType(ImageView.ScaleType.FIT_CENTER);
    int dp4 = DipUtils.getDp(res, 4);
    dcconCategoryBtn.setPadding(dp4, dp4, dp4, dp4);
    return dcconCategoryBtn;
  }

  // 누르면 댓글달리는 디시콘 버튼을 만드는 메소드
  private ImageView createDcconButton(final ArticleDetailActivity activity,
                                      final GridLayout blockedLayout,
                                      final DcConPackage.DcCon dcCon) {
    ImageView btn = new ImageView(activity);
    ContentUtils.loadBitmapFromUrlWithLocalCheck(activity, dcCon.getDccon_src(), btn, currentData);
    btn.setAdjustViewBounds(true);
    btn.setScaleType(ImageView.ScaleType.FIT_CENTER);
    btn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        activity.hideDcconLayout();
        ServiceProvider.getInstance().writeDcconComment(activity, viewModel, blockedLayout,
                articleDetail, articleUrl, dcCon);
      }
    });
    return btn;
  }

  // 글에 달린 댓글들을 모두 삭제하는 메소드
  private void clearComments() {
    commentLayout = (LinearLayout) articleDetailActivity.findViewById(R.id.article_read_comment_layout);
    commentLayout.removeAllViews();
  }


  // 글에 달린 댓글들의 뷰를 만들어 추가하는 메소드
  public void addComments(final Activity activity, ArticleDetailViewModel viewModel, List<Comment> comments) {
    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);

    LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
    int dp5 = DipUtils.getDp(activity.getResources(), 5);
    lineParams.setMargins(dp5, 0, dp5, 0);


    commentLayout = (LinearLayout) articleDetailActivity.findViewById(R.id.article_read_comment_layout);

    for (Comment comment : comments)
      addCommentView(activity, viewModel, layoutParams, lineParams, commentLayout, comment);

    // 댓글 새로고침 버튼
    ThreadPoolManager.getContentEc().submit(new Runnable() {
      @Override
      public void run() {
        final Button btn = getRefreshButton(articleDetailActivity);
        activity.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            commentLayout.addView(btn);
          }
        });

      }
    });
  }

  // 댓글 새로고침 버튼을 만드는 메소드
  private Button getRefreshButton(final ArticleDetailActivity activity) {
    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
    layoutParams.setMargins(DipUtils.getDp(res, 40), DipUtils.getDp(res, 20),
            DipUtils.getDp(res, 40), DipUtils.getDp(res, 30));
    Button refreshBtn = new Button(articleDetailActivity);
    refreshBtn.setLayoutParams(layoutParams);
    int dp10 = DipUtils.getDp(res, 10);
    refreshBtn.setPadding(dp10, dp10, dp10, dp10);
    refreshBtn.setGravity(Gravity.CENTER);
    refreshBtn.setText(res.getString(R.string.comment_refresh_button));
    refreshBtn.setFocusable(false);
    ButtonUtils.setBtnTheme(activity, currentData, refreshBtn);

    refreshBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        commentText.requestFocus();
        ServiceProvider.getInstance().refreshComment(activity, viewModel, articleUrl);
        MainUIThread.showSnackBar(view, res.getString(R.string.comment_refresh_msg));
      }
    });

    return refreshBtn;
  }

  // 댓글 하나에 해당하는 뷰를 만들고 추가하는 메소드
  private void addCommentView(final Activity activity,
                              final ArticleDetailViewModel viewModel,
                              final LinearLayout.LayoutParams layoutParams,
                              final LinearLayout.LayoutParams lineParams,
                              final LinearLayout commentLayout,
                              final Comment comment) {

    ThreadPoolManager.getContentEc().submit(new Runnable() {
      @Override
      public void run() {
        LayoutInflater inflater = (LayoutInflater) commentLayout.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.comment_item, null);
        // 닉네임
        TextView nickName = (TextView) view.findViewById(R.id.comment_item_nickname);
        nickName.setText(comment.getUserInfo().getNickname());
        // 유저 타입
        ImageView userType = (ImageView) view.findViewById(R.id.comment_item_user_type);
        UserTypeManager.set(res, comment.getUserInfo(), userType);
        // 아이피
        TextView ip = (TextView) view.findViewById(R.id.comment_item_ip);
        ip.setText(comment.getIp());
        //  작성시간
        TextView date = (TextView) view.findViewById(R.id.comment_item_date);
        date.setText(comment.getDate());

        // 댓글 삭제 경고 메시지
        ImageView commentDeleteButton = (ImageView) view.findViewById(R.id.comment_item_delete);
        if (comment.getDeleteCode().length() > 0) {
          commentDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              CommentDeleteDialogFragment alert = CommentDeleteDialogFragment
                      .newInstance(articleDetailActivity, viewModel,
                              currentData,
                              commentLayout,
                              comment.getDeleteCode(), articleDetail, articleUrl);
              alert.show(articleDetailActivity.getFragmentManager(), "commentDeleteDialog");
            }
          });
        } else // 댓글 삭제가 불가능하면 삭제버튼을 안보이게 해둠.
          commentDeleteButton.setVisibility(View.GONE);

        // 댓글 내용
        TextView content = (TextView) view.findViewById(R.id.comment_item_content);
        content.setText(comment.getContent());
        if (Build.VERSION.SDK_INT > 18) {
          content.setTextIsSelectable(true);
        }

        // 디시콘 댓글일 때 처리
        if (comment.getImgUrl().length() > 0) {
          content.setVisibility(View.GONE);
          setDcconComment(comment, view);
        }

        // 뷰를 꾹 누르면 뜨는 유저 차단창 핸들링
        view.setOnLongClickListener(FilterUserLongClickListener.get(articleDetailActivity, comment.getUserInfo()));

        // ScrollView 이벤트 처리
        view.setDuplicateParentStateEnabled(true);

        // 하단 댓글 구분선
        final View yellowLine = inflater.inflate(R.layout.yellow_separator_line, null);

        // 대댓글이면 표시
        if (comment.isAddComment()) {
          ImageView commentIsAdd = (ImageView) view.findViewById(R.id.comment_is_add);
          commentIsAdd.setVisibility(View.VISIBLE);
        }

        activity.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            commentLayout.addView(view, layoutParams);
            commentLayout.addView(yellowLine, lineParams);

          }
        });
      }
    });


  }

  // 댓글이 디시콘일때 이미지 처리
  private void setDcconComment(final Comment comment, View view) {

    final ImageView dcconImg = (ImageView) view.findViewById(R.id.comment_item_dccon);
    final ImageButton dcconPrevImg = (ImageButton) view.findViewById(R.id.comment_item_dccon_prev);

    // 디시콘을 바로보지 않을 때
    if (currentData.isDcconCheck()) {
      dcconPrevImg.setVisibility(View.VISIBLE);
      dcconPrevImg.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          commentText.requestFocus();
          ContentUtils.loadBitmapFromUrlWithLocalCheck(articleDetailActivity, comment.getImgUrl(), dcconImg, currentData);
          dcconPrevImg.setVisibility(View.GONE);
          dcconImg.setVisibility(View.VISIBLE);
        }
      });
    } else {
      ContentUtils.loadBitmapFromUrlWithLocalCheck(articleDetailActivity, comment.getImgUrl(), dcconImg, currentData);
      dcconImg.setVisibility(View.VISIBLE);
    }

  }

  public void clearImageBytes() {
    imageBytes.clear();
  }

  public int getCommentLayoutCount() {
    return commentLayout.getChildCount();
  }

  public void removeCommentLayoutFirst() {
    commentLayout.removeViewAt(0);
  }

}

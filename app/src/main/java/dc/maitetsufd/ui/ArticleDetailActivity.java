package dc.maitetsufd.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import butterknife.BindView;
import butterknife.ButterKnife;
import dc.maitetsufd.R;
import dc.maitetsufd.data.CurrentData;
import dc.maitetsufd.data.CurrentDataManager;
import dc.maitetsufd.data.ImageData;
import dc.maitetsufd.enums.RequestCodes;
import dc.maitetsufd.enums.ResultCodes;
import dc.maitetsufd.models.ArticleDetail;
import dc.maitetsufd.service.ServiceProvider;
import dc.maitetsufd.ui.viewmodel.ArticleDetailViewModel;
import dc.maitetsufd.utils.MainUIThread;
import dc.maitetsufd.utils.ShortcutKeyEvent;
import dc.maitetsufd.utils.ThreadPoolManager;
import dc.maitetsufd.utils.VibrateUtils;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * 글 상세보기 (내용) 액티비티.
 */
public class ArticleDetailActivity extends SwipeBackActivity {
  public ArticleDetailViewModel articleDetailViewModel;
  private SwipeBackLayout swipeBackLayout;
  private Context context;
  private ArticleDetail articleDetail;
  @BindView(R.id.article_detail_dccon_layout) LinearLayout dcconLayout;
  @BindView(R.id.article_detail_scroll)  ScrollView scrollView;
  private boolean isModifyDestroy = false;
  private int scrollY = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.context = this.getApplicationContext();
    CurrentData currentData = CurrentDataManager.getInstance(this);
    setThemeColor(currentData);
    setContentView(R.layout.activity_article_detail);
    ButterKnife.bind(this);

    this.articleDetail = (ArticleDetail) getIntent().getSerializableExtra("articleDetail");
    final String articleUrl = getIntent().getStringExtra("articleUrl");
    articleDetailViewModel = new ArticleDetailViewModel(this, articleDetail, articleUrl);
    this.swipeBackLayout = getSwipeBackLayout();
    setSwipeListener(this);
  }

  private void setThemeColor(CurrentData currentData) {
      if(currentData.isDarkTheme()) {
        setTheme(R.style.DarkTheme);
        if (Build.VERSION.SDK_INT >= 21) {
          getWindow().setStatusBarColor(
                  ContextCompat.getColor(this, R.color.darkThemeLightBackground));
        }
      }
  }


  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if(requestCode == RequestCodes.ARTICLE.ordinal()) {
      if(resultCode == ResultCodes.ARTICLE_REFRESH.ordinal()) {
        this.isModifyDestroy = true;
        ServiceProvider.getInstance()
                .refreshArticleDetail(this, articleDetail.getUrl());
      }
    }

  }

  // 게시물 닫기 스와이프 리스너
  private void setSwipeListener(final Activity activity) {

    swipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT | SwipeBackLayout.EDGE_RIGHT);
    swipeBackLayout.addSwipeListener(new SwipeBackLayout.SwipeListener() {
      @Override
      public void onScrollStateChange(int state, float scrollPercent) {}
      @Override
      public void onEdgeTouch(int edgeFlag) {
        if(CurrentDataManager.getInstance(activity)
                .isArticleCloseVib())
        VibrateUtils.call(context, VibrateUtils.VIBRATE_DURATION_SHORT); }
      @Override
      public void onScrollOverThreshold() {
        if(CurrentDataManager.getInstance(activity)
                .isArticleCloseVib())
        VibrateUtils.call(context, VibrateUtils.VIBRATE_DURATION); }
    });
  }

  @Override
  public void onBackPressed() {
    if(!isVisibleDcconlayout()){
      scrollToFinishActivity();
    }
    else hideDcconLayout();
  }

  public boolean isVisibleDcconlayout(){
    return dcconLayout.getVisibility() != View.GONE;
  }

  public void hideDcconLayout(){
    dcconLayout.setVisibility(View.GONE);
  }
  public void showDcconLayout(){ dcconLayout.setVisibility(View.VISIBLE); }


  @Override
  protected void onPause() {
    super.onPause();
    scrollView.computeScroll();
    scrollY = scrollView.getScrollY();
  }

  @Override
  protected void onResume() {
    super.onResume();
    scrollView.computeScroll();
    scrollView.setScrollY(scrollY);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if(!isModifyDestroy) {
      ThreadPoolManager.shutdownAllEc();
      articleDetailViewModel.clearImageBytes();
      ImageData.clearHoldImageBytes();
    }
  }

  @Override
  public boolean dispatchKeyEvent(KeyEvent event) {
    boolean isDoneEvent = ShortcutKeyEvent.computeArticleDetailKeyEvent(this, scrollView, event);
    if(isDoneEvent) return false;
    else return super.dispatchKeyEvent(event);
  }

  public void focusCommentText() {
    articleDetailViewModel.commentText.requestFocus();
    MainUIThread.showKeyboard(this.getCurrentFocus());
  }

  public boolean isFocusedCommentText(){
    return articleDetailViewModel.commentText.hasFocus();
  }

  public void focusScrollView(){
    scrollView.requestFocus();
  }
}

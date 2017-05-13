package dc.maitetsu.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import butterknife.BindView;
import butterknife.ButterKnife;
import dc.maitetsu.R;
import dc.maitetsu.data.CurrentData;
import dc.maitetsu.data.CurrentDataManager;
import dc.maitetsu.data.ImageData;
import dc.maitetsu.enums.RequestCodes;
import dc.maitetsu.enums.ResultCodes;
import dc.maitetsu.models.ArticleDetail;
import dc.maitetsu.service.ServiceProvider;
import dc.maitetsu.ui.viewmodel.ArticleDetailViewModel;
import dc.maitetsu.utils.ThreadPoolManager;
import dc.maitetsu.utils.VibrateUtils;
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
    String articleUrl = getIntent().getStringExtra("articleUrl");
    this.articleDetailViewModel = new ArticleDetailViewModel(this, articleDetail, articleUrl);
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
  protected void onDestroy() {
    super.onDestroy();
    ThreadPoolManager.shutdownContentEc();
    ImageData.clearHoldImageBytes();
  }

}

package dc.maitetsu.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dc.maitetsu.R;
import dc.maitetsu.data.CurrentData;
import dc.maitetsu.data.CurrentDataManager;
import dc.maitetsu.data.ImageData;
import dc.maitetsu.models.MangaSimpleModel;
import dc.maitetsu.service.MaruServiceProvider;
import dc.maitetsu.utils.ThreadPoolManager;

import java.util.ArrayList;
import java.util.List;

public class MaruViewerDetailActivity extends AppCompatActivity {
  private List<ImageView> imageViews = new ArrayList<>();
  @BindView(R.id.maru_detail_view_layout) LinearLayout layout;
  @BindView(R.id.maru_detail_view_title) TextView pageTitle;
  @BindView(R.id.maru_detail_scroll) ScrollView scrollView;
  private int scrollY = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    MangaSimpleModel model = (MangaSimpleModel) getIntent()
                            .getSerializableExtra("simpleData");
    CurrentData currentData = CurrentDataManager.getInstance(this);
    setTheme(currentData);
    setContentView(R.layout.activity_maru_detail_view);
    ButterKnife.bind(this);
    pageTitle.setText(model.getTitle());
    MaruServiceProvider.getInstance().addMaruImages(model.getNo(),
                            this, currentData, scrollView, layout, imageViews, model.isViewerModel());
  }


  // 테마 설정
  private void setTheme(CurrentData currentData) {
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    if (currentData.isDarkTheme()) {
      setTheme(R.style.DarkTheme);
      if (Build.VERSION.SDK_INT >= 21) {
        getWindow().setStatusBarColor(
                ContextCompat.getColor(this, R.color.darkThemeLightBackground));
      }
    }
  }


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
    ThreadPoolManager.shutdownServiceEc();
    ImageData.clearHoldImageBytes();
  }

  // 닫기 버튼
  @OnClick(R.id.maru_detail_close)
  void finishButton(){
    finish();
  }

}

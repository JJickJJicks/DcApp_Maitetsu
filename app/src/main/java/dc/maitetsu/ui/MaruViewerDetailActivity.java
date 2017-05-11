package dc.maitetsu.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dc.maitetsu.R;
import dc.maitetsu.data.CurrentData;
import dc.maitetsu.data.CurrentDataManager;
import dc.maitetsu.models.MaruSimpleModel;
import dc.maitetsu.service.MaruServiceProvider;

import java.util.ArrayList;
import java.util.List;

public class MaruViewerDetailActivity extends AppCompatActivity {
  private List<ImageView> imageViews = new ArrayList<>();
  @BindView(R.id.maru_detail_view_layout) LinearLayout layout;
  @BindColor(R.color.darkThemeLightBackground) int darkThemeLightBackground;
  @BindView(R.id.maru_detail_view_title) TextView pageTitle;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    MaruSimpleModel model = (MaruSimpleModel) getIntent()
                            .getSerializableExtra("data");
    CurrentData currentData = CurrentDataManager.getInstance(this);
    setTheme(currentData);
    setContentView(R.layout.activity_maru_detail_view);
    ButterKnife.bind(this);
    pageTitle.setText(model.getTitle());

    MaruServiceProvider.getInstance().addMaruImages(model.getNo(),
                            this, currentData, layout, imageViews);
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


  // 닫기 버튼
  @OnClick(R.id.maru_detail_close)
  public void finishButton(){
    finish();
  }

}

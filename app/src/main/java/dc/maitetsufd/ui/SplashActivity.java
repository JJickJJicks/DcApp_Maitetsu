package dc.maitetsufd.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import dc.maitetsufd.R;
import dc.maitetsufd.data.CurrentData;
import dc.maitetsufd.data.CurrentDataManager;
import dc.maitetsufd.service.ServiceProvider;
import dc.maitetsufd.utils.ButtonUtils;

/**
 * @since 2017-04-23
 *
 * 가장 먼저 보여지는 스플래시 액티비티
 *
 */
public class SplashActivity extends AppCompatActivity {
  private long backKeyPressed = 0L;
  @BindView(R.id.splash_text) TextView splashTextView;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    CurrentData currentData = CurrentDataManager.getInstance(this);
    setTheme(currentData);
    setContentView(R.layout.activity_splash);

    ButterKnife.bind(this);
    hideWindowStatusBar();

    // 로그인
    ServiceProvider.getInstance()
            .login(this,
            getIntent().getBooleanExtra("resetMode", false));
  }

  // 테마 설정 메소드
  private void setTheme(CurrentData currentData) {
    if(currentData.isDarkTheme()) {
      setTheme(R.style.DarkTheme);
    }
  }

  // 상단 스테이터스 바 제거
  private void hideWindowStatusBar() {
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
  }

  /**
   * 스플래시 액티비티의 메시지를 설정하는 메소드
   */
  public void setSplashText(String msg) {
      this.splashTextView.setText(msg);
  }


  @Override
  public void onBackPressed() {
    backKeyPressed = ButtonUtils.backButtnFinish(this, backKeyPressed);
  }


}

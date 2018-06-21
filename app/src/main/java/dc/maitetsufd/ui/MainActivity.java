package dc.maitetsufd.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dc.maitetsufd.R;
import dc.maitetsufd.data.CurrentData;
import dc.maitetsufd.data.CurrentDataManager;
import dc.maitetsufd.enums.RequestCodes;
import dc.maitetsufd.enums.ResultCodes;
import dc.maitetsufd.ui.adapter.MyPagerAdapter;
import dc.maitetsufd.ui.viewmodel.TabLayoutViewModel;
import dc.maitetsufd.utils.ButtonUtils;
import dc.maitetsufd.utils.MainUIThread;
import dc.maitetsufd.utils.SelectViewPage;


/**
 * 앱의 메인 액티비티.
 * <p>
 * 툴바와 페이저를 만들고 설정한다.
 */
public class MainActivity extends AppCompatActivity {
  private long backKeyPressed = 0L;

  @BindView(R.id.tabs)
  TabLayout tabLayout;
  @BindView(R.id.view_pager)
  ViewPager viewPager;
  @BindView(R.id.toolbar_title)
  TextView toolbarTitle;
  @BindView(R.id.toolbar_search_edit)
  EditText searchEdit;
  @BindView(R.id.toolbar_search_close)
  ImageView searchClose;
  @BindView(R.id.toolbar_search_btn)
  ImageView searchOpen;
  @BindColor(R.color.colorWhite)
  int whiteColor;
  private static boolean isLoaded = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    CurrentData currentData = CurrentDataManager.load(this);
    setTheme(currentData);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    if (!isLoaded) { // 멀티 윈도우 모드에서 비정상적인 스플래시 호출을 방지
      callSplashActivity();
      isLoaded = true;
    }

    // 페이저와 탭 설정
    setupPagerAdapter(currentData);
    setSearchToolsColor();
  }

  private void setupPagerAdapter(CurrentData currentData) {

    MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
    viewPager.setAdapter(myPagerAdapter);
    viewPager.setOffscreenPageLimit(5);
    tabLayout.setupWithViewPager(viewPager);
    tabLayout.setTabMode(TabLayout.MODE_FIXED);
    tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

    // 만화 뷰어 기능 비활성화 처리
    if (!currentData.isMaruViewer()) {
      removeTab(myPagerAdapter, 3);
    }

    // 탭 모델뷰 설정
    TabLayoutViewModel.invoke(this, myPagerAdapter);

  }


  // 검색바 색상
  private void setSearchToolsColor() {
    searchOpen.setColorFilter(whiteColor);
    searchClose.setColorFilter(whiteColor);
  }

  // 비활성화 탭 제거
  private void removeTab(MyPagerAdapter myPagerAdapter, int position) {
    viewPager.setAdapter(null);
    myPagerAdapter.removeItem(position);
    viewPager.setAdapter(myPagerAdapter);
  }

  // 스플래시 액티비티를 여는 메소드
  public void callSplashActivity() {
    Intent intent = new Intent(this, SplashActivity.class);
    startActivityForResult(intent, RequestCodes.LOGIN.ordinal());
  }


  // 검색바 닫기 버튼
  @OnClick(R.id.toolbar_search_close)
  void searchCloseClick(View view) {
    searchClose.setVisibility(View.INVISIBLE);
    searchEdit.setText("");
    searchEdit.setVisibility(View.INVISIBLE);
    MainUIThread.hideKeyboard(view);
  }


  // 테마 설정
  private void setTheme(CurrentData currentData) {
    if (currentData.isDarkTheme()) {
      setTheme(R.style.DarkTheme);
      if (Build.VERSION.SDK_INT >= 21) {
        getWindow().setStatusBarColor(
                ContextCompat.getColor(this, R.color.darkThemeLightBackground));
      }
    }
  }

  @Override
  public void onBackPressed() {
    backKeyPressed = ButtonUtils.backButtnFinish(this, backKeyPressed);
  }


  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == RequestCodes.LOGIN.ordinal()) {
      if (resultCode == ResultCodes.LOGIN_SUCCESS.ordinal())
        SelectViewPage.select(this, 1);
      else if (requestCode == ResultCodes.LOGIN_FAIL.ordinal())
        SelectViewPage.select(this, viewPager.getChildCount() - 1);

    } else if (requestCode == RequestCodes.ARTICLE.ordinal()) {
      if (resultCode == ResultCodes.ARTICLE_REFRESH.ordinal()) {
        SelectViewPage.select(this, 1);
      }
    }
  }

}

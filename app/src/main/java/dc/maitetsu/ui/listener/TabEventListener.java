package dc.maitetsu.ui.listener;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import dc.maitetsu.R;
import dc.maitetsu.data.CurrentData;
import dc.maitetsu.data.CurrentDataManager;
import dc.maitetsu.ui.MainActivity;
import dc.maitetsu.utils.MainUIThread;
import dc.maitetsu.ui.adapter.MyPagerAdapter;
import dc.maitetsu.utils.ThreadPoolManager;

/**
 * @since 2017-04-22
 *
 * 탭바를 눌렀을 때 리스너
 *
 */
public class TabEventListener {
  @BindView(R.id.tabs) TabLayout tabLayout;
  @BindView(R.id.toolbar_title) TextView toolbarTitle;
  @BindView(R.id.toolbar_search_layout) LinearLayoutCompat searchLayout;
  @BindView(R.id.toolbar_search_edit) EditText searchEditText;
  @BindView(R.id.toolbar_search_close) ImageView searchClose;
  @BindView(R.id.toolbar_search_btn) ImageView searchBtn;
  @BindView(R.id.view_pager) ViewPager viewPager;
  @BindColor(R.color.colorWhite) int whiteColor;
  @BindColor(R.color.colorBlack) int blackColor;
  @BindColor(R.color.colorGray) int grayColor;
  @BindColor(R.color.darkThemeGray) int darkThemeGrayColor;
  private MainActivity activity;
  private MyPagerAdapter pagerAdapter;
  private CurrentData currentData;


  public TabEventListener(final MainActivity activity, final MyPagerAdapter pagerAdapter){
    this.activity = activity;
    this.pagerAdapter = pagerAdapter;
    this.currentData = CurrentDataManager.getInstance(activity);
    ButterKnife.bind(this, activity);

    tabLayout.addOnTabSelectedListener(get());

  }


  // 뷰페이저 리스너를 리턴하는 메소드
  private TabLayout.ViewPagerOnTabSelectedListener get() {
    return new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
      @Override
      public void onTabSelected(TabLayout.Tab tab) {
        super.onTabSelected(tab);

        currentData = CurrentDataManager.getInstance(activity);
        currentData.setSearchWord("");

        // 선택된 탭 색상처리
        setTabSelectColorTheme(tab);

        // 탭 상단 타이틀 처리
        String title = activity.getResources().getString((int) tab.getTag());
        toolbarTitle.setText(String.format(title, currentData.getGalleryInfo().getGalleryName()));

        try {// 검색 계속 버튼, 검색 바 숨기기
          // fragment detach시 nullpointException이 발생 할 수 있음.
          pagerAdapter.getSimpleArticleListFragment().getHasAdapterViewModel().hideSearchContinueBtn();
        }catch(Exception e){}

        searchLayout.setVisibility(View.INVISIBLE);

        // 탭 전환시 동작
        setTabAutoRefresh(tab);
      }

      @Override
      public void onTabUnselected(TabLayout.Tab tab) {
        super.onTabUnselected(tab);
        changeTabColor(tab, darkThemeGrayColor, grayColor);
      }
    };
  }

  // 탭 전환시
  private void setTabAutoRefresh(TabLayout.Tab tab) {

    // 이전 작업 셧다운
    ThreadPoolManager.shutdownAllEc();

    if ((int) tab.getTag() == R.string.title_article_list) { // 일반글
      searchLayout.setVisibility(View.VISIBLE);
      searchBtn.setVisibility(View.VISIBLE);
      searchBtn.setOnClickListener(SearchBtnListener.get(pagerAdapter.getSimpleArticleListFragment(),
                                  searchClose, searchBtn, searchEditText));
      MainUIThread.refreshArticleListView(pagerAdapter.getSimpleArticleListFragment(), true);

    }else if((int) tab.getTag() == R.string.maru_viewer_title) { // 실험실
      MainUIThread.refreshMaruListView(pagerAdapter.getDcmysDcMysFragment(), true);

    } else if ((int) tab.getTag() == R.string.title_recommend_article_title_bar) { // 개념글
      MainUIThread.refreshArticleListView(pagerAdapter.getRecommendArticleListFragment(), true);
    }
  }

  // 선택한 탭 색상을 바꾸는 메소드
  private void setTabSelectColorTheme(TabLayout.Tab tab) {
    changeTabColor(tab, whiteColor, blackColor);
  }

  // 탭과 색상을 입력받아 탭의 색상을 변경함
  private void changeTabColor(TabLayout.Tab tab, int darkThemeColor, int basicThemeColor) {
    if (currentData.isDarkTheme()) {
      tab.getIcon().setColorFilter(darkThemeColor, PorterDuff.Mode.SRC_IN);
    } else {
      tab.getIcon().setColorFilter(basicThemeColor, PorterDuff.Mode.SRC_IN);
    }
  }
}
package dc.maitetsufd.ui.viewmodel;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.widget.TextView;
import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import dc.maitetsufd.R;
import dc.maitetsufd.data.CurrentData;
import dc.maitetsufd.data.CurrentDataManager;
import dc.maitetsufd.ui.MainActivity;
import dc.maitetsufd.ui.adapter.MyPagerAdapter;
import dc.maitetsufd.ui.listener.TabEventListener;

/**
 * @since 2017-04-22
 *
 *  탭 레이아웃
 *
 */
public class TabLayoutViewModel {

  private Activity activity;
  private MyPagerAdapter myPagerAdapter;
  @BindView(R.id.tabs) TabLayout tabLayout;
  @BindView(R.id.view_pager) ViewPager viewPager;
  @BindView(R.id.toolbar_title) TextView toolbarTitle;
  @BindColor(R.color.colorWhite) int whiteColor;
  @BindColor(R.color.colorBlack) int blackColor;
  @BindColor(R.color.colorGray) int grayColor;
  @BindColor(R.color.darkThemeGray) int darkThemeGrayColor;
  @BindColor(R.color.darkThemeTabBackground) int darkThemeTabBackground;
  private static TabLayoutViewModel self;

  private TabLayoutViewModel(MainActivity activity, MyPagerAdapter myPagerAdapter) {
    CurrentData currentData = CurrentDataManager.getInstance(activity);
    this.activity = activity;
    this.myPagerAdapter = myPagerAdapter;
    ButterKnife.bind(this, activity);

    setTabLayoutIconAndTag();
    setTabIconColorGray(currentData);
    setTabLayoutColor(currentData);
    setTabAndTitle(0, currentData);

    attachTabEvent(activity, myPagerAdapter);
  }

  public static void invoke(MainActivity activity, MyPagerAdapter myPagerAdapter) {
   self = new TabLayoutViewModel(activity, myPagerAdapter);
  }

  private void attachTabEvent(MainActivity activity, MyPagerAdapter myPagerAdapter) {
    TabEventListener.invoke(activity, myPagerAdapter);
  }


  // 탭의 배경색 설정
  private void setTabLayoutColor(CurrentData currentData) {
    if (currentData.isDarkTheme()) {
      tabLayout.setBackgroundColor(darkThemeTabBackground);
    }
  }

  // 탭 아이콘과 태그를 설정한다
  private void setTabLayoutIconAndTag() {
    int i = 0;
    tabLayout.getTabAt(i++).setIcon(R.drawable.gallery_list).setTag(R.string.title_gallery_list);
    tabLayout.getTabAt(i++).setIcon(R.drawable.article_list_icon).setTag(R.string.title_article_list);
    tabLayout.getTabAt(i++).setIcon(R.drawable.recommand_article_icon).setTag(R.string.title_recommend_article_title_bar);
    if(myPagerAdapter.getCount() > 4)
      tabLayout.getTabAt(i++).setIcon(R.drawable.maru_viewer_icon).setTag(R.string.maru_viewer_title);
    tabLayout.getTabAt(i).setIcon(R.drawable.setting).setTag(R.string.setting);
  }

  // 탭 이벤트 처리
  private void setTabAndTitle(final int i, final CurrentData currentData) {
    activity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        int tabNumb = i; if(tabNumb < 0) { tabNumb = viewPager.getChildCount() + tabNumb; }

        TabLayout.Tab tab = tabLayout.getTabAt(tabNumb);

        String title = activity.getString((int) tab.getTag());
        toolbarTitle.setText(String.format(title, currentData.getGalleryInfo().getGalleryName()));

        // 탭 눌렀을 때 색상
        if (currentData.isDarkTheme()) tab.getIcon().setColorFilter(whiteColor, PorterDuff.Mode.SRC_IN);
        else tab.getIcon().setColorFilter(blackColor, PorterDuff.Mode.SRC_IN);

        viewPager.setCurrentItem(tabNumb);
        tab.select();
      }
    });
  }

  // 탭 기본 색상 처리
  private void setTabIconColorGray(CurrentData currentData) {
    for (int i = 0; i < myPagerAdapter.getCount(); i++) {
      if(currentData.isDarkTheme())
        tabLayout.getTabAt(i).getIcon()
                .setColorFilter(darkThemeGrayColor, PorterDuff.Mode.SRC_IN);
      else
        tabLayout.getTabAt(i).getIcon()
                .setColorFilter(grayColor, PorterDuff.Mode.SRC_IN);
    }
  }

}

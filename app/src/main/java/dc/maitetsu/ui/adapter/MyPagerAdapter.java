package dc.maitetsu.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;
import dc.maitetsu.ui.fragment.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 각 탭의 프래그먼트를 처리하는 메소드
 *
 */
public class MyPagerAdapter extends FragmentPagerAdapter {
  private List<Fragment> fragmentList = new ArrayList<>();
  private SimpleArticleListFragment simpleArticleListFragment;
  private RecommendArticleListFragment recommendArticleListFragment;
  private MaruViewerFragment maruViewerFragment;

  public MyPagerAdapter(FragmentManager fm) {
    super(fm);
    simpleArticleListFragment = SimpleArticleListFragment.newInstance();
    recommendArticleListFragment = RecommendArticleListFragment.newInstance();
    maruViewerFragment = MaruViewerFragment.newInstance();

    fragmentList.add(GalleryListFragment.newInstance());
    fragmentList.add(simpleArticleListFragment);
    fragmentList.add(recommendArticleListFragment);
    fragmentList.add(maruViewerFragment);
    fragmentList.add(SettingFragment.newInstance());

  }

  @Override
  public Fragment getItem(int position) {
    return fragmentList.get(position);
  }

  @Override
  public int getCount() {
    return fragmentList.size();
  }

  @Override
  public CharSequence getPageTitle(int position) {
    return null;
  }

  public void removeItem(int position) {
    fragmentList.remove(position);
    notifyDataSetChanged();
  }

  @Override
  public void destroyItem(ViewGroup container, int position, Object object) {
    container.removeView(fragmentList.get(position).getView());
  }

  public SimpleArticleListFragment getSimpleArticleListFragment(){
    return simpleArticleListFragment;
  }

  public RecommendArticleListFragment getRecommendArticleListFragment(){
    return recommendArticleListFragment;
  }

  public MaruViewerFragment getDcmysDcMysFragment() {
    return maruViewerFragment;
  }

}

package dc.maitetsu.utils;

import android.app.Activity;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import dc.maitetsu.R;

/**
 * @author Park Hyo Jun
 * @since 2017-05-02
 */
public class SelectViewPage {

  public static void select(final Activity activity, final int i) {
    activity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        ViewPager viewPager = (ViewPager) activity.findViewById(R.id.view_pager);
        TabLayout tabLayout = (TabLayout) activity.findViewById(R.id.tabs);

        int tabNumb = i;
        if (tabNumb < 0) {
          tabNumb = viewPager.getChildCount() + tabNumb;
        }

        TabLayout.Tab tab = tabLayout.getTabAt(tabNumb);
        if(tab != null) {
          TabLayout.Tab firstTab = tabLayout.getTabAt(0);
          if(firstTab != null) firstTab.select();
          tab.select();
        }

      }
    });
  }

}

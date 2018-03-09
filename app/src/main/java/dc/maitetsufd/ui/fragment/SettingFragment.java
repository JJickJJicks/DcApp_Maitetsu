package dc.maitetsufd.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.inputmethod.InputMethodManager;
import dc.maitetsufd.R;
import dc.maitetsufd.data.CurrentData;
import dc.maitetsufd.data.CurrentDataManager;
import dc.maitetsufd.ui.FilterUserListActivity;
import dc.maitetsufd.ui.MainActivity;
import dc.maitetsufd.ui.OpenSourceActivity;

/**
 * 설정 프래그먼트.
 */
public class SettingFragment extends PreferenceFragmentCompat {
  public SettingFragment() {}
  private CurrentData currentData;

  public static SettingFragment newInstance() {
    return new SettingFragment();
  }

  @Override
  public void onCreatePreferences(Bundle bundle, String s) {
    currentData = CurrentDataManager.getInstance(this.getContext());
    addPreferencesFromResource(R.xml.fragment_setting);
    setLoginButton(this);
    setOpenSourceButton(this, currentData);
    setFilterUserButton(this);
    setFilterUserListButton(this);
    setDcmysMode(this);
    setVibAndResolutionButtons(this);
    setImageAndDcconTouch(this);
    setMovieIgnore(this);
  }

  private void setMovieIgnore(final SettingFragment fragment) {
    final Preference movieIgnore = fragment.getPreferenceManager()
            .findPreference("movie_ignore");
    movieIgnore.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override
      public boolean onPreferenceClick(Preference preference) {
        currentData = CurrentDataManager.getInstance(fragment.getContext());
        currentData
                .setMovieIgnore(!currentData.isMovieIgnore());
        return false;
      }
    });


  }

  private void setFilterUserButton(final SettingFragment fragment) {
    final Preference telcomFilter = fragment.getPreferenceManager()
            .findPreference("telcom_filter");
    telcomFilter.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override
      public boolean onPreferenceClick(Preference preference) {
        currentData = CurrentDataManager.getInstance(fragment.getContext());
        currentData
                .setTelcomFilter(!currentData.isTelcomFilter());
        return false;
      }
    });

    final Preference flowFilter = fragment.getPreferenceManager()
            .findPreference("flow_filter");
    flowFilter.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override
      public boolean onPreferenceClick(Preference preference) {
        currentData = CurrentDataManager.getInstance(fragment.getContext());
        currentData
                .setFlowFilter(!currentData.isFlowFilter());
        return false;
      }
    });




  }

  private void setImageAndDcconTouch(final SettingFragment fragment) {
    final Preference imageCheck = fragment.getPreferenceManager()
            .findPreference("image_check");
    imageCheck.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override
      public boolean onPreferenceClick(Preference preference) {
        currentData = CurrentDataManager.getInstance(fragment.getContext());
        currentData.setImageCheck(!currentData.isImageCheck());
        return false;
      }
    });


    final Preference dcconCheck = fragment.getPreferenceManager()
            .findPreference("dccon_check");
    dcconCheck.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override
      public boolean onPreferenceClick(Preference preference) {
        currentData = CurrentDataManager.getInstance(fragment.getContext());
        currentData
                .setDcconCheck(!currentData.isDcconCheck());
        return false;
      }
    });
  }


  // 진동 버튼, 저화질 선택시 데이터 인스턴스에도 적용
  private void setVibAndResolutionButtons(final SettingFragment fragment) {
    final Preference articleTabVib = fragment.getPreferenceManager()
            .findPreference("article_tab_vib");
    articleTabVib.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override
      public boolean onPreferenceClick(Preference preference) {
        currentData = CurrentDataManager.getInstance(fragment.getContext());
        currentData
                .setArticleTabVib(!currentData.isArticleTabVib());
        return false;
      }
    });

    final Preference articleCloseVib = fragment.getPreferenceManager()
            .findPreference("article_close_vib");
    articleCloseVib.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override
      public boolean onPreferenceClick(Preference preference) {
        currentData = CurrentDataManager.getInstance(fragment.getContext());
        currentData
                .setArticleCloseVib(!currentData.isArticleCloseVib());
        return false;
      }
    });

    final Preference isLowResolution = fragment.getPreferenceManager()
            .findPreference("is_split_load");
    isLowResolution.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override
      public boolean onPreferenceClick(Preference preference) {
        currentData = CurrentDataManager.getInstance(fragment.getContext());
        currentData
                .setSplitLoad(!currentData.isSplitLoad());
        return false;
      }
    });

  }


  // 실험용 모드
  private void setDcmysMode(final SettingFragment fragment) {
    final Preference darkTheme = fragment.getPreferenceManager()
            .findPreference("dark_theme");

    /*
    darkTheme.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override
      public boolean onPreferenceClick(Preference preference) {
        if(counter > 10) {
          currentData = CurrentDataManager.getInstance(fragment.getContext());
          currentData.setMaruViewer(!currentData.isMaruViewer());
          CurrentDataManager.save(fragment.getContext());
          counter = 0;
          Intent intent = new Intent(fragment.getContext(), MainActivity.class);
          intent.putExtra("resetMode", true);
          GalleryListFragment.removeInstance();
          fragment.getActivity().finishAffinity();
          return true;
        }else {
          counter++;
          return false;
        }
      }
    });
    */

  }

  private void setOpenSourceButton(final SettingFragment fragment, final CurrentData currentData) {
    Preference userOpenSourceButton = fragment.getPreferenceManager().findPreference(fragment.getString(R.string.use_opensource));
    userOpenSourceButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override
      public boolean onPreferenceClick(Preference preference) {
        Intent intent = new Intent(fragment.getContext(), OpenSourceActivity.class);
        intent.putExtra("currentData", currentData);
        startActivity(intent);
        return true;
      }
    });
  }

  private void setLoginButton(final SettingFragment fragment) {
    Preference button = fragment.getPreferenceManager().findPreference(fragment.getString(R.string.setting_login));
    button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override
      public boolean onPreferenceClick(Preference preference) {
        Activity mainActivity = fragment.getActivity();
        Intent intent = new Intent(mainActivity, MainActivity.class);
        intent.putExtra("resetMode", true);
        mainActivity.finishAffinity();
        mainActivity.startActivity(intent);
        return true;
      }
    });
  }


  @Override
  public void setDivider(Drawable divider) {
    super.setDivider(new ColorDrawable(Color.TRANSPARENT));
  }

  private void setFilterUserListButton(final SettingFragment fragment) {
    Preference filterUserListButton = fragment.getPreferenceManager().findPreference("filter_user_list");
    filterUserListButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override
      public boolean onPreferenceClick(Preference preference) {
        Intent intent = new Intent(fragment.getContext(), FilterUserListActivity.class);
        startActivity(intent);
        return true;
      }
    });
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
  }

}

package dc.maitetsufd.ui;

import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dc.maitetsufd.R;
import dc.maitetsufd.data.CurrentData;
import dc.maitetsufd.data.CurrentDataManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 사용된 오픈소스 액티비티
 */
public class OpenSourceActivity extends AppCompatActivity {

  @BindView(R.id.use_open_source_list) ListView listView;
  @BindColor(R.color.darkThemeLightBackground) int darkThemeLightBackground;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    CurrentData currentData = CurrentDataManager.getInstance(this);
    setTheme(currentData);
    setContentView(R.layout.activity_open_source);
    ButterKnife.bind(this);
    setListView();
  }

  // 사용된 리스트
  private void setListView() {
    String[] list = new String[]{ "SwipeBackLayout 1.0",
                                  "Glide 3.7.0",
                                  "ButterKnife 8.5.1",
                                  "Apache common-io 2.5",
                                  "Lombok 1.16.16",
                                  "Jsoup 1.10.2",
                                  "PhotoView 1.2.4",
                                  "Simple Magic 1.11",
                                  "Google json-simple 1.1.1"};

    ArrayList<Map<String, String>> dataList = new ArrayList<>();
    for(String str: list) {
      Map<String, String> data = new HashMap<>();
      data.put("name", str);
      dataList.add(data);
    }

    SimpleAdapter adapter = new SimpleAdapter(this,
                                              dataList,
                                              android.R.layout.simple_list_item_1,
                                              new String[]{"name"},
                                              new int[]{android.R.id.text1});
    listView.setAdapter(adapter);
  }

  // 닫기 버튼
  @OnClick(R.id.use_open_source_close)
  public void finishButton(){
    finish();
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

}

package dc.maitetsu.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import dc.maitetsu.R;
import dc.maitetsu.data.CurrentData;
import dc.maitetsu.data.CurrentDataManager;
import dc.maitetsu.models.ArticleModify;
import dc.maitetsu.ui.viewmodel.ArticleWriteViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 글 쓰기 액티비티.
 */
public class ArticleWriteActivity extends AppCompatActivity {

  private ArticleWriteViewModel articleWriteViewModel;
  public List<File> attachFiles = new ArrayList<>();
  @BindView(R.id.article_write_activity_title) TextView titleBar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    CurrentData currentData = CurrentDataManager.getInstance(this);
    setThemeColor(currentData);
    setContentView(R.layout.activity_article_write);
    ButterKnife.bind(this);


    ArticleModify articleModify = (ArticleModify) getIntent().getSerializableExtra("articleModify");
    articleWriteViewModel = new ArticleWriteViewModel(this, articleModify, currentData);
    setTitle(articleModify, currentData);
  }


  private void setThemeColor(CurrentData currentData) {
    if(currentData.isDarkTheme()) {
      setTheme(R.style.DarkTheme);
      if (Build.VERSION.SDK_INT >= 21) {
        getWindow().setStatusBarColor(
                ContextCompat.getColor(this, R.color.darkThemeLightBackground));
      }
    }
  }


  @Override
  protected void onDestroy() {
    super.onDestroy();
    attachFiles.clear();
  }

  //타이틀바 텍스트와 스테이터스바 색상 설정
  private void setTitle(ArticleModify articleModify, CurrentData currentData) {
    String title;

    if(articleModify == null) title = getResources().getString(R.string.write_article_title_bar);
    else title = getString(R.string.modify_article_title_bar);

    titleBar.setText(String.format(title, currentData.getGalleryInfo().getGalleryName()));
  }

  // 이미지 첨부로 액티비티에 되돌아왔을 때 핸들링
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 0 && resultCode == RESULT_OK && data.getData() != null) {
      Uri uri = data.getData();
      String path = getAbsolutePath(uri);
      if(path != null && !path.isEmpty()) {
        attachFiles.add(new File(path));
        articleWriteViewModel.setAttachFiles(this, attachFiles);
      }
    }
  }


  // 갤러리에서 전달받은 uri로 파일의 절대주소를 찾음.
  private String getAbsolutePath(Uri uri) {
    String[] projection = { MediaStore.MediaColumns.DATA };
    @SuppressWarnings("deprecation")
    Cursor cursor = managedQuery(uri, projection, null, null, null);
    if (cursor != null) {
      int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
      cursor.moveToFirst();
      return cursor.getString(column_index);
    } else
      return null;
  }

}
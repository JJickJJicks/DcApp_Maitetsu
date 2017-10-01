package dc.maitetsu.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import dc.maitetsu.R;
import dc.maitetsu.data.ImageData;
import dc.maitetsu.ui.adapter.ImageViewPagerAdapter;
import dc.maitetsu.utils.ImageViewPager;
import dc.maitetsu.utils.MainUIThread;
import dc.maitetsu.utils.ThreadPoolManager;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;

public class ImageViewActivity extends AppCompatActivity {

  @BindView(R.id.image_view_pager)  ImageViewPager viewPager;
  @BindView(R.id.activity_image_view)  FrameLayout layout;
  @BindView(R.id.image_view_tools)  LinearLayout toolLayout;
  @BindView(R.id.image_view_height_fit) ImageView widthFitButton;
  @BindView(R.id.image_view_fit) ImageView viewFitButton;

  private SparseArray<byte[]> imageBytes;
  private ImageViewPagerAdapter imageViewPagerAdapter;
  private boolean isPortrait = true;
  private String name;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_image_view);
    ButterKnife.bind(this);

    if(getIntent().getBooleanExtra("hideStatusBar", false)) {
      hideStatusBar();
    }

    int position = getIntent().getIntExtra("position", 0);
    name = getIntent().getStringExtra("name");

    imageBytes = ImageData.getHoldImageBytes();
    imageViewPagerAdapter = new ImageViewPagerAdapter(this, toolLayout, imageBytes, ImageView.ScaleType.FIT_CENTER);
    viewPager.setAdapter(imageViewPagerAdapter);
    viewPager.setCurrentItem(position);
    viewPager.setOffscreenPageLimit(2);
  }

  private void hideStatusBar() {
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
  }


  /**
   * 높이 맞춤 버튼
   */
  @OnClick(R.id.image_view_height_fit)
  void widthFit() {
    viewFitButton.setVisibility(View.VISIBLE);
    widthFitButton.setVisibility(View.GONE);

    int currentItem = viewPager.getCurrentItem();
    imageViewPagerAdapter = new ImageViewPagerAdapter(this, toolLayout, imageBytes, ImageView.ScaleType.CENTER_CROP);
    viewPager.setAdapter(imageViewPagerAdapter);
    viewPager.setCurrentItem(currentItem);
  }


  /**
   * 가운데 맞춤 버튼
   */
  @OnClick(R.id.image_view_fit)
  void viewFit() {
    viewFitButton.setVisibility(View.GONE);
    widthFitButton.setVisibility(View.VISIBLE);
    imageViewPagerAdapter = new ImageViewPagerAdapter(this, toolLayout, imageBytes, ImageView.ScaleType.FIT_CENTER);
    int currentItem = viewPager.getCurrentItem();
    viewPager.setAdapter(imageViewPagerAdapter);
    viewPager.setCurrentItem(currentItem);
  }


  /**
   * 이미지 회전
   */
  @OnClick(R.id.image_view_rotate)
  void rotate() {
    FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) viewPager.getLayoutParams();
    int width = layout.getWidth();
    int height = layout.getHeight();
    if (isPortrait) {
      isPortrait = false;
      viewPager.setRotation(90);
      lp.width = height;
      lp.height = width;

    } else {
      isPortrait = true;
      viewPager.setRotation(0);
      lp.width = width;
      lp.height = height;
    }
      viewPager.requestLayout();
  }

  /**
   * 지금 보고있는 이미지 다운로드
   */
  @OnClick(R.id.image_view_download)
  void download() {
    int item = viewPager.getCurrentItem();
    ContentInfoUtil util = new ContentInfoUtil();
    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    try {
      ContentInfo info = util.findMatch(imageBytes.get(item));
      File file = new File(path, name + "_" + item
              + "." + info.getContentType().name());
      FileOutputStream stream = new FileOutputStream(file, true);
      stream.write(imageBytes.get(item));
      stream.close();
      MainUIThread.showToast(this, String.format(getString(R.string.download_success), file.getName()));
      this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file)));
    } catch (Exception e) {
      MainUIThread.showToast(this, getString(R.string.download_failure));
    }
  }

  /**
   * 이미지 싹다 다운로드
   */
  @OnClick(R.id.image_view_download_all)
  void downloadAll() {

    ContentInfoUtil util = new ContentInfoUtil();
    try {
      File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
      File folder = new File(path, name);
      folder.mkdir();

      for (int item = 0; item < imageBytes.size(); item++) {
        if (imageBytes.get(item) == null) continue;
        ContentInfo info = util.findMatch(imageBytes.get(item));
        File file = new File(folder, name + "_" + item
                + "." + info.getContentType().name());

        FileOutputStream stream = new FileOutputStream(file, true);
        stream.write(imageBytes.get(item));
        stream.close();
      }
      MainUIThread.showToast(this, getString(R.string.download_all_success));
      this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + folder)));
    } catch (Exception e) {
      MainUIThread.showToast(this, getString(R.string.download_failure));
    }
  }


  @Override
  protected void onDestroy() {
    super.onDestroy();
    ThreadPoolManager.shutdownImageViewEc();
  }
}

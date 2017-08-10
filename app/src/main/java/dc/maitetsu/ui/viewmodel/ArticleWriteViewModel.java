package dc.maitetsu.ui.viewmodel;

import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import butterknife.BindView;
import butterknife.ButterKnife;
import dc.maitetsu.R;
import dc.maitetsu.data.CurrentData;
import dc.maitetsu.models.ArticleModify;
import dc.maitetsu.ui.ArticleWriteActivity;
import dc.maitetsu.ui.fragment.ArticleSubmitDialogFragment;
import dc.maitetsu.utils.ContentUtils;
import dc.maitetsu.utils.DipUtils;
import dc.maitetsu.utils.MainUIThread;

import java.io.File;
import java.util.List;

/**
 * 글쓰기 액티비티 뷰모델
 *
 * @author Park Hyo Jun
 * @since 2017-04-23
 */
public class ArticleWriteViewModel {
  private CurrentData currentData;
  private ArticleModify articleModify;

  @BindView(R.id.article_write_submit)
  Button submitButton;
  @BindView(R.id.article_write_content)
  EditText articleContent;
  @BindView(R.id.article_write_title)
  EditText articleTitle;
  @BindView(R.id.article_write_cancle)
  Button cancleButton;
  @BindView(R.id.article_write_file_infos)
  GridLayout attachFileLayout;
  @BindView(R.id.article_write_file_add)
  Button attachButton;


  public ArticleWriteViewModel(ArticleWriteActivity activity, ArticleModify articleModify, CurrentData currentData) {
    ButterKnife.bind(this, activity);
    this.articleModify = articleModify;
    this.currentData = currentData;
    setSubmitOrModifyButton(activity, articleModify);
    setAttachButtonClick(activity);
    setCancleButton(activity);
    setAttachFiles(activity, null);
  }

  // 수정이나 등록 버튼 처리
  private void setSubmitOrModifyButton(final ArticleWriteActivity activity, final ArticleModify articleModify) {

    if(articleModify != null) { // 수정 모드
      submitButton.setText(activity.getString(R.string.modify));
      articleTitle.setText(articleModify.getTitle());
      articleContent.setText(articleModify.getContent());
    }

    // 버튼 처리
    submitButton.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View view) {
        String title = articleTitle.getText().toString();
        String content = articleContent.getText().toString();

        if (title.trim().isEmpty() || content.trim().isEmpty()) {
          MainUIThread.showToast(activity, activity.getString(R.string.write_empty_msg));
        } else {
          ArticleSubmitDialogFragment alert = ArticleSubmitDialogFragment.newInstance(activity,
                  title, content, submitButton, currentData, activity.attachFiles, articleModify);

          alert.show(activity.getFragmentManager(), "submitDialog");
        }
      }
    });
  }

  // 취소버튼과 등록 버튼을 핸들링하는 메소드
  private void setCancleButton(final ArticleWriteActivity activity) {
    // 취소버튼을 누르면 액티비티 종료
    cancleButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        activity.finish();
      }
    });
  }


  /**
   * 첨부할 파일을 설정하는 메소드
   *
   * @param activity the activity
   * @param files    the files
   */
  public void setAttachFiles(ArticleWriteActivity activity, final List<File> files) {

    int dp80 = DipUtils.getDp(activity.getResources(), 80);
    attachFileLayout.removeAllViews();

    if(articleModify != null) { // 수정모드면 이전 등록된 이미지를 표기한다
      for(final ArticleModify.AttachFile file : articleModify.getAttachFileList()) {
        if(articleModify.getDeleteFileList().contains(file.getFno())) continue;

        final TextView textView = new TextView(activity);
        textView.setText(file.getName());
        textView.setTextAppearance(activity, R.style.List_subText);
        textView.setGravity(Gravity.CENTER);
        textView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            attachFileLayout.removeView(textView);
            articleModify.getDeleteFileList().add(file.getFno());
          }
        });
        attachFileLayout.addView(textView, dp80, dp80);
      }
    }

    if(files != null) {
      for (final File file : files) {
        final ImageButton attachImg = new ImageButton(activity);
        ContentUtils.loadBitmapFromLocal(activity, file, attachImg, currentData);
        attachImg.setAdjustViewBounds(true);
        attachImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
        attachImg.setBackgroundColor(android.R.color.transparent);
        attachImg.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            attachFileLayout.removeView(attachImg);
            files.remove(file);
            attachImg.setImageDrawable(null);
          }
        });
        attachFileLayout.addView(attachImg, dp80, dp80);
      }
    }
  }

  // 첨부 버튼 핸들링 메소드
  // 이미지 형태만 선택 가능하게 함.
  private void setAttachButtonClick(final ArticleWriteActivity activity) {
    attachButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        activity.startActivityForResult(intent, 0);
      }
    });
  }


}

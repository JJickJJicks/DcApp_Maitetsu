package dc.maitetsu.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import dc.maitetsu.R;
import dc.maitetsu.data.CurrentData;
import dc.maitetsu.models.ArticleModify;
import dc.maitetsu.service.ServiceProvider;
import dc.maitetsu.ui.ArticleWriteActivity;

import java.io.File;
import java.util.List;

/**
 *  글 등록 확인 다이얼로그
 *
 * @author Park Hyo Jun
 * @since 2017-04-24
 */
public class ArticleSubmitDialogFragment extends DialogFragment {
  public String articleTitle;
  public String articleContent;
  public ArticleWriteActivity articleWriteActivity;
  public List<File> attachFiles;
  public Button submitButton;
  public ArticleModify articleModify;
  public CurrentData currentData;

  public static ArticleSubmitDialogFragment newInstance(ArticleWriteActivity activity,
                                                        String articleTitle,
                                                        String articleContent,
                                                        Button submitButton,
                                                        CurrentData currentData,
                                                        List<File> attachFiles,
                                                        ArticleModify articleModify) {
    ArticleSubmitDialogFragment fragment = new ArticleSubmitDialogFragment();
    fragment.articleTitle = articleTitle;
    fragment.articleContent = articleContent;
    fragment.articleWriteActivity = activity;
    fragment.currentData = currentData;
    fragment.attachFiles = attachFiles;
    fragment.submitButton = submitButton;
    fragment.articleModify = articleModify;
    return fragment;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder;
    if( currentData.isDarkTheme()) builder = new AlertDialog.Builder(getActivity(), R.style.DarkTheme_DialogCustom);
    else builder= new AlertDialog.Builder(getActivity());
    builder.setMessage(R.string.article_submit_dialog_text)
            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                ServiceProvider.getInstance().writeArticle(articleWriteActivity,
                                                            submitButton, articleTitle,
                                                            articleContent,attachFiles, articleModify);
              }
            })
            .setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
              }
            });
    // Create the AlertDialog object and return it
    return builder.create();
  }
}

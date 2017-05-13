package dc.maitetsu.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import dc.maitetsu.R;
import dc.maitetsu.data.CurrentData;
import dc.maitetsu.models.ArticleDetail;
import dc.maitetsu.service.ServiceProvider;
import dc.maitetsu.ui.ArticleDetailActivity;

/**
 * @since 2017-04-24
 *
 * 글 삭제 확인 다이얼로그
 *
 */
public class ArticleDeleteDialogFragment extends DialogFragment {

    public ArticleDetail articleDetail;
    private ArticleDetailActivity articleDetailActivity;
    public CurrentData currentData;

    public ArticleDeleteDialogFragment(){}

    public static ArticleDeleteDialogFragment newInstance(
            ArticleDetail articleDetail,
            ArticleDetailActivity articleDetailActivity,
            CurrentData currentData) {
      ArticleDeleteDialogFragment fragment = new ArticleDeleteDialogFragment();
      fragment.articleDetail = articleDetail;
      fragment.articleDetailActivity = articleDetailActivity;
      fragment.currentData = currentData;
      return fragment;
    }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder;
    if( currentData.isDarkTheme()) builder = new AlertDialog.Builder(getActivity(), R.style.DarkTheme_DialogCustom);
    else builder= new AlertDialog.Builder(getActivity());
    builder.setMessage(R.string.article_delete_dialog_text)
            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                ServiceProvider.getInstance()
                        .deleteArticle(articleDetailActivity.articleDetailViewModel.deleteButton,
                                articleDetail, articleDetailActivity );
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

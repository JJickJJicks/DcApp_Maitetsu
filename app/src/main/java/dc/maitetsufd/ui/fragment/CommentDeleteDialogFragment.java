package dc.maitetsufd.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.LinearLayout;
import dc.maitetsufd.R;
import dc.maitetsufd.data.CurrentData;
import dc.maitetsufd.models.ArticleDetail;
import dc.maitetsufd.service.ServiceProvider;
import dc.maitetsufd.ui.ArticleDetailActivity;
import dc.maitetsufd.ui.viewmodel.ArticleDetailViewModel;

/**
 * @author Park Hyo Jun
 * @since 2017-04-24
 *
 *  댓글 삭제 확인 다이얼로그
 */
public class CommentDeleteDialogFragment extends DialogFragment {
    public ArticleDetailActivity activity;
    private ArticleDetailViewModel presenter;
    private String deleteCode;
    private String articleUrl;
    public CurrentData currentData;
    public ArticleDetail articleDetail;

    public static CommentDeleteDialogFragment newInstance(ArticleDetailActivity articleDetailActivity,
                                                          ArticleDetailViewModel presenter,
                                                          CurrentData currentData,
                                                          LinearLayout commentLayout,
                                                          String deleteCode,
                                                          ArticleDetail articleDetail,
                                                          String articleUrl){
      CommentDeleteDialogFragment fragment = new CommentDeleteDialogFragment();
      fragment.articleDetail = articleDetail;
      fragment.currentData = currentData;
      fragment.deleteCode = deleteCode;
      fragment.activity = articleDetailActivity;
      fragment.presenter = presenter;
      fragment.articleUrl = articleUrl;
      return fragment;
    }
  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {

    AlertDialog.Builder builder;
    if( currentData.isDarkTheme()) builder = new AlertDialog.Builder(getActivity(), R.style.DarkTheme_DialogCustom);
    else builder = new AlertDialog.Builder(getActivity());  builder.setMessage(R.string.comment_delete_dialog_text);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                ServiceProvider.getInstance().deleteComment(articleDetail,
                        presenter, articleUrl,  deleteCode, activity);
//                ServiceProvider.getInstance().refreshComment(activity, presenter, articleUrl);
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

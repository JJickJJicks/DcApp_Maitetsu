package dc.maitetsu.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import dc.maitetsu.R;
import dc.maitetsu.data.CurrentData;
import dc.maitetsu.ui.ArticleDetailActivity;

/**
 * @since 2017-04-24
 *
 * 이미지 저장 여부 확인 프래그먼트.
 *
 */
public class ImageSaveDialogFragment extends DialogFragment {

    private String url;
    public CurrentData currentData;
    public ArticleDetailActivity activity;

    public ImageSaveDialogFragment(){}

    public static ImageSaveDialogFragment newInstance(
            ArticleDetailActivity articleDetailActivity,
            String url,CurrentData currentData) {
      ImageSaveDialogFragment fragment = new ImageSaveDialogFragment();
      fragment.activity = articleDetailActivity;
      fragment.currentData = currentData;
      fragment.url = url;
      return fragment;
    }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder;
    if( currentData.isDarkTheme()) builder = new AlertDialog.Builder(getActivity(), R.style.DarkTheme_DialogCustom);
    else builder= new AlertDialog.Builder(getActivity());
    builder.setMessage(R.string.image_save_msg)
            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                activity.startActivity(i);
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

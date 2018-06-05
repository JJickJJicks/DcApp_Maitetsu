package dc.maitetsufd.ui;

import android.content.DialogInterface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dc.maitetsufd.data.CurrentData;
import dc.maitetsufd.data.CurrentDataManager;
import dc.maitetsufd.R;
import dc.maitetsufd.ui.adapter.BlockWordListAdapter;
import dc.maitetsufd.utils.DipUtils;
import dc.maitetsufd.utils.MainUIThread;

import java.util.ArrayList;
import java.util.List;

public class BlockWordListActivity extends AppCompatActivity {
  @BindView(R.id.block_word_save) Button saveButton;
  @BindView(R.id.block_word_put_button) Button putWordButton;
  @BindView(R.id.block_word_list_view) ListView blockWordListView;
  CurrentData currentData;
  BlockWordListAdapter blockWordListAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    currentData = CurrentDataManager.getInstance(this);
    setTheme(currentData);
    setContentView(R.layout.activity_block_word_list);
    ButterKnife.bind(this);

    blockWordListAdapter = new BlockWordListAdapter();
    blockWordListAdapter.addAllBlockWords(currentData.getBlockWordList());
    blockWordListView.setAdapter(blockWordListAdapter);
  }

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

  @OnClick(R.id.block_word_save)
  public void saveButtonClick() {
    currentData.setBlockWordList(blockWordListAdapter.getBlockWords());
    CurrentDataManager.save(this);
    MainUIThread.showToast(this, this.getResources().getString(R.string.save_msg));
    finish();
  }

  @OnClick(R.id.block_word_cancel)
  public void cancelButtonClick() {
    finish();
  }

  @OnClick(R.id.block_word_put_button)
  public void putBLockWord() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(this.getResources().getString(R.string.put_block_word));

    final EditText input = new EditText(this);
    input.setPadding(input.getPaddingLeft() + 40, input.getPaddingTop(),
                   input.getPaddingRight() + 40, input.getPaddingBottom());
    input.setSingleLine(true);
    input.setInputType(InputType.TYPE_CLASS_TEXT);
    builder.setView(input);

    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        String word = input.getText().toString().toUpperCase();
        if (!word.isEmpty()) {
          blockWordListAdapter.addBlockWord(word);
          blockWordListAdapter.notifyDataSetChanged();
        }
      }
    });
    builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
      }
    });
    builder.show();
  }

}

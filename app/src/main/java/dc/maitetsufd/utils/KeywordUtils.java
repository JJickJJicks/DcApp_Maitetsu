package dc.maitetsufd.utils;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import java.util.List;

/**
 * @since 2017-04-28
 * 검색 단어 글자 색상 처리
 */
public class KeywordUtils {

  public static SpannableString colorText(String str, int color) {
    SpannableString colorText = new SpannableString(str);
    colorText.setSpan(new ForegroundColorSpan(color), 0,
            str.length(), 0);
    return colorText;
  }

  public static SpannableStringBuilder getBuilder(String str, String keyword, SpannableStringBuilder builder) {
    String[] splitStr = str.split(keyword);
    if (builder == null) builder = new SpannableStringBuilder();
    if (splitStr.length > 1) {
      SpannableString colorKeyword = new SpannableString(keyword);
      colorKeyword.setSpan(new ForegroundColorSpan(Color.RED), 0,
              keyword.length(), 0);
      for (int i = 0; i < splitStr.length; i++) {
        builder.append(splitStr[i]);
        if (i < splitStr.length - 1) builder.append(colorKeyword);
      }
    } else
      builder.append(str);

    return builder;
  }

  public static SpannableStringBuilder getBuilder(final List<String> strs, final String keyword) {
    SpannableStringBuilder builder = null;
    for (String str : strs) {
      builder = getBuilder(str, keyword, builder);
      builder.append("\n");
    }
    return builder;
  }
}

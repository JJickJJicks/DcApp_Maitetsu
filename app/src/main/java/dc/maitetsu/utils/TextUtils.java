package dc.maitetsu.utils;

/**
 * @since 2017-05-07
 */
public class TextUtils {

  public static String replaceHTMLText(String htmlText) {
    return htmlText
            .replaceAll("&gt;", ">")
            .replaceAll("&lt;", "<")
            .replaceAll("&amp;", "&")
            .replaceAll("&quot;", "\"")
            .replaceAll("\t|&nbsp;", " ");
  }
}

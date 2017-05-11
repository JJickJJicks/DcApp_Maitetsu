package dc.maitetsu.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * @author Park Hyo Jun
 * @since 2017-05-02
 */
public class ContentWebView {

  public static WebView get(Activity activity, String url) {

    WebView webview = new WebView(activity);
    webview.setBackgroundColor(android.R.color.transparent);
    webview.setPadding(2, 2, 2, 2);
    String frameUrl = checkUrl(url);
    String frame = "<html><body><iframe width=\"100%\" height=\"100%\" src=\""
                    + frameUrl
                    + "\" frameborder=\"0\" allowfullscreen></iframe></body></html>";

    webview.setWebViewClient(new WebViewClient() {
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url != null && url.startsWith("http://")) {
          view.getContext().startActivity(
                  new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
          return true;
        } else {
          return false;
        }
      }
    });

    WebSettings webSettings = webview.getSettings();
    webSettings.setJavaScriptEnabled(true);
    webSettings.setSupportMultipleWindows(true);
    webview.loadData(frame, "text/html", "utf-8");
    return webview;
  }


  private static String checkUrl(String url) {
    try {
      String[] slashUrls = url.split("//");
      String slashUrl = slashUrls[0];
      if(slashUrls.length > 1) slashUrl = slashUrls[1];

      String[] splitUrl = slashUrl.split("www.youtube.com/v/");
      if (splitUrl.length > 1) {
        String code = splitUrl[1].split("\\?")[0];
        return "https://www.youtube.com/embed/" + code;
      } else
        return "https://" + slashUrl;


    }catch(Exception e) { return ""; }
  }
}

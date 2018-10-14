package dc.maitetsufd.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * @since 2017-05-02
 */
public class ContentWebView {

  public static WebView get(final Activity activity, String url) {

    final WebView webview = new WebView(activity);
    webview.setBackgroundColor(android.R.color.transparent);
    webview.setPadding(2, 2, 2, 2);
    final String frameUrl = checkUrl(url);
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
    // 꾹 누르면 브라우저로 띄우기
    webview.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View view) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(frameUrl));
        activity.startActivity(i);
        return false;
      }
    });

    webview.setWebChromeClient(new WebChromeClient());

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

      String[] splitUrl = slashUrl
              .replace("www.youtube.com/watch?v=", "www.youtube.com/v/")
              .split("www.youtube.com/v/");


      if (splitUrl.length > 1) {
        String code = splitUrl[1].split("\\?")[0];
        return "https://www.youtube.com/embed/" + code;
      } else
        return "https://" + slashUrl;


    }catch(Exception e) { return ""; }
  }
}

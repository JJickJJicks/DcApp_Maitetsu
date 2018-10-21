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
	private static final String MOBILE_YOUTUBE = "https://m.youtube.com/watch?v=";

  public static WebView get(final Activity activity, final String url) {

    final WebView webview = new WebView(activity);
    webview.setBackgroundColor(activity.getResources().getColor(android.R.color.transparent));
    webview.setPadding(2, 2, 2, 2);
    String frame = "<html><body><iframe width=\"100%\" height=\"100%\" src=\""
                    + url
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
        if (url == null) return false;

        String code = "";
        String splitUrl[] = url.split("embed/");
        if (splitUrl.length > 0) {
          code = splitUrl[1];
        } else {
          return false;
        }
		
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(MOBILE_YOUTUBE + code));
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

}

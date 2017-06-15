package dc.maitetsu.service;

import android.webkit.URLUtil;
import dc.maitetsu.models.MaruModel;
import dc.maitetsu.models.MaruSimpleModel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @since 2017-04-29
 */
enum MaruService {
  getInstance;

  private static final String MARU_URL = "http://marumaru.in/";
  private static final String MARU_LINK_URL = "http://marumaru.in/b/mangaup/";
  private static final String PASS = "?pass=qndxkr";


  public List<MaruSimpleModel> getMaruSimpleModels(String userAgent, int page, String keyword) throws IOException {
    Document rawData = getTitleRawData(userAgent, page, keyword);
    List<MaruSimpleModel> maruSimpleModels = new ArrayList<>();

    Elements elements = rawData.select(".list");
    for(Element element: elements) {
      String thumbStyle = element.select(".image-thumb").attr("style");
      if(thumbStyle.trim().isEmpty()) continue;

      String no = element.previousElementSibling().attr("name");
      String thumbUrl = MARU_URL + thumbStyle.split("\\(")[1].split("\\)")[0];
      String title = element.select(".subject").text();
      String date = element.select(".info").text().split(" \\|")[0];

      maruSimpleModels.add(new MaruSimpleModel(no, thumbUrl, title, date));
    }

    return maruSimpleModels;
  }


  // 업데이트 목록
  private Document getTitleRawData(String userAgent, int page, String keyword) throws IOException {
    String category = "?c=26";
    String searchKeyword = "&where=subject&keyword=" + keyword;
    String pageKeyword = "&sort=gid&p=" + page;

    return Jsoup.connect(MARU_URL + category + searchKeyword + pageKeyword)
            .userAgent(userAgent)
            .timeout(5000)
            .header("Origin", "http://marumaru.in/")
            .header("Referer", MARU_URL)
            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
            .header("Accept-Encoding", "gzip, deflate, sdch")
            .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
            .get();
  }


  private String getMaruUrl(String userAgent, String no) throws IOException {
    return Jsoup.connect(MARU_LINK_URL + no.substring(1))
            .userAgent(userAgent)
            .timeout(5000)
            .header("Origin", "http://marumaru.in/")
            .header("Referer", MARU_URL)
            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
            .header("Accept-Encoding", "gzip, deflate, sdch")
            .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
            .get()
            .select("#vContent a").first().attr("abs:href");
  }



  public MaruModel getImageUrls(String userAgent, String no) throws IOException {
    String url = getMaruUrl(userAgent, no);
    Document rawData =  Jsoup.connect(url + PASS)
                              .userAgent(userAgent)
                              .timeout(5000)
                              .header("Origin", "http://marumaru.in/")
                              .header("Referer", MARU_LINK_URL + no)
                              .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                              .header("Accept-Encoding", "gzip, deflate, sdch")
                              .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
                              .get();
    List<String> urls = new ArrayList<>();
    Elements elements = rawData.select(".lz-lazyload");
    for(Element element : elements) {
      String imageUrl = URLUtil.guessUrl(element.attr("abs:data-src").replaceAll(" ", "%20"));
      urls.add(imageUrl);
    }

    MaruModel maruModel = new MaruModel();
    maruModel.setUrl(url);
    maruModel.setImagesUrls(urls);

    return maruModel;
  }

}

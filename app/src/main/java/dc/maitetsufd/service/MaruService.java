package dc.maitetsufd.service;

import android.util.Log;
import dc.maitetsufd.models.MangaContentModel;
import dc.maitetsufd.models.MangaSimpleModel;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @since 2017-04-29
 */
enum MaruService implements IMangaService {
  getInstance;

  private static final String MARU_URL = "http://marumaru.in/";
  private static final String MARU_LINK_URL = "http://marumaru.in/b/mangaup/";
  private static final String PASS = "qndxkr";
  private static final String ORIGIN = "http://wasabisyrup.com/";
  private static Map<String, String> cookies = new HashMap<>();
  static Map<String, String> contentCookies = new HashMap<>();

  public List<MangaSimpleModel> getSimpleModels(String userAgent, int page, String keyword) throws IOException {
    Document rawData = getTitleRawData(userAgent, page, keyword);

    List<MangaSimpleModel> mangaSimpleModels = new ArrayList<>();
    Elements elements = rawData.select(".list");
    for (Element element : elements) {
      String thumbStyle = element.select(".image-thumb").attr("style");
      if (thumbStyle.trim().isEmpty()) continue;

      String no = element.previousElementSibling().attr("name");
      String thumb = thumbStyle.split("\\(")[1].split("\\)")[0];
      String thumbUrl = thumb.contains("http") ? thumb : MARU_URL + thumb;
      String title = element.select(".subject").text();
      String date = element.select(".info").text().split(" \\|")[0];

      mangaSimpleModels.add(new MangaSimpleModel(no, thumbUrl, title, date, false));
    }

    return mangaSimpleModels;
  }


  // 업데이트 목록
  private Document getTitleRawData(String userAgent, int page, String keyword) throws IOException {
    String category = "?m=bbs&bid=mangaup";
    String searchKeyword = "&where=subject&keyword=" + keyword;
    String pageKeyword = "&sort=gid&p=" + page;

    Connection.Response res = Jsoup.connect(MARU_URL + category + searchKeyword + pageKeyword)
                                  .userAgent(userAgent)
                                  .timeout(5000)
                                  .header("Origin", MARU_URL)
                                  .header("Referer", MARU_URL)
                                  .cookies(cookies)
                                  .method(Connection.Method.GET)
                                  .followRedirects(true)
                                  .execute();
    cookies.putAll(res.cookies());

//    sendIncapsula(res.body(), userAgent);
    return res.parse();
  }


  private String getWasabiUrl(String userAgent, String no) throws IOException {
    Connection.Response response = Jsoup.connect(MARU_LINK_URL + no.substring(1))
                        .userAgent(userAgent)
                        .timeout(5000)
                        .header("Origin", "http://marumaru.in/")
                        .header("Referer", MARU_URL)
                        .cookies(cookies)
                        .followRedirects(true)
                        .method(Connection.Method.GET)
                        .execute();

    cookies.putAll(response.cookies());

    Elements els = response.parse().select("#vContent a");
    for (Element e : els) {
      String url = e.attr("abs:href");
      if (url.contains("archives")) return url;
    }
    return "";
  }


  public MangaContentModel getContentModel(String userAgent, String no, boolean isViewerModel, int count) throws IOException {
    if (count > 3) throw new IOException();

    String url;
    if (isViewerModel)
      url = ORIGIN + "archives/" + no;
    else
      url = getWasabiUrl(userAgent, no);

    Connection.Response response = Jsoup.connect(url)
                                        .timeout(5000)
                                        .header("Origin", ORIGIN)
                                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                                        .header("Accept-Encoding", "gzip, deflate")
                                        .header("User-Agent", userAgent)
                                        .header("Upgrade-Insecure-Requests", "1")
                                        .cookies(contentCookies)
                                        .followRedirects(true)
                                        .method(Connection.Method.GET)
                                        .execute();

    Document rawData = response.parse();
    contentCookies.putAll(response.cookies());

    if (rawData.text().trim().isEmpty()) {
      return getContentModel(userAgent, no, isViewerModel, count + 1);
    }

    int episodeNum = 1;
    String nowEpisodeName = rawData.select(".title-no").first().text().trim();
    String title = rawData.select(".title-subject").first().text()
            + " " + nowEpisodeName;


    // Images
    List<String> urls = new ArrayList<>();
    Element captcha = rawData.select("img[src='/captcha1'").first();
    Elements elements = rawData.select(".lz-lazyload");

    if (elements.size() == 0 && captcha == null) {
      Connection.Response res = Jsoup.connect(url)
                    .header("Origin", ORIGIN)
                    .referrer(url)
                    .userAgent(userAgent)
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                    .header("Accept-Encoding", "gzip, deflate")
                    .header("Upgrade-Insecure-Requests", "1")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .data("pass", PASS)
                    .timeout(5000)
                    .followRedirects(true)
                    .cookies(contentCookies)
                    .method(Connection.Method.POST)
                    .execute();

      contentCookies.putAll(res.cookies());
      rawData = res.parse();
      elements = rawData.select(".lz-lazyload");
    }

    // 캡차가 필요하면
    if (captcha != null) {
      String captchaUrl = captcha.attr("abs:src");
      no = "CAPTCHA";
      urls.add(captchaUrl);
    }

    // 이미지 URL 추가
    for (Element element : elements) {
      String imageUrl = element.attr("abs:data-src");
      urls.add(imageUrl);
    }


    // Episodes
    List<MangaContentModel.MaruEpisode> episodes = new ArrayList<>();
    Elements episodeEls = rawData.select("select.list-articles").first().children();
    for (Element episodeEl : episodeEls) {
      String episodeName = episodeEl.text().trim();
      MangaContentModel.MaruEpisode maruEpisode = new MangaContentModel.MaruEpisode(
              episodeName,
              episodeEl.attr("value")
      );
      if (nowEpisodeName.contains(episodeName)) {
        episodeNum = episodes.size();
      }
      episodes.add(maruEpisode);
    }


    MangaContentModel mangaContentModel = new MangaContentModel();
    mangaContentModel.setNo(no);
    mangaContentModel.setTitle(title);
    mangaContentModel.setUrl(url);
    mangaContentModel.setOrigin(ORIGIN);
    mangaContentModel.setTitle(title);
    mangaContentModel.setEpisodeNum(episodeNum);
    mangaContentModel.setEpisodes(episodes);
    mangaContentModel.setImagesUrls(urls);
    return mangaContentModel;
  }


  public void postCaptcha(String userAgent, String url, String captcha) throws IOException {
    Connection.Response response = Jsoup.connect(url)
                                        .header("Origin", ORIGIN)
                                        .referrer(url)
                                        .userAgent(userAgent)
                                        .timeout(5000)
                                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                                        .header("Accept-Encoding", "gzip, deflate")
                                        .header("Upgrade-Insecure-Requests", "1")
                                        .header("Content-Type", "application/x-www-form-urlencoded")
                                        .followRedirects(true)
                                        .data("captcha1", captcha)
                                        .data("pass", PASS)
                                        .cookies(contentCookies)
                                        .method(Connection.Method.POST)
                                        .execute();

    contentCookies.putAll(response.cookies());
  }

}
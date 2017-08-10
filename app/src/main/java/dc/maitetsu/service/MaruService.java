package dc.maitetsu.service;

import dc.maitetsu.models.MaruContentModel;
import dc.maitetsu.models.MaruModel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
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
  private static final String ORIGIN = "http://wasabisyrup.com/";

  public List<MaruModel> getMaruSimpleModels(String userAgent, int page, String keyword) throws IOException {
    Document rawData = getTitleRawData(userAgent, page, keyword);
    List<MaruModel> maruModels = new ArrayList<>();

    Elements elements = rawData.select(".list");
    for(Element element: elements) {
      String thumbStyle = element.select(".image-thumb").attr("style");
      if(thumbStyle.trim().isEmpty()) continue;

      String no = element.previousElementSibling().attr("name");
      String thumbUrl = MARU_URL + thumbStyle.split("\\(")[1].split("\\)")[0];
      String title = element.select(".subject").text();
      String date = element.select(".info").text().split(" \\|")[0];

      maruModels.add(new MaruModel(no, thumbUrl, title, date, false));
    }

    return maruModels;
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
    Elements els = Jsoup.connect(MARU_LINK_URL + no.substring(1))
            .userAgent(userAgent)
            .timeout(5000)
            .header("Origin", "http://marumaru.in/")
            .header("Referer", MARU_URL)
            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
            .header("Accept-Encoding", "gzip, deflate, sdch")
            .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
            .get()
            .select("#vContent a");

    for (Element e : els) {
      String url = e.attr("abs:href");
      if(url.contains("archives")) return url;
    }
    return "";
  }



  public MaruContentModel getMaruModel(String userAgent, String no, boolean isViewerModel, int count) throws IOException {
    if(count > 3) throw new IOException();

    String url;
    if(isViewerModel)
      url = ORIGIN + "archives/" + no;
    else
      url = getMaruUrl(userAgent, no);


    Document rawData =  Jsoup.connect(url + PASS)
                              .userAgent(userAgent)
                              .timeout(5000)
                              .header("Origin", ORIGIN)
                              .header("Upgrade-Insecure-Requests", "1")
                              .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                              .header("Accept-Encoding", "gzip, deflate")
                              .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
                              .get();

    if(rawData.text().trim().isEmpty()){
      return getMaruModel(userAgent, no, isViewerModel, count+1);
    }

    int episodeNum = 1;
    String nowEpisodeName = rawData.select(".title-no").first().text().trim();
    String title = rawData.select(".title-subject").first().text()
                    + " " + nowEpisodeName;


    // Images
    List<String> urls = new ArrayList<>();
    Elements elements = rawData.select(".lz-lazyload");
    for(Element element : elements) {
      String imageUrl = element.attr("abs:data-src");
      urls.add(imageUrl);
    }


    // Episodes
    List<MaruContentModel.MaruEpisode> episodes = new ArrayList<>();
    Elements episodeEls = rawData.select("select.list-articles").first().children();
    for (Element episodeEl : episodeEls) {
      String episodeName = episodeEl.text().trim();
      MaruContentModel.MaruEpisode maruEpisode = new MaruContentModel.MaruEpisode(
                                      episodeName,
                                      episodeEl.attr("value")
                                        );
      if(nowEpisodeName.contains(episodeName)) {
        episodeNum = episodes.size();
      }
      episodes.add(maruEpisode);
    }


    MaruContentModel maruContentModel = new MaruContentModel();
    maruContentModel.setNo(no);
    maruContentModel.setTitle(title);
    maruContentModel.setUrl(url);
    maruContentModel.setOrigin(ORIGIN);
    maruContentModel.setTitle(title);
    maruContentModel.setEpisodeNum(episodeNum);
    maruContentModel.setEpisodes(episodes);
    maruContentModel.setImagesUrls(urls);
    return maruContentModel;
  }

}

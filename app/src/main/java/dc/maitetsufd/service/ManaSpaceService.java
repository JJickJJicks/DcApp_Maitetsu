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
 * @author Park Hyo Jun
 * @since 2017-08-14
 */
enum ManaSpaceService implements IMangaService {
  getInstance;

  private static final String MANA_URL = "https://manaa.space/updated-comics/?status=1&querystring_key=page";
  private static final String MANA_ORIGIN = "https://manaa.space";
  private static final String MANA_API = "https://manaa.space/api/read-comics/";
  private static final String MANA_SEARCH_URL = "https://manaa.space/comics/";
  private static Map<String, String> cookies = new HashMap<>();


  @Override
  public List<MangaSimpleModel> getSimpleModels(String userAgent, int page, String keyword) throws IOException {
    Elements rawData = getSimpleRawData(userAgent, page, keyword);
    List<MangaSimpleModel> mangaSimpleModels = new ArrayList<>();

    for (int i = 0; i < rawData.size(); i++) {
      Element element = rawData.get(i);
      MangaSimpleModel sm = new MangaSimpleModel();
      sm.setNo(element.select("a").attr("abs:href"));
      sm.setThumbUrl(element.select("img.updated-comics-thumbnail").attr("abs:data-src"));
      sm.setTitle(element.select("p.updated-comics-title").first().text());
      sm.setDate(element.select("p.updated-comics-date").first().text());
      mangaSimpleModels.add(sm);
    }
    return mangaSimpleModels;
  }

  private Elements getSimpleRawData(String userAgent, int page, String keyword) throws IOException {
    if (!keyword.isEmpty()) { // 검색시
      Connection.Response res = Jsoup.connect(MANA_SEARCH_URL + "?keyword=" + keyword)
              .userAgent(userAgent)
              .timeout(5000)
              .header("Origin", MANA_ORIGIN)
              .header("Referer", MANA_ORIGIN)
              .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
              .header("Accept-Encoding", "gzip, deflate, br")
              .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
              .execute();
      cookies = res.cookies();
      return res.parse().select("comics-horizontal-card");
    } else {
      Connection.Response res = Jsoup.connect(MANA_URL + "&page=" + page)
              .userAgent(userAgent)
              .timeout(5000)
              .header("Origin", MANA_ORIGIN)
              .header("Referer", MANA_ORIGIN)
              .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
              .header("Accept-Encoding", "gzip, deflate, br")
              .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
              .execute();
      cookies = res.cookies();
      return res.parse().select("div.shelf-item");
    }
  }

  @Override
  public MangaContentModel getContentModel(String userAgent, String no, boolean isViewerModel, int count) throws IOException {

    Document document = Jsoup.connect(no)
            .userAgent(userAgent)
            .timeout(5000)
            .header("Origin", "https://manaa.space/")
            .header("Referer", "https://manaa.space/")
            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
            .header("Accept-Encoding", "gzip, deflate, br")
            .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
            .get();

    // 내용 이미지
    List<String> imgUrls = new ArrayList<>();
    String[] splitUrl = no.split("/");
    String postId = splitUrl[splitUrl.length - 1];
    Log.e("err", postId);
    Log.e("err", cookies.toString());


    Document imageDoc = Jsoup.connect(MANA_API)
            .userAgent(userAgent)
            .timeout(5000)
            .header("Origin", MANA_URL)
            .header("Referer", no)
            .header("x-requested-with", "XMLHttpRequest")
            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
            .header("Accept-Encoding", "gzip, deflate, br")
            .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
            .cookies(cookies)
            .data("post_id", postId)
            .ignoreContentType(true)
            .post();
    Log.e("err", " : imageDoc : " + imageDoc.html());


    Elements imgs = document.select("#view img");
    for (int i = 0; i < imgs.size(); i++) {
      Element element = imgs.get(i);
      imgUrls.add(element.attr("abs:src"));
    }

    // 제목
    String title = document.select("h1").first().text();

    // 에피소드
    List<MangaContentModel.MaruEpisode> episodes = new ArrayList<>();

    MangaContentModel mangaContentModel = new MangaContentModel();
    mangaContentModel.setNo(no);
    mangaContentModel.setTitle(title);
    mangaContentModel.setOrigin(MANA_ORIGIN);
    mangaContentModel.setUrl(no);
    mangaContentModel.setImagesUrls(imgUrls);
    mangaContentModel.setEpisodes(episodes);
    mangaContentModel.setEpisodeNum(0);
    return mangaContentModel;
  }
}

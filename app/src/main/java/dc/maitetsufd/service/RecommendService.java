package dc.maitetsufd.service;

import dc.maitetsufd.models.ArticleDetail;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @since 2017-04-22
 */
public enum RecommendService {
  getInstance;
  private static final String RECOMMEND_URL = "http://m.dcinside.com/ajax/recommend";
  private static final String NORECOMMEND_URL = "http://m.dcinside.com/ajax/nonrecommend";
  private static final JSONParser jsonParser = new JSONParser();

  // 게시물 개념글 추천하는 메소드
  public boolean recommend(Map<String, String> loginCookie, String userAgent, ArticleDetail articleDetail) throws IOException, ParseException {

    Document result = Jsoup.connect(RECOMMEND_URL)
                          .userAgent(userAgent)
                          .cookies(loginCookie)
                          .header("Host", "m.dcinside.com")
                          .header("Origin", "http://m.dcinside.com")
                          .header("X-Requested-With", "XMLHttpRequest")
                          .header("X-CSRF-TOKEN", articleDetail.getCommentWriteData().getCsrfToken())
                          .referrer(articleDetail.getUrl())
                          .data(dataSerialize(articleDetail, "recommend_join"))
                          .ignoreContentType(true)
                          .post();

    JSONObject jsonObject = (JSONObject) jsonParser.parse(result.body().text());
    return jsonObject.get("result").equals(true);
  }

  private Map<String, String> dataSerialize(ArticleDetail articleDetail, String type) {
    Map<String, String> data = new HashMap<>();
    data.put("type", type);
    data.put("id", articleDetail.getBoardId());
    data.put("no", articleDetail.getNo());
    return data;
  }


  // 게시물 비추천하는 메소드
  public boolean norecommend(Map<String, String> loginCookie, String userAgent, ArticleDetail articleDetail) throws IOException, ParseException {

    Document result = Jsoup.connect(NORECOMMEND_URL)
                          .userAgent(userAgent)
                          .cookies(loginCookie)
                          .header("Host", "m.dcinside.com")
                          .header("Origin", "http://m.dcinside.com")
                          .header("X-Requested-With", "XMLHttpRequest")
                          .header("X-CSRF-TOKEN", articleDetail.getCommentWriteData().getCsrfToken())
                          .referrer(articleDetail.getUrl())
                          .data(dataSerialize(articleDetail, "nonrecommend_join"))
                          .ignoreContentType(true)
                          .post();

    JSONObject jsonObject = (JSONObject) jsonParser.parse(result.body().text());
    return jsonObject.get("result").equals(true);
  }

}

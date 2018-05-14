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
enum ArticleDeleteService {
  getInstance;

  private static final String ARTICLE_DELETE_URL = "http://m.dcinside.com/_option_write.php";
  private static final JSONParser jsonParser = new JSONParser();

  /**
   * 게시물 삭제를 시도하는 메소드
   *
   * @param loginCookie   로그인 정보
   * @param userAgent     모바일 기기 UserAgent
   * @param articleDetail 게시물
   * @return the boolean
   */
  boolean delete(Map<String, String> loginCookie, String userAgent, ArticleDetail articleDetail) {

    try {
      Document result = Jsoup.connect(ARTICLE_DELETE_URL).cookies(loginCookie)
                              .userAgent(userAgent)
                              .header("Origin", "http://m.dcinside.com")
                              .referrer(articleDetail.getUrl())
                              .header("X-Requested-With", "XMLHttpRequest")
                              .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                              .data(adSerialize(articleDetail.getArticleDeleteData()))
                              .ignoreContentType(true)
                              .post();

      try {
        JSONObject jsonObject = (JSONObject) jsonParser.parse(result.body().text());
        String msg = (String) jsonObject.get("msg");
        if (msg.equals("23")) throw new IllegalAccessException((String) jsonObject.get("data"));
        return msg.equals("1");

      } catch (ParseException pe) {
        throw new IllegalAccessException();
      }

    } catch (Exception e) {
      return delete(loginCookie, userAgent, articleDetail);
    }
  }


  private Map<String, String> adSerialize(ArticleDetail.ArticleDeleteData articleDeleteData) {
    Map<String, String> data = new HashMap<>();
    data.put("mode", articleDeleteData.getMode());
    data.put("no", articleDeleteData.getNo());
    data.put("user_no", articleDeleteData.getUser_no());
    data.put("id", articleDeleteData.getId());
    data.put("page", articleDeleteData.getPage());
    return data;
  }

}

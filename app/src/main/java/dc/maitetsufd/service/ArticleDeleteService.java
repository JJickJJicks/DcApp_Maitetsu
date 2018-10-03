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

  private static final String ARTICLE_DELETE_URL = "http://m.dcinside.com/del/board";
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
                              .header("X-CSRF-TOKEN", articleDetail.getCommentWriteData().getCsrfToken())
                              .header("X-Requested-With", "XMLHttpRequest")
                              .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                              .data(adSerialize(articleDetail, userAgent, loginCookie))
                              .ignoreContentType(true)
                              .post();

      try {
        JSONObject jsonObject = (JSONObject) jsonParser.parse(result.body().text());
        if (jsonObject.get("result").equals(false)) throw new IllegalAccessException((String) jsonObject.get("data"));
        return jsonObject.get("result").equals(true);

      } catch (ParseException pe) {
        throw new IllegalAccessException();
      }

    } catch (Exception e) {
      return delete(loginCookie, userAgent, articleDetail);
    }
  }


  private Map<String, String> adSerialize(ArticleDetail articleDetail, String userAgent, Map<String, String> loginCookie) {
    Map<String, String> data = new HashMap<>();
    ArticleDetail.ArticleDeleteData articleDeleteData = articleDetail.getArticleDeleteData();
    data.put("no", articleDeleteData.getNo());
    data.put("id", articleDeleteData.getId());
    data.put("con_key", AccessTokenService.getInstance.getAccessToken("board_Del", "",
            userAgent, articleDetail.getCommentDeleteData().getCsrfToken(), loginCookie));
    return data;
  }

}

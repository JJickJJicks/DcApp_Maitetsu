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
 * @since 2017-04-21
 */
public enum CommentDeleteService {
  getInstance;

  private static final String COMMENT_WRITE_URL = "http://m.dcinside.com/del/comment";
  private static final JSONParser jsonParser = new JSONParser();


  public boolean delete(Map<String, String> loginCookie, String userAgent, ArticleDetail articleDetail, String deleteNo) {
    try {
      Document result = Jsoup.connect(COMMENT_WRITE_URL).cookies(loginCookie)
                              .userAgent(userAgent)
                              .header("Host", "m.dcinside.com")
                              .header("Origin", "http://m.dcinside.com")
                              .referrer(articleDetail.getUrl())
                              .header("X-Requested-With", "XMLHttpRequest")
                              .header("X-CSRF-TOKEN", articleDetail.getCommentDeleteData().getCsrfToken())
                              .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                              .data(cddSerialize(articleDetail.getCommentDeleteData(), deleteNo, userAgent, loginCookie))
                              .ignoreContentType(true)
                              .timeout(10000)
                              .post();

      JSONObject msg = (JSONObject) jsonParser.parse(result.body().text());
      return msg.get("result").equals(true);

    } catch (Exception e) {
      return true;

    }
  }


  private Map<String, String> cddSerialize(ArticleDetail.CommentDeleteData cdd, String deleteNo,
                                           String userAgent, Map<String, String> loginCookie)  {
    Map<String, String> result = new HashMap<>();
    result.put("comment_no", deleteNo);
    result.put("id", cdd.getId());
    result.put("no", cdd.getNo());
    result.put("best_chk", "");
    result.put("board_id", cdd.getBoard_id());
    result.put("con_key", AccessTokenService.getInstance.getAccessToken("com_submitDel", "",
            cdd.getCsrfToken(), userAgent, loginCookie));

    return result;
  }

}

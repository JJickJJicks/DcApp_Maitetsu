package dc.maitetsu.service;

import dc.maitetsu.models.ArticleDetail;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @since 2017-04-21
 */
enum CommentDeleteService {
  getInstance;

  private static final String COMMENT_WRITE_URL = "http://m.dcinside.com/_option_write.php";


  boolean delete(Map<String, String> loginCookie,
                 String userAgent,
                 ArticleDetail articleDetail,
                 String deleteCode) throws IOException, ParseException {

    Document result = Jsoup.connect(COMMENT_WRITE_URL).cookies(loginCookie)
            .userAgent(userAgent)
            .header("Origin", "http://m.dcinside.com")
            .header("Referer", articleDetail.getUrl())
            .header("X-Requested-With", "XMLHttpRequest")
            .header("Accept", "*/*")
            .header("Accept-Encoding", "gzip, deflate")
            .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
            .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
            .data(cddSerialize(articleDetail.getCommentDeleteData(), deleteCode))
            .ignoreContentType(true)
            .timeout(10000)
            .post();


    String msg = result.body().text().trim();
    return msg.equals("1");
  }


  private Map<String, String> cddSerialize(ArticleDetail.CommentDeleteData cdd, String deleteCode) throws IOException, ParseException {
    Map<String, String> result = new HashMap<>();
    result.put("id", cdd.getId());
    result.put("no", cdd.getNo());
    result.put("iNo", deleteCode);
    result.put("user_no", cdd.getUser_no());
    result.put("board_id", cdd.getBoard_id());
    result.put("best_chk", cdd.getBest_chk());
//    result.put("best_comno", cdd.getBest_comno());
//    result.put("best_comid", cdd.getBest_comid());
    result.put("mode", cdd.getMode());

    return result;
  }



}

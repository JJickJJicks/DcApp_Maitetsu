package dc.maitetsu.service;

import dc.maitetsu.models.ArticleDetail;
import dc.maitetsu.models.DcConPackage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @since 2017-04-21
 */
enum CommentWriteService {
  getInstance;

  private static final String COMMENT_WRITE_URL = "http://m.dcinside.com/_option_write.php";
  private static final String COMMENT_ACCESS_TOKEN_URL = "http://m.dcinside.com/_access_token.php";
  private static final JSONParser jsonParser = new JSONParser();

  boolean write(Map<String, String> loginCookie,
                String userAgent,
                ArticleDetail articleDetail,
                String articleUrl,
                String comment) throws IOException, ParseException {

    Document result = Jsoup.connect(COMMENT_WRITE_URL).cookies(loginCookie)
                      .userAgent(userAgent)
                      .header("Origin", "http://m.dcinside.com")
                      .header("Referer", articleUrl)
                      .header("X-Requested-With", "XMLHttpRequest")
                      .header("Accept", "*/*")
                      .header("Accept-Encoding", "gzip, deflate")
                      .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
                      .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                      .data(cwdSerailize(articleDetail.getCommentWriteData(), comment, articleUrl, userAgent))
                      .timeout(10000)
                      .ignoreContentType(true)
                      .post();

    JSONObject jsonObject = (JSONObject) jsonParser.parse(result.body().text());
    String msg = (String) jsonObject.get("msg");
    return msg.equals("1");
  }

  boolean writeDcCon(Map<String, String> loginCookie, String userAgent, ArticleDetail articleDetail,
                     String articleUrl,
                     DcConPackage.DcCon dcCon) throws IOException, ParseException {
    String comment = "[[dccon:" + dcCon.getDccon_package() + "|" + dcCon.getDccon_detail() + "]]";

      Map<String, String> cwdData = cwdSerailize(articleDetail.getCommentWriteData(), comment, articleDetail.getUrl(), userAgent);
      cwdData.put("click_dccon", "1");
      cwdData.put("comment_memo2", "");

    Document result = Jsoup.connect(COMMENT_WRITE_URL).cookies(loginCookie)
            .userAgent(userAgent)
            .header("Origin", "http://m.dcinside.com")
            .header("Referer", articleUrl)
            .header("X-Requested-With", "XMLHttpRequest")
            .header("Accept", "*/*")
            .header("Accept-Encoding", "gzip, deflate")
            .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
            .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
            .data(cwdData)
            .timeout(10000)
            .ignoreContentType(true)
            .post();

    JSONObject jsonObject = (JSONObject) jsonParser.parse(result.body().text());
    String msg = (String) jsonObject.get("msg");
    return msg.equals("1");
  }





  private Map<String, String> cwdSerailize(ArticleDetail.CommentWriteData cwd, String comment, String articleUrl, String userAgent) throws IOException, ParseException {
    Map<String, String> data = new HashMap<>();
    data.put("comment_memo", comment);
    data.put("mode", cwd.getMode());
    data.put("voice_file",cwd.getVoice_file());
    data.put("no", cwd.getNo());
    data.put("id", cwd.getId());
    data.put("board_id", cwd.getBoard_id());
    data.put("user_no", cwd.getUser_no());
    data.put("ko_name", cwd.getKo_name());
    data.put("subject", cwd.getSubject());
    data.put("board_name", cwd.getBoard_name());
    data.put("date_time", URLEncoder.encode(cwd.getDate_time(), "UTF-8"));
    data.put("ip", cwd.getIp());
    data.put("best_chk", "");
    data.put("userToken", cwd.getUserToken());
    data.put("rand_code", "");
    data.put("con_key", getAccessToken(articleUrl, userAgent));
    return data;
  }


  // 댓글을 쓰기 위한 access token을 얻어오는 메소드.
  private String getAccessToken(String articleUrl, String userAgent) throws IOException, ParseException {
    Document doc = Jsoup.connect(COMMENT_ACCESS_TOKEN_URL)
            .userAgent(userAgent)
            .header("Referer", articleUrl)
            .header("Origin", "http://m.dcinside.com")
            .header("X-Requested-With", "XMLHttpRequest")
            .header("Accept", "*/*")
            .header("Accept-Encoding", "gzip, deflate")
            .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
            .data("token_verify", "com_submit")
            .timeout(10000)
            .ignoreContentType(true)
            .post();

    JSONObject jsonObject = (JSONObject) jsonParser.parse(doc.body().text());
    return (String) jsonObject.get("data");
  }

}

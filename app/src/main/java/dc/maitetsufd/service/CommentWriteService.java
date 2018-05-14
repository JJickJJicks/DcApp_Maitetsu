package dc.maitetsufd.service;

import dc.maitetsufd.models.ArticleDetail;
import dc.maitetsufd.models.DcConPackage;
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

  // 댓글
  boolean write(Map<String, String> loginCookie, String userAgent, ArticleDetail articleDetail, String articleUrl, String comment) throws IOException, ParseException, IllegalAccessException {

    Document result = Jsoup.connect(COMMENT_WRITE_URL).cookies(loginCookie)
                      .userAgent(userAgent)
                      .header("Origin", "http://m.dcinside.com")
                      .referrer(articleUrl)
                      .header("X-Requested-With", "XMLHttpRequest")
                      .data(cwdSerailize(articleDetail.getCommentWriteData(), comment, articleUrl, userAgent))
                      .timeout(10000)
                      .ignoreContentType(true)
                      .post();

    JSONObject jsonObject = (JSONObject) jsonParser.parse(result.body().text());
    String msg = (String) jsonObject.get("msg");
    if(msg.equals("44")) throw new IllegalAccessException((String) jsonObject.get("data"));
    return msg.equals("1");
  }

  // 디시콘 댓글
  boolean writeDcCon(Map<String, String> loginCookie, String userAgent, ArticleDetail articleDetail,
                     String articleUrl,
                     DcConPackage.DcCon dcCon) throws IOException, ParseException, IllegalAccessException {
    String comment = "[[dccon:" + dcCon.getDccon_package() + "|" + dcCon.getDccon_detail() + "]]";

      Map<String, String> cwdData = cwdSerailize(articleDetail.getCommentWriteData(), comment, articleDetail.getUrl(), userAgent);
      cwdData.put("click_dccon", "1");
      cwdData.put("comment_memo2", "");

      try {
        Document result = Jsoup.connect(COMMENT_WRITE_URL)
                              .cookies(loginCookie)
                              .userAgent(userAgent)
                              .header("Origin", "http://m.dcinside.com")
                              .referrer(articleUrl)
                              .header("X-Requested-With", "XMLHttpRequest")
                              .data(cwdData)
                              .timeout(10000)
                              .ignoreContentType(true)
                              .post();

        JSONObject jsonObject = (JSONObject) jsonParser.parse(result.body().text());
        String msg = (String) jsonObject.get("msg");
        if (msg.equals("44")) throw new IllegalAccessException((String) jsonObject.get("data"));
        return msg.equals("1");

      } catch (IllegalAccessException ie) {
        throw ie;

      } catch(Exception e) {
        return true;
      }
  }





  private Map<String, String> cwdSerailize(ArticleDetail.CommentWriteData cwd, String comment, String articleUrl, String userAgent) throws IOException, ParseException {
    Map<String, String> data = new HashMap<>();
    data.put("comment_memo", comment);
    data.put("mode", "comment");// cwd.getMode());
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
                        .referrer(articleUrl)
                        .header("Origin", "http://m.dcinside.com")
                        .header("X-Requested-With", "XMLHttpRequest")
                        .data("token_verify", "com_submit")
                        .timeout(10000)
                        .ignoreContentType(true)
                        .post();

    JSONObject jsonObject = (JSONObject) jsonParser.parse(doc.body().text());
    return (String) jsonObject.get("data");
  }

}

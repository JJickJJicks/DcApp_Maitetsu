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
public enum CommentWriteService {
  getInstance;

  private static final String COMMENT_WRITE_URL = "http://m.dcinside.com/ajax/comment-write";
  private static final String GET_DCCON_URL = "http://m.dcinside.com/dccon/dccon_chk";
  private static final JSONParser jsonParser = new JSONParser();

  // 댓글
  public boolean write(Map<String, String> loginCookie, String userAgent, ArticleDetail articleDetail, String comment, String detailIdx) throws IOException, ParseException, IllegalAccessException {

    Document result = Jsoup.connect(COMMENT_WRITE_URL).cookies(loginCookie)
                      .userAgent(userAgent)
                      .header("Origin", "http://m.dcinside.com")
                      .referrer(articleDetail.getUrl())
                      .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                      .header("X-Requested-With", "XMLHttpRequest")
                      .header("X-CSRF-TOKEN", articleDetail.getCommentWriteData().getCsrfToken())
                      .data(cwdSerailize(articleDetail.getCommentWriteData(), detailIdx, comment, userAgent, loginCookie))
                      .timeout(10000)
                      .ignoreContentType(true)
                      .post();

	String responseText = result.body().text();
	
	if (responseText.trim().isEmpty()) // 비정상 응답인 경우 재시도
		return write (loginCookie, userAgent, articleDetail, comment, detailIdx);

    JSONObject jsonObject = (JSONObject) jsonParser.parse(responseText);
    if(jsonObject.get("result").equals(false)) throw new IllegalAccessException((String) jsonObject.get("data"));
    return jsonObject.get("result").equals(true);
  }

  // 디시콘 댓글
  public boolean writeDcCon(Map<String, String> loginCookie, String userAgent, ArticleDetail articleDetail,
                     DcConPackage.DcCon dcCon) throws IllegalAccessException {
      try {
        Document result = Jsoup.connect(GET_DCCON_URL)
                              .cookies(loginCookie)
                              .userAgent(userAgent)
                              .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                              .header("Host", "m.dcinside.com")
                              .header("Origin", "http://m.dcinside.com")
                              .referrer(articleDetail.getUrl())
                              .header("X-Requested-With", "XMLHttpRequest")
                              .header("X-CSRF-TOKEN", articleDetail.getCommentWriteData().getCsrfToken())
                              .data("detail_idx", dcCon.getDccon_detail())
                              .data("package_idx", dcCon.getDccon_package())
                              .timeout(10000)
                              .ignoreContentType(true)
                              .post();

		String responseText = result.body().text();
	
		if (responseText.trim().isEmpty()) // 비정상 응답인 경우 재시도
			return writeDcCon(loginCookie, userAgent, articleDetail, dcCon);
					  
							  
        JSONObject jsonObject = (JSONObject) jsonParser.parse(responseText);
        if (!jsonObject.get("result").equals("ok")) return false;

        String src = jsonObject.get("img_src").toString();
        String alt = jsonObject.get("alt").toString();
        String tag = "<img src='" + src + "' class='written_dccon' alt='" + alt + "' conalt='" + alt + "' title='" + alt + "'>";
        return write(loginCookie, userAgent, articleDetail, tag, dcCon.getDccon_detail());

      } catch (IllegalAccessException ie) {
        throw ie;

      } catch(Exception e) {
        return true;
      }

  }


  private Map<String, String> cwdSerailize(ArticleDetail.CommentWriteData cwd, String detailIdx, String comment, String userAgent, Map<String, String> cookies) throws IOException, ParseException {
    Map<String, String> data = new HashMap<>();
    data.put("comment_memo", comment);
    data.put("comment_nick", "");
    data.put("comment_pw", "");
    data.put("mode", "com_write");
    data.put("comment_no", "");
    data.put("no", cwd.getNo());
    data.put("id", cwd.getId());
    data.put("board_id", cwd.getBoard_id());
    data.put("reple_id", "");
    data.put("subject", cwd.getSubject());
    data.put("best_chk", "");
    data.put("cpage", cwd.getCpage());
    if (!detailIdx.isEmpty()) {
      data.put("detail_idx", detailIdx);
    }
    data.put("reple_id", "");
    data.put("con_key", AccessTokenService.getInstance.getAccessToken("com_submit", "", cwd.getCsrfToken(), userAgent, cookies));

    data.put(cwd.getHoneyKey(), cwd.getHoneyValue());
    return data;
  }

}

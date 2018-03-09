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
enum RecommendService {
  getInstance;

  private static final String RECOMMEND_URL = "http://m.dcinside.com/_recommend_join.php";
  private static final JSONParser jsonParser = new JSONParser();

  // 게시물 개념글 추천하는 메소드
  boolean recommend(Map<String, String> loginCookie, ArticleDetail articleDetail, String userAgent) throws IOException, ParseException {
    ArticleDetail.RecommendData recommendData = articleDetail.getRecommendData();
    Map<String, String> recommendCookie = new HashMap<>(loginCookie);
    recommendCookie.put(recommendData.getGall_id() + "_recomPrev_" + recommendData.getNo(), "done");

    Document result = Jsoup.connect(RECOMMEND_URL)
            .userAgent(userAgent)
            .cookies(recommendCookie)
            .header("Origin", "http://m.dcinside.com")
            .header("Referer", articleDetail.getUrl())
            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
            .header("Accept-Encoding", "gzip, deflate")
            .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
            .data(recommendDataSerialize(recommendData))
            .ignoreContentType(true)
            .post();

    JSONObject jsonObject = (JSONObject) jsonParser.parse(result.body().text());
    String msg = (String) jsonObject.get("msg");
    return msg.equals("1");
  }


  private Map<String, String> recommendDataSerialize(ArticleDetail.RecommendData recommendData) {
    Map<String, String> data = new HashMap<>();
    data.put("no", recommendData.getNo());
    data.put("gall_id", recommendData.getGall_id());
    data.put("ko_name", recommendData.getKo_name());
    data.put("category_no", recommendData.getCategory_no());
    data.put("gserver", recommendData.getGserver());
    data.put("ip", recommendData.getIp());
    data.put("gno", recommendData.getGno());
    return data;
  }


}

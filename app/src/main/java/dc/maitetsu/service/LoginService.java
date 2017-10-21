package dc.maitetsu.service;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @since 2017-04-21
 */

enum LoginService {
  getInstance;

  private static final String MOBILE_LOGIN_ACCESS_TOKEN_URL = "http://m.dcinside.com/_access_token.php";
  private static final String MOBILE_LOGIN_FORM_URL = "http://m.dcinside.com/login.php?r_url=m.dcinside.com%2Findex.php";
  private static final String MOBILE_LOGIN_URL = "https://dcid.dcinside.com/join/mobile_login_ok.php";
  private static final JSONParser jsonParser = new JSONParser();

  /**
   * 모바일 웹에 로그인해 로그인 데이터를 얻어오는 메소드.
   *
   * @param id        유저 아이디
   * @param pw        유저 비밀번호
   * @param userAgent 모바일 기기의 userAgent.
   * @return 로그인 데이터
   */
  Connection.Response login(String id, String pw, String userAgent) throws IOException, ParseException, InterruptedException, IllegalAccessException {
    Connection.Response response;
    int i=0;

    do {
      if(i > 3) throw new IllegalAccessException("\n로그인 정보를 확인해보세요.");
      TimeUnit.MILLISECONDS.sleep(300L);
      response = Jsoup.connect(MOBILE_LOGIN_URL)
                      .userAgent(userAgent)
                      .data(getLoginParams(id, pw, userAgent))
                      .header("Origin", "http://m.dcinside.com")
                      .header("Referer", "http://m.dcinside.com/login.php?r_url=m.dcinside.com%2Findex.php")
                      .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                      .header("Content-Type", "application/x-www-form-urlencoded")
                      .header("Accept-Encoding", "gzip, deflate")
                      .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
                      .timeout(3000)
                      .method(Connection.Method.POST)
                      .execute();
      i++;
    } while(response.cookies().get("mc_enc") == null);

    return response;
  }


  // 로그인에 필요한 값들을 Map으로 만들어주는 메소드
  private Map<String, String> getLoginParams(String id, String pw, String userAgent) throws IOException, ParseException {
    String html = Jsoup.connect(MOBILE_LOGIN_FORM_URL).get().html();
    Document doc = Jsoup.parse(html);
    Element loginform = doc.getElementById("login_process");
    Elements inputElements = loginform.getElementsByTag("input");
    Map<String, String> params = getConKey(inputElements, userAgent);

    params.put("user_id", id);
    params.put("user_pw", pw);
    params.put("id_chk", "on");
    return params;
  }


  // 로그인 폼에서 con_key를 구해준다.
  private Map<String, String> getConKey(Elements elements, String userAgent) throws IOException, ParseException {
    Map<String, String> params = new HashMap<>();

    for (Element inputElement : elements) {
      String key = inputElement.attr("name");
      String value = inputElement.attr("value");
      if(key.equals("con_key")) {
        value = conKeyToAccessToken(value, userAgent);
      }
      params.put(key, value);
    }

    return params;
  }

  // con_key를 이용해 access token을 얻어오는 메소드.
  private String conKeyToAccessToken(String conKey, String userAgent) throws IOException, ParseException {
    Document doc = Jsoup.connect(MOBILE_LOGIN_ACCESS_TOKEN_URL)
            .userAgent(userAgent)
            .header("Referer", "http://m.dcinside.com/login.php?r_url=m.dcinside.com%2Findex.php")
            .header("Origin", "http://m.dcinside.com")
            .header("X-Requested-With", "XMLHttpRequest")
            .header("Accept", "*/*")
            .header("Accept-Encoding", "gzip, deflate")
            .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
            .data("token_verify", "login")
            .data("con_key", conKey)
            .ignoreContentType(true)
            .post();

    JSONObject jsonObject = (JSONObject) jsonParser.parse(doc.body().text());
    return (String) jsonObject.get("data");
  }

}

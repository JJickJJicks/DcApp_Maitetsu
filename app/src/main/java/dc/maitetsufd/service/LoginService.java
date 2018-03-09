package dc.maitetsufd.service;

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

public enum LoginService {
  getInstance;

  private static final String MOBILE_LOGIN_ACCESS_TOKEN_URL = "http://m.dcinside.com/_access_token.php";
  private static final String MOBILE_LOGIN_FORM_URL = "http://m.dcinside.com/login.php?r_url=m.dcinside.com%2Findex.php";
  private static final String MOBILE_LOGIN_URL = "https://dcid.dcinside.com/join/mobile_login_ok.php";
  private static final JSONParser jsonParser = new JSONParser();
  private static Map<String, String> cookies = new HashMap<>();

  /**
   * 모바일 웹에 로그인해 로그인 데이터를 얻어오는 메소드.
   *
   * @param id        유저 아이디
   * @param pw        유저 비밀번호
   * @param userAgent 모바일 기기의 userAgent.
   * @return 로그인 데이터
   */
  public Map<String, String> login(String id, String pw, String userAgent) throws IOException, ParseException, InterruptedException, IllegalAccessException {
    int i = 0;
    Connection.Response response = null;

    do {
      if (i > 3) throw new IllegalAccessException("\n로그인 정보를 확인해보세요.");
      TimeUnit.MILLISECONDS.sleep(100L);
        Map<String, String> data = getLoginParams(id, pw, userAgent);
      try {
        response = Jsoup.connect(MOBILE_LOGIN_URL)
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .userAgent(userAgent)
                .data(data)
//                .header("Host", ".dcinside.com")
//                .header("Origin", ".dcinside.com")
                .header("Referer", "http://m.dcinside.com/login.php?r_url=m.dcinside.com%2Findex.php")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept-Encoding", "gzip, deflate")
                .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
                .cookies(LoginService.cookies)
                .timeout(3000)
                .method(Connection.Method.POST)
                .execute();
        LoginService.cookies.putAll(response.cookies());

        // 리다이렉트
        response = Jsoup.connect("http://m.dcinside.com/login.php?r_url=m.dcinside.com%252Findex.php&mode=&rucode=1")
                .userAgent(userAgent)
                .cookies(LoginService.cookies)
                .timeout(3000)
                .method(Connection.Method.GET)
                .execute();
        LoginService.cookies.putAll(response.cookies());

      } catch (Exception e) {
      }
      i++;
    } while (LoginService.cookies.get("mc_enc") == null);

    return LoginService.cookies;
  }


  // 로그인에 필요한 값들을 Map으로 만들어주는 메소드
  private Map<String, String> getLoginParams(String id, String pw, String userAgent) throws IOException, ParseException {
    Connection.Response res = Jsoup.connect(MOBILE_LOGIN_FORM_URL)
            .userAgent(userAgent)
            .ignoreContentType(true)
            .ignoreHttpErrors(true)
//            .header("Host", ".dcinside.com")
//            .header("Origin", ".dcinside.com")
            .header("Referer", "http://m.dcinside.com/")
            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
            .header("Accept-Encoding", "gzip, deflate")
            .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
            .header("Pragma", "no-cache")
            .header("Cache-Control", "no-cache")
            .header("Connection", "keep-alive")
            .method(Connection.Method.GET)
            .execute();
    LoginService.cookies.putAll(res.cookies());

    Document doc = res.parse();
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
      if (key.equals("con_key")) {
        value = conKeyToAccessToken(value, userAgent);
      }
      params.put(key, value);
    }
    return params;
  }

  // con_key를 이용해 access token을 얻어오는 메소드.
  private String conKeyToAccessToken(String conKey, String userAgent) throws IOException, ParseException {
    Connection.Response res = Jsoup.connect(MOBILE_LOGIN_ACCESS_TOKEN_URL)
            .userAgent(userAgent)
            .ignoreContentType(true)
            .ignoreHttpErrors(true)
//            .header("Host", ".dcinside.com")
//            .header("Origin", ".dcinside.com")
            .header("Referer", "http://m.dcinside.com/login.php?r_url=m.dcinside.com%2Findex.php")
            .header("X-Requested-With", "XMLHttpRequest")
            .header("Accept", "*/*")
            .header("Accept-Encoding", "gzip, deflate")
            .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
            .header("Connection", "keep-alive")
            .data("token_verify", "login")
            .data("con_key", conKey)
            .cookies(LoginService.cookies)
            .ignoreContentType(true)
            .method(Connection.Method.POST)
            .execute();
    LoginService.cookies.putAll(res.cookies());

    Document doc = res.parse();
    JSONObject jsonObject = (JSONObject) jsonParser.parse(doc.body().text());

    return (String) jsonObject.get("data");
  }

}

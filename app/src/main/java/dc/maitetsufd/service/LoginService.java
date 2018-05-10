package dc.maitetsufd.service;

import android.util.Log;
import lombok.val;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author John
 * @since 2018-03-14
 */
public enum LoginService {
  getInstance;

  private static final String MOBILE_INDEX = "http://m.dcinside.com";
  private static final String MOBILE_LOGIN_ACCESS_TOKEN_URL = "http://m.dcinside.com/_access_token.php";
  private static final String MOBILE_LOGIN_FORM_URL = "http://m.dcinside.com/login.php?r_url=m.dcinside.com%2Findex.php";
  private static final String MOBILE_LOGIN_URL = "https://dcid.dcinside.com/join/mobile_login_ok.php";
  private static final JSONParser jsonParser = new JSONParser();

  /**
   * 로그인하는 함수
   *
   * @param id        dcinside 아이디
   * @param pw        dcinside 비밀번호
   * @param userAgent 로그인 시도할 user-agent
   * @return
   * @throws IOException
   */
  public Map<String, String> login(String id, String pw, String userAgent) throws Exception {
    val cookies = new HashMap<String, String>();
    cookies.put("user-agent", userAgent);

    val loginData = getLoginData(id, pw, userAgent, cookies);

    // 로그인 시도 후 로그인 쿠키 검사
    for (int i = 0; i < 3; i++) {
      val response = Jsoup.connect(MOBILE_LOGIN_URL)
              .data(loginData)
              .followRedirects(true)
              .ignoreContentType(true)
              .ignoreHttpErrors(true)
              .userAgent(userAgent)
              .referrer(MOBILE_LOGIN_FORM_URL)
              .cookies(cookies)
              .method(Connection.Method.POST)
              .execute();
      val loginCookies = response.cookies();
      System.out.println(loginCookies);

      if (loginCookies.get("mc_enc") != null) {
        loginCookies.put("userAgent", userAgent);
        return response.cookies();
      }
    }

    throw new Exception("로그인 정보를 확인하세요");
  }

  /**
   * 로그인에 필요한 데이터를 얻어오는 메소드
   *
   * @param id
   * @param pw
   * @param userAgent
   * @param cookies
   * @return
   * @throws IOException
   */
  private Map<String, String> getLoginData(String id, String pw, String userAgent, Map<String, String> cookies) throws IOException {
    val firstURL = loginRedirect(MOBILE_LOGIN_FORM_URL, userAgent, cookies, MOBILE_LOGIN_FORM_URL);
    loginRedirect(firstURL, userAgent, cookies, MOBILE_LOGIN_FORM_URL);

    val loginFormResponse = Jsoup.connect(MOBILE_LOGIN_FORM_URL)
            .userAgent(userAgent)
            .followRedirects(false)
            .ignoreContentType(true)
            .ignoreHttpErrors(true)
            .referrer(MOBILE_LOGIN_FORM_URL)
            .method(Connection.Method.GET)
            .cookies(cookies)
            .execute();

    // 로그인 페이지 쿠키 처리
    cookies.putAll(loginFormResponse.cookies());

    val conKeyData = getConKey(loginFormResponse.parse(), userAgent, cookies);
    conKeyData.put("user_id", id);
    conKeyData.put("user_pw", pw);
    conKeyData.put("id_chk", "on");
    conKeyData.put("r_url", "m.dcinside.com%2Findex.php");
    return conKeyData;
  }

  /**
   * 로그인 페이지에서 con_key를 얻어오는 메소드
   *
   * @param document
   * @param userAgent
   * @param cookies
   * @return
   * @throws IOException
   */
  private Map<String, String> getConKey(Document document, String userAgent, Map<String, String> cookies) throws IOException {
    val loginData = new HashMap<String, String>();
    val conKey = document.getElementById("login_process")
            .select("input[name=con_key]")
            .first().val();
    val accessToken = getAccessToken(conKey, userAgent, cookies);

    loginData.put("con_key", accessToken);
    return loginData;
  }

  /**
   * 로그인 페이지의 con_key를 이용하여 access_token을 얻어오는 메소드
   *
   * @param conKey
   * @param userAgent
   * @param cookies
   * @return
   * @throws IOException
   */
  private String getAccessToken(String conKey, String userAgent, Map<String, String> cookies) throws IOException {
    val accessTokenResponse = Jsoup.connect(MOBILE_LOGIN_ACCESS_TOKEN_URL)
            .userAgent(userAgent)
            .ignoreContentType(true)
            .ignoreHttpErrors(true)
            .referrer(MOBILE_LOGIN_FORM_URL)
            .header("X-Requested-With", "XMLHttpRequest")
            .data("token_verify", "login")
            .data("con_key", conKey)
            .cookies(cookies)
            .method(Connection.Method.POST)
            .execute();

    // 로그인 페이지 쿠키 처리
    cookies.putAll(accessTokenResponse.cookies());

    // 로그인 엑세스 토큰 리턴
    try {
      val jsonObject = (JSONObject) jsonParser.parse(accessTokenResponse.body());
      return (String) jsonObject.get("data");
    } catch (ParseException e) {
      e.printStackTrace();
      return "";
    }
  }

  private String loginRedirect(String URL, String userAgent, Map<String, String> cookies, String previousURL) throws IOException {
    URL = URLDecoder.decode(URL, "UTF-8");

    val response = Jsoup.connect(URL)
            .userAgent(userAgent)
            .followRedirects(false)
            .ignoreContentType(true)
            .ignoreHttpErrors(true)
            .referrer(previousURL)
            .timeout(3000)
            .execute();
    cookies.putAll(response.cookies());

    return response.header("location");
  }

}



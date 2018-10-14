package dc.maitetsufd.service;

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
import java.util.concurrent.TimeUnit;

/**
 * @author John
 * @since 2018-03-14
 */
public enum LoginService {
  getInstance;
//  private static final String MOBILE_LOGIN_ACCESS_TOKEN_URL = "http://m.dcinside.com/_access_token.php";
  private static final String MOBILE_LOGIN_FORM_URL = "http://m.dcinside.com/auth/login?r_url=http%3A%2F%2Fm.dcinside.com";
//  private static final String MOBILE_LOGIN_FORM_URL = "http://m.dcinside.com/login.php?r_url=m.dcinside.com%2Findex.php";
  private static final String MOBILE_LOGIN_URL = "https://dcid.dcinside.com/join/mobile_login_ok_new.php";
//  private static final String MOBILE_LOGIN_URL = "https://dcid.dcinside.com/join/mobile_login_ok.php";

  /**
   * 로그인하는 함수
   *
   * @param id        dcinside 아이디
   * @param pw        dcinside 비밀번호
   * @param userAgent 로그인 시도할 user-agent
   * @return 로그인 쿠키
   * @throws Exception 로그인 실패 예외
   */
  public Map<String, String> login(String id, String pw, String userAgent) throws Exception {
    val cookies = new HashMap<String, String>();

    // 로그인 시도 후 로그인 쿠키 검사
    val loginData = getLoginData(id, pw, userAgent, cookies);
    Connection.Response response = Jsoup.connect(MOBILE_LOGIN_URL)
                                        .data(loginData)
                                        .followRedirects(true)
                                        .ignoreContentType(true)
                                        .ignoreHttpErrors(true)
                                        .userAgent(userAgent)
                                        .header("Connection", "keep-alive")
                                        .header("Host", "dcid.dcinside.com")
                                        .header("Origin", "http://m.dcinside.com")
                                        .header("Content-Type", "application/x-www-form-urlencoded")
                                        .referrer(MOBILE_LOGIN_FORM_URL)
                                        .cookies(cookies)
                                        .method(Connection.Method.POST)
                                        .execute();
    cookies.putAll(response.cookies());

    val body = response.body();
    if (body.contains("alert")) { // 로그인시 경고 메시지
      throw new IllegalAccessException(body.split("alert\\('")[1].split("'\\)")[0]);
    }

    // SSO 로그인
//    Connection.Response ssoResponse = Jsoup.connect(response.header("location"))
//                                          .followRedirects(true)
//                                          .ignoreContentType(true)
//                                          .ignoreHttpErrors(true)
//                                          .userAgent(userAgent)
//                                          .header("Host", ".dcinside.com")
//                                          .header("Origin", ".dcinside.com")
//                                          .referrer(MOBILE_LOGIN_FORM_URL)
//                                          .cookies(cookies)
//                                          .method(Connection.Method.GET)
//                                          .execute();
//    cookies.putAll(ssoResponse.cookies());


    // 2차 로그인
    Connection.Response response2 = Jsoup.connect(MOBILE_LOGIN_URL)
                                        .data(loginData)
                                        .followRedirects(true)
                                        .ignoreContentType(true)
                                        .ignoreHttpErrors(true)
                                        .userAgent(userAgent)
                                        .header("Connection", "keep-alive")
                                        .header("Host", "dcid.dcinside.com")
                                        .header("Origin", "http://m.dcinside.com")
                                        .header("Content-Type", "application/x-www-form-urlencoded")
                                        .referrer(MOBILE_LOGIN_FORM_URL)
                                        .cookies(cookies)
                                        .method(Connection.Method.POST)
                                        .execute();
    cookies.putAll(response2.cookies());

    // user agent 삽입
    cookies.put("userAgent", userAgent);

    return cookies;
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
   * @throws IllegalAccessException
   */
  private Map<String, String> getLoginData(String id, String pw, String userAgent, Map<String, String> cookies) throws IOException, IllegalAccessException {
//    val firstURL = loginRedirect(MOBILE_LOGIN_FORM_URL, userAgent, cookies, MOBILE_LOGIN_FORM_URL);
//    loginRedirect(firstURL, userAgent, cookies, MOBILE_LOGIN_FORM_URL);

    try {
      val loginFormResponse = Jsoup.connect(MOBILE_LOGIN_FORM_URL)
                                    .userAgent(userAgent)
                                    .followRedirects(false)
                                    .ignoreContentType(true)
                                    .ignoreHttpErrors(true)
                                    .referrer(MOBILE_LOGIN_FORM_URL)
                                    .header("Host", "m.dcinside.com")
                                    .method(Connection.Method.GET)
                                    .cookies(cookies)
                                    .execute();
      // 로그인 페이지 쿠키 처리
      cookies.putAll(loginFormResponse.cookies());

      val conKeyData = getConKey(loginFormResponse.parse(), userAgent, cookies);
      conKeyData.put("user_id", id);
      conKeyData.put("user_pw", pw);
      conKeyData.put("id_chk", "on");
      conKeyData.put("r_url", "http://m.dcinside.com");
      return conKeyData;
    } catch (IllegalAccessException ie) {
      throw ie;

    } catch (Exception e) {
      throw e;

    }

  }

  /**
   * 로그인 페이지에서 con_key를 얻어오는 메소드
   *
   * @param document
   * @param userAgent
   * @param cookies
   * @return
   * @throws IllegalAccessException
   */
  private Map<String, String> getConKey(Document document, String userAgent, Map<String, String> cookies) throws IllegalAccessException {
    val loginData = new HashMap<String, String>();
    val conKey = document.getElementById("login_process")
                          .select("input[name=con_key]")
                          .first().val();
    val csrfToken = document.select("meta[name=csrf-token]").attr("content");
    val accessToken = AccessTokenService.getInstance.getAccessToken("dc_login", conKey, csrfToken, userAgent, cookies);
    if (accessToken.isEmpty()) throw new IllegalAccessException("로그인 키 획득에 실패했습니다.");

    loginData.put("con_key", accessToken);
    return loginData;
  }

}



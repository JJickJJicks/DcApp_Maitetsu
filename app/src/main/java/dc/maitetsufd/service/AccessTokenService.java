package dc.maitetsufd.service;

import lombok.val;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.Map;

/**
 * @author Park Hyo Jun
 * @since 2018-10-01
 */
public enum AccessTokenService {
  getInstance;

  private static final JSONParser jsonParser = new JSONParser();
  private static final String MOBILE_LOGIN_ACCESS_TOKEN_URL = "http://m.dcinside.com/ajax/access";

  /**
   * 로그인 페이지의 con_key를 이용하여 access_token을 얻어오는 메소드
   *
   * @param conKey
   * @param userAgent
   * @param cookies
   * @return
   */
  public String getAccessToken(String verify, String conKey, String csrfToken, String userAgent, Map<String, String> cookies) {
    Connection.Response accessTokenResponse;
    try {
      accessTokenResponse = Jsoup.connect(MOBILE_LOGIN_ACCESS_TOKEN_URL)
              .userAgent(userAgent)
              .followRedirects(false)
              .ignoreContentType(true)
              .ignoreHttpErrors(true)
              .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
              .header("X-Requested-With", "XMLHttpRequest")
              .header("X-CSRF-TOKEN", csrfToken)
              .header("Host", "m.dcinside.com")
              .header("Origin", "http://m.dcinside.com")
              .data("token_verify", verify)
              .data("con_key", conKey)
              .cookies(cookies)
              .method(Connection.Method.POST)
              .execute();
      // 로그인 페이지 쿠키 처리
      cookies.putAll(accessTokenResponse.cookies());

      // 로그인 엑세스 토큰 리턴
      try {
        val jsonObject = (JSONObject) jsonParser.parse(accessTokenResponse.body());
        return (String) jsonObject.get("Block_key");

      } catch (ParseException e) {
        return "";

      }

    } catch (Exception e) {
      return getAccessToken(verify, conKey, csrfToken, userAgent, cookies);
    }
  }
}

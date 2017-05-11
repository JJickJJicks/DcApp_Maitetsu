package dc.maitetsu.data;

import android.content.Context;
import android.content.SharedPreferences;
import dc.maitetsu.models.DcConPackage;
import dc.maitetsu.models.GalleryInfo;
import dc.maitetsu.models.UserInfo;

import java.util.*;

import static android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * 사용자 정보를 저장하고 로드 할 수 있는 메소드를 지원하는 클래스
 *
 * @author Park Hyo Jun
 * @since 2017-04-24
 */
public class CurrentDataManager {
  private static final String MY_GALL_LIST = "MY_GALL_LIST";
  private static final String LOGIN_COOKIE = "LOGIN_COOKIE";
  private static final String FILTER_USER_LIST = "FILTER_USER_LIST";
  private static final String DCCON_LIST = "DCCON_LIST";
  private static final String DCCON = "DCCON";
  private static final String PACKAGE = "PACKAGE";
  private static final String SRC = "SRC";
  private static final String DETAIL = "DETAIL";
  private static final String NICKNAME = "NICKNAME";
  private static final String SIZE = "SIZE";
  private static final String USERTYPE = "USERTYPE";
  private static final String KEY = "KEY";
  private static final String VALUE = "VALUE";
  private static final String RECOMMEND = "RECOMMEND";

  private static CurrentData currentData;

  /**
   * 사용 정보를 저장하는 메소드
   *
   * @param context     the context
   */
  public static synchronized void save(Context context) {
    if(currentData == null) return;

    SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
    SharedPreferences.Editor editor = sharedPreferences.edit();

    editor.putString("user_id", currentData.getId());
    editor.putString("user_pw", currentData.getPw());
    editor.putBoolean("image_check", currentData.isImageCheck());
    editor.putBoolean("dccon_check", currentData.isDcconCheck());
    editor.putBoolean("telcom_filter", currentData.isTelcomFilter());
    editor.putBoolean("flow_filter", currentData.isFlowFilter());
    editor.putString("search_word", currentData.getSearchWord());
    editor.putLong("last_login", currentData.getLastLogin());
    editor.putBoolean("fast_login", currentData.isFastLogin());
    editor.putBoolean("article_close_vib", currentData.isArticleCloseVib());
    editor.putBoolean("article_tab_vib", currentData.isArticleTabVib());
    editor.putBoolean("dark_theme", currentData.isDarkTheme());
    editor.putBoolean("touch_image_open", currentData.isTouchImageOpen());
    editor.putBoolean("maru_viewer", currentData.isMaruViewer());
    editor.putBoolean("movie_ignore", currentData.isMovieIgnore());
    saveMyGallList(currentData, editor);
    saveFilterUserList(currentData, editor);
    saveDcconPackage(currentData, editor);
    saveRecommendMap(currentData, editor);

    // 로그인 쿠키 저장
    saveLoginCookie(currentData, editor);

    editor.apply();
  }

  private static void saveRecommendMap(CurrentData currentData, SharedPreferences.Editor editor) {
    int count = 0;
    Map<String, Long> recommendList = currentData.getRecommendList();
    Set<String> keySet = recommendList.keySet();
    long timeOut = 1000 * 60 * 60 * 24; // 1일

    for(String key : keySet) {
      Long time = recommendList.get(key);
      if(time != null
              && time + timeOut > System.currentTimeMillis()) {
        editor.putString(RECOMMEND + KEY + count, key);
        editor.putLong(RECOMMEND + VALUE + count, time);
        count++;
      }
    }
    editor.putInt(RECOMMEND + SIZE, count);
  }

  // 방문한 갤러리 리스트 저장
  private static void saveMyGallList(CurrentData currentData, SharedPreferences.Editor editor) {
    editor.putInt("my_gall_size", currentData.getMyGalleryList().size());
    for (int i = 0; i < currentData.getMyGalleryList().size(); i++) {
      editor.putString(MY_GALL_LIST + i + "name", currentData.getMyGalleryList().get(i).getGalleryName());
      editor.putString(MY_GALL_LIST + i + "code", currentData.getMyGalleryList().get(i).getGalleryCode());
    }
  }

  // 차단 유저 리스트 저장
  private static void saveFilterUserList(CurrentData currentData, SharedPreferences.Editor editor) {
    editor.putInt("filter_user_list_size", currentData.getFilterUserList().size());
    for (int i = 0; i < currentData.getFilterUserList().size(); i++) {
      String nickname = currentData.getFilterUserList().get(i).getNickname();
      String userType = currentData.getFilterUserList().get(i).getUserType().toString();
      editor.putString(FILTER_USER_LIST + i + NICKNAME, nickname);
      editor.putString(FILTER_USER_LIST + i + USERTYPE, userType);
    }
  }

  // 사용가능한 디시콘 리스트 저장
  private static void saveDcconPackage(CurrentData currentData, SharedPreferences.Editor editor) {
    editor.putInt("dccon_list_size", currentData.getDcConPackages().size());
    for (int i = 0; i < currentData.getDcConPackages().size(); i++) {
      DcConPackage dcConPackage = currentData.getDcConPackages().get(i);
      editor.putString(DCCON_LIST + PACKAGE + i, dcConPackage.getDccon_package());
      editor.putString(DCCON_LIST + PACKAGE + i + SRC, dcConPackage.getDccon_package_src());

      editor.putInt(DCCON_LIST + PACKAGE + i + SIZE, dcConPackage.getDcCons().size());
      for (int j = 0; j < dcConPackage.getDcCons().size(); j++) {
        DcConPackage.DcCon dcCon = dcConPackage.getDcCons().get(j);
        editor.putString(DCCON_LIST + PACKAGE + i + DCCON + j + PACKAGE, dcCon.getDccon_package());
        editor.putString(DCCON_LIST + PACKAGE + i + DCCON + j + SRC, dcCon.getDccon_src());
        editor.putString(DCCON_LIST + PACKAGE + i + DCCON + j + DETAIL, dcCon.getDccon_detail());
      }
    }
  }

  // 로그인 쿠키 저장
  private static void saveLoginCookie(CurrentData currentData, SharedPreferences.Editor editor) {
    editor.putInt("login_cookie_size", currentData.getLoginCookies().size());
    Set<String> keySet = currentData.getLoginCookies().keySet();
    int i = 0;
    for (String key : keySet) {
      editor.putString(LOGIN_COOKIE + i + KEY, key);
      editor.putString(LOGIN_COOKIE + i + VALUE, currentData.getLoginCookies().get(key));
      i++;
    }
  }







  /**
   * 현재 사용자 정보를 얻어오는 메소드
   *
   * @param context the context
   * @return the current data
   */
  public static synchronized CurrentData getInstance(Context context) {
    if(currentData == null) {
      currentData = load(context);
    }
    return currentData;
  }



  /**
   * 저장되어있는 사용 정보를 로드하는 메소드
   *
   * @param context the context
   * @return the current data
   */
  public static CurrentData load(Context context) {
    SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
    String id = sharedPreferences.getString("user_id", "");
    String pw = sharedPreferences.getString("user_pw", "");
    boolean imageCheck = sharedPreferences.getBoolean("image_check", false);
    boolean dcconCheck = sharedPreferences.getBoolean("dccon_check", false);
    boolean telcomCheck = sharedPreferences.getBoolean("telcom_filter", false);
    boolean flowCheck = sharedPreferences.getBoolean("flow_filter", false);
    long lastLogin = sharedPreferences.getLong("last_login", 0);
    boolean fastLogin = sharedPreferences.getBoolean("fast_login", true);
    boolean articleTabVib = sharedPreferences.getBoolean("article_tab_vib", true);
    boolean articleCloseVib = sharedPreferences.getBoolean("article_close_vib", true);
    boolean darkTheme = sharedPreferences.getBoolean("dark_theme", false);
    boolean touchImageOpen = sharedPreferences.getBoolean("touch_image_open", true);
    boolean maruViewer = sharedPreferences.getBoolean("maru_viewer", false);
    boolean movieIgnore = sharedPreferences.getBoolean("movie_ignore", false);

    String searchWord = sharedPreferences.getString("search_word", "");

    CurrentData loadCurrentData = new CurrentData();
    loadCurrentData.setId(id);
    loadCurrentData.setPw(pw);
    loadCurrentData.setImageCheck(imageCheck);
    loadCurrentData.setDcconCheck(dcconCheck);
    loadCurrentData.setTelcomFilter(telcomCheck);
    loadCurrentData.setFlowFilter(flowCheck);
    loadCurrentData.setLastLogin(lastLogin);
    loadCurrentData.setFastLogin(fastLogin);
    loadCurrentData.setArticleCloseVib(articleCloseVib);
    loadCurrentData.setArticleTabVib(articleTabVib);
    loadCurrentData.setDarkTheme(darkTheme);
    loadCurrentData.setSearchWord(searchWord);
    loadCurrentData.setTouchImageOpen(touchImageOpen);
    loadCurrentData.setMaruViewer(maruViewer);
    loadCurrentData.setMovieIgnore(movieIgnore);
    loadMyGallList(sharedPreferences, loadCurrentData);
    loadLoginCookie(sharedPreferences, loadCurrentData);
    loadFilteruserList(sharedPreferences, loadCurrentData);
    loadDcconPackage(sharedPreferences, loadCurrentData);
    loadRecommendList(sharedPreferences, loadCurrentData);
    currentData = loadCurrentData;

    return loadCurrentData;
  }

  private static void loadRecommendList(SharedPreferences sharedPreferences, CurrentData loadCurrentData) {

    Map<String, Long> recommendList = new HashMap<>();
    int count = sharedPreferences.getInt(RECOMMEND + SIZE, 0);

    for(int i=0; i<count; i++) {
      String key = sharedPreferences.getString(RECOMMEND + KEY + i, "");
      Long value = sharedPreferences.getLong(RECOMMEND + VALUE + i, 0);
      recommendList.put(key, value);
    }

    loadCurrentData.setRecommendList(recommendList);
  }


  // 방문 갤러리 리스트 로드
  private static void loadMyGallList(SharedPreferences sharedPreferences, CurrentData currentData) {
    int myGalleryListSize = sharedPreferences.getInt("my_gall_size", 0);
    List<GalleryInfo> myGallery = getMyGallery(sharedPreferences, myGalleryListSize);
    currentData.setMyGalleryList(myGallery);
  }

  // 유저 로그인 정보 로드
  private static void loadLoginCookie(SharedPreferences sharedPreferences, CurrentData currentData) {
    int loginCookieSize = sharedPreferences.getInt("login_cookie_size", 0);
    Map<String, String> loginCookie = new HashMap<>();
    for (int i = 0; i < loginCookieSize; i++) {
      String key = sharedPreferences.getString(LOGIN_COOKIE + i + KEY, "");
      String value = sharedPreferences.getString(LOGIN_COOKIE + i + VALUE, "");
      loginCookie.put(key, value);
    }
    currentData.setLoginCookies(loginCookie);
  }

  // 차단 유저 리스트 로드
  private static void loadFilteruserList(SharedPreferences sharedPreferences, CurrentData currentData) {
    int filterUserListSize = sharedPreferences.getInt("filter_user_list_size", 0);
    for (int i = 0; i < filterUserListSize; i++) {
      String nickname = sharedPreferences.getString(FILTER_USER_LIST + i + NICKNAME, "");
      String userType = sharedPreferences.getString(FILTER_USER_LIST + i + USERTYPE, "");
      currentData.getFilterUserList().add(new UserInfo(nickname, UserInfo.UserType.valueOf(userType)));
    }
  }


  // 디시콘 리스트 로드
  private static void loadDcconPackage(SharedPreferences sharedPreferences, CurrentData currentData) {

    int dcConPackageSize = sharedPreferences.getInt("dccon_list_size", 0);
    for (int i = 0; i < dcConPackageSize; i++) {
      DcConPackage dcConPackage = new DcConPackage();
      String pack = sharedPreferences.getString(DCCON_LIST + PACKAGE + i, "");
      String src = sharedPreferences.getString(DCCON_LIST + PACKAGE + i + SRC, "");
      dcConPackage.setDccon_package(pack);
      dcConPackage.setDccon_package_src(src);

      List<DcConPackage.DcCon> dcConList = new ArrayList<>();
      int dcconListSize = sharedPreferences.getInt(DCCON_LIST + PACKAGE + i + SIZE, 0);
      for (int j = 0; j < dcconListSize; j++) {
        DcConPackage.DcCon dcCon = new DcConPackage.DcCon();
        String dcconPack = sharedPreferences.getString(DCCON_LIST + PACKAGE + i + DCCON + j + PACKAGE, "");
        String dcconSrc = sharedPreferences.getString(DCCON_LIST + PACKAGE + i + DCCON + j + SRC, "");
        String dcconDetail = sharedPreferences.getString(DCCON_LIST + PACKAGE + i + DCCON + j + DETAIL, "");
        dcCon.setDccon_package(dcconPack);
        dcCon.setDccon_src(dcconSrc);
        dcCon.setDccon_detail(dcconDetail);
        dcConList.add(dcCon);
      }
      dcConPackage.setDcCons(dcConList);
      currentData.getDcConPackages().add(dcConPackage);
    }
  }


  // 방문 갤러리 리스트 로드
  private static List<GalleryInfo> getMyGallery(SharedPreferences sharedPreferences, int myGalleryListSize) {
    List<GalleryInfo> list = new ArrayList<>();
    for (int i = 0; i < myGalleryListSize; i++) {
      String name = sharedPreferences.getString(MY_GALL_LIST + i + "name", "");
      String code = sharedPreferences.getString(MY_GALL_LIST + i + "code", "");
      GalleryInfo galleryInfo = new GalleryInfo(name, code);
      list.add(galleryInfo);
    }
    return list;
  }
}

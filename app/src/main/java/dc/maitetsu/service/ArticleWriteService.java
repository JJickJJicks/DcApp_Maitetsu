package dc.maitetsu.service;

import android.util.Log;
import dc.maitetsu.models.ArticleModify;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Park Hyo Jun
 * @since 2017-04-21
 */
enum ArticleWriteService {
  getInstance;

  private static final String ARTICLE_WRITE_FORM_URL = "http://m.dcinside.com/write.php";
  private static final String ARTICLE_WRITE_IMAGE_UPLOAD_URL = "http://upload.dcinside.com/upload_imgfree_mobile.php";
  private static final String ARTICLE_GET_BLOCK_KEY_URL = "http://m.dcinside.com/_option_write.php";
  private static final String ARTICLE_WRITE_URL = "http://upload.dcinside.com/g_write.php";
  private static final JSONParser jsonParser = new JSONParser();


  String write(Map<String, String> loginCookie, String galleryCode, List<File> files,
               String userAgent, String title, String content, ArticleModify articleModify) throws IOException, ParseException, IllegalAccessException {

    String mode = "&mode=write";
    Map<String, String> writeFormData;
    StringBuilder delcheck = new StringBuilder();
    Document writeFormRawData = getWriteFormRawData(loginCookie, galleryCode, userAgent);

    if(articleModify == null) {
      writeFormData = getArticleWriteFormData(writeFormRawData);
    } else {
      writeFormData = articleModify.getArticleWriteDataList();
      mode = "&mode=modify";
      for(String d : articleModify.getDeleteFileList()) {
        delcheck.append(d)
                .append(";");
      }
    }

    if(writeFormData == null) return null;
    else {
      writeFormData.put("subject", title);
      writeFormData.put("memo", content);
      writeFormData.put("delcheck", delcheck.toString());
      writeFormData.put("Block_key", getBlockKey(loginCookie, writeFormData, userAgent));
    }

    if (files != null && !files.isEmpty()) { // 이미지 업로드
      boolean uploadResult =
              uploadFiles(loginCookie, writeFormData, files, userAgent, galleryCode, articleModify);
      if(!uploadResult) throw new IllegalAccessException();
    }


    Document result = Jsoup.connect(ARTICLE_WRITE_URL)
                            .userAgent(userAgent)
                            .cookies(loginCookie)
                            .header("Origin", "http://m.dcinside.com")
                            .header("Referer", ARTICLE_WRITE_FORM_URL + "?id=" + galleryCode + mode)
                            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                            .header("Accept-Encoding", "gzip, deflate")
                            .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
                            .data(writeFormData)
                            .post();

    return result.body().text();
  }


  // 이미지 업로드하고 이미지 정보를 writeFormData에 추가
  private boolean uploadFiles(Map<String, String> loginCookie,
                              Map<String, String> writeFormData,
                              List<File> files,
                              String userAgent,
                              String galleryCode,
                              ArticleModify articleModify) {
    try {
      Connection connection = Jsoup.connect(ARTICLE_WRITE_IMAGE_UPLOAD_URL)
              .cookies(loginCookie)
              .userAgent(userAgent)
              .header("Origin", "http://m.dcinside.com")
              .header("Referer", ARTICLE_WRITE_FORM_URL + "?id=" + galleryCode + "&mode=write")
              .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
              .header("Accept-Encoding", "gzip, deflate")
              .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
              .timeout(10000)
              .data("imgId", writeFormData.get("imgId"))
              .data("mode", writeFormData.get("mode"))
              .data("img_num", writeFormData.get("img_num"));

              int size = 0;
              if(articleModify != null) { // 글 수정인 경우 순서를 다시 설정한다
                size = articleModify.getAttachFileList().size()
                        - articleModify.getDeleteFileList().size();
              }

              for (int i = 0; i < files.size(); i++) {
                connection.data("upload[" + (i + size) + "]",
                        files.get(i).getName(), new FileInputStream(files.get(i)));
              }
              connection.data("upload[" + files.size() + "]");

      Element script = connection.post();
      String scriptText = script.html();

      List<String> data = Arrays.asList(scriptText.split("'"));

      if (data.size() < 9) throw new Exception();
      writeFormData.put("FL_DATA", data.get(5));
      writeFormData.put("OFL_DATA", data.get(9));
      return true;

    } catch (Exception e) {
            Log.i("err", e.getMessage());
      return false;}
  }


  // 글쓰기 폼 데이터를 Map으로 가공
  static Map<String, String> getArticleWriteFormData(Document doc) {
    Map<String, String> result = new HashMap<>();

    result.put("user_id", doc.select("#user_id").attr("value"));
    result.put("id", doc.select("#id").attr("value"));
    result.put("page", doc.select("#page").attr("value"));
    result.put("mode", doc.select("#mode").attr("value"));
    result.put("code", doc.select("input[name=code]").attr("value"));
    result.put("fno", doc.select("input[name=fno]").attr("value"));
    result.put("mobile_key", doc.select("#mobile_key").attr("value"));
    result.put("t_ch2", doc.select("#t_ch2").attr("value"));
    result.put("FL_DATA", doc.select("#FL_DATA").attr("value"));
    result.put("OFL_DATA", doc.select("#OFL_DATA").attr("value"));
    result.put("filter", doc.select("#filter").attr("value"));
    result.put("wikiTag", doc.select("#wikiTag").attr("value"));

    result.put("imgId", doc.select("#imgId").attr("value"));
    result.put("img_num", doc.select("#img_num").attr("value"));

      return result;
  }


  // 글쓰기 폼의 raw data를 얻어오는 메소드
  private Document getWriteFormRawData(Map<String, String> loginCookie, String galleryCode, String userAgent) throws IOException {
    return Jsoup.connect(ARTICLE_WRITE_FORM_URL + "?id=" + galleryCode + "&mode=write")
            .cookies(loginCookie)
            .userAgent(userAgent)
            .header("Origin", "http://m.dcinside.com")
            .header("Referer", "http://m.dcinside.com/login.php?r_url=m.dcinside.com%2Findex.php")
            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("Accept-Encoding", "gzip, deflate")
            .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
            .timeout(3000)
            .get();
  }


  private String getBlockKey(Map<String, String> loginCookie, Map<String, String> writeFormData, String userAgent) throws IOException, ParseException {

    Document result = Jsoup.connect(ARTICLE_GET_BLOCK_KEY_URL)
            .userAgent(userAgent)
            .header("Origin", "http://m.dcinside.com")
            .header("Referer", "http://m.dcinside.com/login.php?r_url=m.dcinside.com%2Findex.php")
            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("Accept-Encoding", "gzip, deflate")
            .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
            .data("id", writeFormData.get("id"))
            .data("w_subject", writeFormData.get("subject"))
            .data("w_memo", writeFormData.get("memo"))
            .data("w_filter", writeFormData.get("filter"))
            .data("mode", "write_verify")
            .cookies(loginCookie)
            .ignoreContentType(true)
            .post();

    JSONObject jo = (JSONObject) jsonParser.parse(result.body().text());
    return (String) jo.get("data");
  }




}

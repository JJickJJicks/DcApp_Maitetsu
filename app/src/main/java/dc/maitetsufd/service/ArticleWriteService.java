package dc.maitetsufd.service;

import dc.maitetsufd.models.ArticleDetail;
import dc.maitetsufd.models.ArticleModify;
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
 * @since 2017-04-21
 */
public enum ArticleWriteService {
  getInstance;

  private static final String ARTICLE_WRITE_FORM_URL = "http://m.dcinside.com/write/";
  private static final String ARTICLE_MODIFY_FORM_URL = "http://m.dcinside.com/modify/";
  private static final String ARTICLE_WRITE_IMAGE_UPLOAD_URL = "http://upload.dcinside.com/upload_img.php";
  private static final String ARTICLE_WFILTER_URL = "http://m.dcinside.com/ajax/w_filter";
  private static final String ARTICLE_WRITE_URL = "http://upload.dcinside.com/write_new.php";
  private static final JSONParser jsonParser = new JSONParser();


  public String write(Map<String, String> loginCookie, String galleryCode, List<File> files,
               String userAgent, String title, String content, ArticleModify articleModify) throws Exception {

      Map<String, String> writeFormData;
      Document writeFormRawData = getWriteFormRawData(loginCookie, galleryCode, userAgent);
      String delcheck = "";
      String iData = "";
      String contentOrder = "";
      final String csrfToken = writeFormRawData.select("meta[name=csrf-token]").attr("content");
      String referrer = ARTICLE_WRITE_FORM_URL + galleryCode;

      if (articleModify == null) {
        writeFormData = getArticleWriteFormData(writeFormRawData, csrfToken, userAgent, loginCookie);
        writeFormData.put("mode", "write");
        writeFormData.remove("no");
        writeFormData.remove("t_ch2");

      } else {
        writeFormData = articleModify.getArticleWriteDataList();
        writeFormData.put("Block_key", AccessTokenService.getInstance.getAccessToken("dc_check2", "", csrfToken, userAgent, loginCookie));
        writeFormData.put("mode", "modify");
        referrer = ARTICLE_MODIFY_FORM_URL + writeFormData.get("no");

        for (String d : articleModify.getDeleteFileList()) {
          ArticleModify.AttachFile attachFile = null;

          for (ArticleModify.AttachFile file : articleModify.getAttachFileList()) {
            if (file.getFno().equals(d)) {
              attachFile = file;
              break;
            }
          }
          if (attachFile != null) {
            articleModify.getAttachFileList().remove(attachFile);
            delcheck = delcheck.length() > 0 ? delcheck + ";" + d : d;
          }
        }

        // 수정 전 원본
        for (ArticleModify.AttachFile attachFile : articleModify.getAttachFileList()) {
          if (attachFile.getName() == null || attachFile.getName().isEmpty()
              || attachFile.getSrc() == null || attachFile.getSrc().isEmpty()) continue;

          iData = iData.length() > 0 ? iData + "^@^" : iData;
          iData += attachFile.getName() + "|" + attachFile.getSrc();
          contentOrder += attachFile.getName() + ";";
        }

        writeFormData.put("delcheck", delcheck);
      }




      if (files != null && !files.isEmpty()) { // 이미지 업로드
        String uploadResult = uploadFiles(loginCookie, files, userAgent, galleryCode, articleModify);
        if (uploadResult.isEmpty()) throw new IllegalAccessException(uploadResult);
        if (iData.length() > 0) {
          iData += "^@^";
        }
        iData += uploadResult;

        String[] keys = uploadResult.split("\\^@\\^");
        for (int i=0; i<keys.length; i++) {
          contentOrder += keys[i].split("\\|")[0] + ";";
        }
      }

      writeFormData.put("id", galleryCode);
      writeFormData.put("headtext", "0");
      writeFormData.put("subject", title);
      writeFormData.put("memo", content);
      writeFormData.put("iData", iData);
      writeFormData.put("contentOrder", contentOrder + "order_memo");

      try {
        // wfilter 체크
        Connection.Response wFilterResult =  Jsoup.connect(ARTICLE_WFILTER_URL)
                                                  .userAgent(userAgent)
                                                  .cookies(loginCookie)
                                                  .header("Host", "m.dcinside.com")
                                                  .header("Origin", "http://m.dcinside.com")
                                                  .header("X-CSRF-TOKEN", csrfToken)
                                                  .header("X-Requested-With", "XMLHttpRequest")
                                                  .referrer(referrer)
                                                  .ignoreHttpErrors(true)
                                                  .ignoreContentType(true)
                                                  .data("subject", writeFormData.get("subject"))
                                                  .data("memo", writeFormData.get("memo"))
                                                  .data("id", galleryCode)
                                                  .method(Connection.Method.POST)
                                                  .execute();

        JSONObject wFiltered = (JSONObject) jsonParser.parse(wFilterResult.body());
        if (!wFiltered.get("result").equals(true)) {
          throw new Exception(" 다시 시도해보세요");
        }

        // 글 업로드
        Document result = Jsoup.connect(ARTICLE_WRITE_URL)
                                .userAgent(userAgent)
                                .cookies(loginCookie)
                                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                                .header("Host", "upload.dcinside.com")
                                .header("Origin", "http://m.dcinside.com")
                                .referrer(referrer)
                                .timeout(10000)
                                .ignoreHttpErrors(true)
                                .ignoreContentType(true)
                                .data(writeFormData)
                                .post();

        String response = result.html();
        if (response.contains("alert")) {
          String msg = response.split("alert\\('")[1]
                               .split("'\\)")[0];
          throw new Exception(" " + msg);
        }

        return "";

      } catch (Exception e) {
        throw e;
      }
  }


  // 이미지 업로드하고 이미지 정보를 writeFormData에 추가
  private String uploadFiles(Map<String, String> loginCookie,
                              List<File> files,
                              String userAgent,
                              String boardId,
                              ArticleModify articleModify) {
    try {
      StringBuilder result = new StringBuilder();

      int size = 0;
      if (articleModify != null) { // 글 수정인 경우 순서를 다시 설정한다
        size = articleModify.getAttachFileList().size();
      }

      for (int i = 0; i < files.size(); i++) {
        String scriptText = Jsoup.connect(ARTICLE_WRITE_IMAGE_UPLOAD_URL)
                                    .cookies(loginCookie)
                                    .userAgent(userAgent)
                                    .header("Host", "upload.dcinside.com")
                                    .header("Origin", "http://m.dcinside.com")
                                    .referrer(ARTICLE_WRITE_FORM_URL)
                                    .timeout(10000)
                                    .data("id", boardId)
                                    .data("upload", files.get(i).getName(), new FileInputStream(files.get(i)))
                                    .post().html();

        String[] rawData = scriptText.split("insertChagneThum\\(");
        rawData = rawData[1].replace("'", "")
                           .split(",0\\);")[0].split(",");
        if (i > 0) {
          result.append("^@^");
        }
        result.append(rawData[0] + (size + i) + "|" + rawData[1]);
      }
      return result.toString();

    } catch (IndexOutOfBoundsException ie) {
      return "";

    } catch (Exception e) {
      return uploadFiles(loginCookie, files, userAgent, boardId, articleModify);

    }
  }


  // 글쓰기 폼 데이터를 Map으로 가공
  static Map<String, String> getArticleWriteFormData(Document doc, String csrfToken, String userAgent, Map<String, String> loginCookie) {
    Map<String, String> result = new HashMap<>();

    result.put("id", doc.select("#id").attr("value"));
    result.put("Block_key", AccessTokenService.getInstance.getAccessToken("dc_check2", "", csrfToken, userAgent, loginCookie));
    result.put("bgm", doc.select("#bgm").attr("value"));
    result.put("yData", "");
    result.put("tmp", "");
    result.put("mobile_key", doc.select("#mobile_key").attr("value"));
    result.put("user_id", doc.select("#user_id").attr("value"));
    result.put("no", doc.select("#no").attr("value"));
    result.put("t_ch2", "0");


      return result;
  }


  // 글쓰기 폼의 raw data를 얻어오는 메소드
  private Document getWriteFormRawData(Map<String, String> loginCookie, String galleryCode, String userAgent) {
    try {
      Connection.Response response =  Jsoup.connect(ARTICLE_WRITE_FORM_URL + galleryCode)
                  .cookies(loginCookie)
                  .userAgent(userAgent)
                  .header("Origin", "http://m.dcinside.com")
                  .referrer(ARTICLE_WRITE_FORM_URL + galleryCode)
                  .timeout(3000)
                  .method(Connection.Method.GET)
                  .execute();

      loginCookie.putAll(response.cookies());
      return response.parse();

    } catch (Exception e) {
      return getWriteFormRawData(loginCookie, galleryCode, userAgent);

    }
  }

}
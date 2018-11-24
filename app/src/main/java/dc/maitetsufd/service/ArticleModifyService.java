package dc.maitetsufd.service;

import dc.maitetsufd.models.ArticleDetail;
import dc.maitetsufd.models.ArticleModify;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @since 2017-05-07
 */
public enum ArticleModifyService {
  getInstance;

  /**
   * 수정 페이지에 들어가 기본 정보를 얻어오는 메소드
   *
   * @param userAgent     the user agent
   * @param articleDetail the article detail
   * @param loginCookie   the login cookie
   * @return the article modify data
   * @throws IOException the io exception
   */
  public ArticleModify getArticleModifyData(String userAgent, ArticleDetail articleDetail, Map<String, String> loginCookie) {
    Document doc = getArticleModifyRawData(userAgent, articleDetail, loginCookie);
    ArticleModify articleModify = new ArticleModify();

    final String csrfToken = doc.select("meta[name=csrf-token]").attr("content");

    Map<String, String> articleWriteData = ArticleWriteService.getArticleWriteFormData(doc, csrfToken, userAgent, loginCookie);
    articleWriteData.put("no", doc.select("input[name=no]").attr("value"));

    List<ArticleModify.AttachFile> attachFileList = getArticleAttachFiles(doc);
    articleModify.setArticleWriteDataList(articleWriteData);
    articleModify.setAttachFileList(attachFileList);
    articleModify.setTitle(doc.select("input#subject").val());
    articleModify.setContent(htmlToText(doc.select("div#textBox").html()));
    return articleModify;
  }

  // 기존 첨부된 파일 목록을 얻어오는 메소드
  private List<ArticleModify.AttachFile> getArticleAttachFiles(Document doc) {
    List<ArticleModify.AttachFile> list = new ArrayList<>();
    String[] mData = doc.select("input#mData").attr("value").split("\\^@\\^");

    for(int i=0; i<mData.length; i++) {
      String[] data = mData[i].split("\\^%\\^");
      if (data.length < 2) continue;

      ArticleModify.AttachFile attachFile = new ArticleModify.AttachFile();
      attachFile.setFno(data[0].replace("img", ""));
      attachFile.setName(data[0]);
      attachFile.setSrc(data[1]);
      if (attachFile.getFno().isEmpty()) continue;

      list.add(attachFile);
    }

    return list;
  }

  // 수정 페이지의 rawData를 얻어오는 메소드
  private Document getArticleModifyRawData(String userAgent, ArticleDetail articleDetail, Map<String, String> loginCookie) {
    try {
      Connection.Response response =  Jsoup.connect(articleDetail.getModifyUrl())
                                          .userAgent(userAgent)
                                          .cookies(loginCookie)
                                          .header("Origin", "http://m.dcinside.com")
                                          .referrer(articleDetail.getUrl())
                                          .method(Connection.Method.GET)
                                          .execute();

      loginCookie.putAll(response.cookies());
      return response.parse();
    } catch (Exception e) {
      return getArticleModifyRawData(userAgent, articleDetail, loginCookie);

    }
  }

  private String htmlToText(String html) {
    return html.replace("<br>", "")
               .replaceAll("<br>", "\n");
  }


}

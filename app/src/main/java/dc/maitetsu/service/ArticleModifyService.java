package dc.maitetsu.service;

import dc.maitetsu.models.ArticleDetail;
import dc.maitetsu.models.ArticleModify;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Park Hyo Jun
 * @since 2017-05-07
 */
enum ArticleModifyService {
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
  public ArticleModify getArticleModifyData(String userAgent, ArticleDetail articleDetail, Map<String, String> loginCookie) throws IOException {
    Document doc = getArticleModifyRawData(userAgent, articleDetail, loginCookie);
    ArticleModify articleModify = new ArticleModify();
    Map<String, String> articleWriteData = ArticleWriteService.getArticleWriteFormData(doc);
    articleWriteData.put("no", doc.select("input[name=no]").attr("value"));

    List<ArticleModify.AttachFile> attachFileList = getArticleAttachFiles(doc);
    articleModify.setArticleWriteDataList(articleWriteData);
    articleModify.setAttachFileList(attachFileList);
    articleModify.setTitle(doc.select("input#subject").val());
    articleModify.setContent(doc.select("textarea#memo").text());
    return articleModify;
  }

  // 기존 첨부된 파일 목록을 얻어오는 메소드
  private List<ArticleModify.AttachFile> getArticleAttachFiles(Document doc) {
    List<ArticleModify.AttachFile> list = new ArrayList<>();
    Elements fileElements = doc.select("div[name=upload_preview[]] > li > a");

    for(Element element : fileElements) {
      ArticleModify.AttachFile attachFile = new ArticleModify.AttachFile();
      String[] href = element.attr("href").split("'");
      attachFile.setFno(href[1]);
      attachFile.setOrder(href[3]);
      attachFile.setName(fileElements.next("span.route").first().text());
      list.add(attachFile);
    }
    return list;
  }

  // 수정 페이지의 rawData를 얻어오는 메소드
  private Document getArticleModifyRawData(String userAgent, ArticleDetail articleDetail, Map<String, String> loginCookie) throws IOException {
    return Jsoup.connect(articleDetail.getModifyUrl())
            .userAgent(userAgent)
            .cookies(loginCookie)
            .header("Origin", "http://m.dcinside.com")
            .header("Referer", articleDetail.getUrl())
            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
            .header("Accept-Encoding", "gzip, deflate")
            .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
            .get();
  }


}

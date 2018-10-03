package dc.maitetsufd.service;

import dc.maitetsufd.data.CurrentData;
import dc.maitetsufd.models.SimpleArticle;
import dc.maitetsufd.models.UserInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @since 2017-04-21
 */
public enum SimpleArticleService {
  getInstance;

  private static final String GALLERY_URL = "http://m.dcinside.com/board/";

  /**
   * 글 제목과 유저 정보를 담고있는 SimpleArticle을 구해내는 메소드
   *
   * @param userAgent     모바일 기기 UserAgent
   * @return SimpleArticle 리스트
   * @throws InterruptedException the io exception
   */
  public List<SimpleArticle> getSimpleArticles(CurrentData currentData, String userAgent,
                                        boolean isRecommend) throws InterruptedException {

    Document pageRawData = getRawData(currentData, userAgent, isRecommend);
    return getListData(pageRawData.select("div.gall-detail-lnktb"));
  }

  // 갤러리에 접속해 Document를 얻어오는 메소드
  private Document getRawData(CurrentData currentData, String userAgent, boolean isRecommend) throws InterruptedException {


    final Map<String, String> loginCookie = currentData.getLoginCookies();
    final String galleryCode = currentData.getGalleryInfo().getGalleryCode();
    final int pageNumber = currentData.getPage();
    final String searchWord = currentData.getSearchWord();
    currentData.setSerPos(currentData.getNextSerPos());
    String recommendStr = ""; if(isRecommend) recommendStr = "&recommend=1";
    String page = "&page=" + pageNumber
                          + "&s_pos=" + currentData.getSerPos();

    String search = "";
    if(!searchWord.trim().isEmpty()){
      search = "&s_type=all&serval=" + searchWord;
    }

    try { // ?s_type=all&serval=ㅁ&page=2
      Document pageRawData = Jsoup.connect(GALLERY_URL + galleryCode + "?" + search + page  + recommendStr)
                                  .userAgent(userAgent)
                                  .header("Origin", "http://m.dcinside.com")
                                  .referrer("http://m.dcinside.com/")
                                  .header("Content-Type", "application/x-www-form-urlencoded")
                                  .cookies(loginCookie)
                                  .timeout(4000)
                                  .get();

      setNextSerPosValue(currentData, pageRawData);
      return pageRawData;

    } catch (Exception e) {
      System.out.println(e.getMessage());
      TimeUnit.MILLISECONDS.sleep(300);
      return getRawData(currentData, userAgent, isRecommend);

    }
  }

  // 다음 serpos값을 저장하는 메소드
  private void setNextSerPosValue(CurrentData currentData, Document pageRawData) {
      int nextSerPos = getNextSerPosValue(pageRawData);
      if (nextSerPos != 0) {
        currentData.setNextSerPos(nextSerPos);
      }
  }

  // document에서 serpos값을 얻어오는 메소드
  private int getNextSerPosValue(Document pageRawData) {
    Element a = pageRawData.select("div#pagination_div")
                          .select("a.next")
                          .first();
    if(a != null){
      try {
        String url = a.attr("abs:href");
        String serPosStr = url.split("s_pos=")[1].split("&")[0];
        return Integer.parseInt(serPosStr);
      } catch(Exception e){ return 0; }
    }
    return 0;
  }


  // Element를 SimpleArticle 객체의 리스트로 변경하는 메소드
  public List<SimpleArticle> getListData(Elements elements) {

    List<SimpleArticle> simpleArticles = new ArrayList<>();

    for (Element e : elements) {

      String[] user = e.nextElementSibling().ownText().split("\\|");
      Elements li = e.select("ul.ginfo > li");
      SimpleArticle simpleArticle = new SimpleArticle();
      if (li.size() == 5) {
        simpleArticle.setType(li.get(0).text());
        li.remove(0);
      }


      simpleArticle.setArticleType(getArticleType(e));

      // 검색어 처리
      Element subject = e.select("span.subject").first();
      subject.select("span.sp-lst").remove();
      simpleArticle.setTitle(subject.text());

      simpleArticle.setUrl(e.select("a.lt").attr("abs:href"));
      simpleArticle.setCommentCount(getCommentCount(e));
      simpleArticle.setUserInfo(new UserInfo(li.get(0).text(),
                                            user[1],
                                            CommonService.getUserType(li.get(0)),
                                            user[1]));

      simpleArticle.setDate(li.get(1).text());
      simpleArticle.setViewCount(Integer.parseInt(li.get(2).text().replace("조회 ", "")));
      simpleArticle.setRecommendCount(Integer.parseInt(li.get(3).text().replace("추천 ", "")));

      simpleArticles.add(simpleArticle);
    }

    return simpleArticles;
  }

  // 댓글 수 읽어내는 메소드
  private int getCommentCount(Element e) {
    String commentCount = e.select("span.ct").first().text();
    int result = 0;
    if (!commentCount.isEmpty())
      result = Integer.parseInt(commentCount);
    return result;
  }


  // 글 타입 읽어내는 메소드
  private SimpleArticle.ArticleType getArticleType(Element e) {
    Elements icoPic = e.select("span.sp-lst");

    if (icoPic.hasClass("sp-lst-img"))
      return SimpleArticle.ArticleType.IMG;

    else if (icoPic.hasClass("sp-lst-txt"))
      return SimpleArticle.ArticleType.NO_IMG;

    else if (icoPic.hasClass("sp-lst-recoimg") || icoPic.hasClass("sp-lst-recotxt"))
      return SimpleArticle.ArticleType.RECOMMAND;

    else
      return SimpleArticle.ArticleType.MOV;

  }


}

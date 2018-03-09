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

/**
 * @since 2017-04-21
 */
enum SimpleArticleService {
  getInstance;

  private static final String GALLERY_URL = "http://m.dcinside.com/list.php";

  /**
   * 글 제목과 유저 정보를 담고있는 SimpleArticle을 구해내는 메소드
   *
   * @param userAgent     모바일 기기 UserAgent
   * @return SimpleArticle 리스트
   * @throws IOException the io exception
   */
  List<SimpleArticle> getSimpleArticles(CurrentData currentData, String userAgent,
                                        boolean isRecommend,
                                        boolean refreshSerPos) throws IOException {

    Document pageRawData = getRawData(currentData, userAgent, isRecommend, refreshSerPos);
    return getListData(pageRawData.select(".list_best li span a"));
  }

  // 갤러리에 접속해 Document를 얻어오는 메소드
  private Document getRawData(CurrentData currentData, String userAgent, boolean isRecommend,
                              boolean refreshSerPos) throws IOException {


    final Map<String, String> loginCookie = currentData.getLoginCookies();
    final String galleryCode = currentData.getGalleryInfo().getGalleryCode();
    final int pageNumber = currentData.getPage();
    final String searchWord = currentData.getSearchWord();
    String recommendStr = ""; if(isRecommend) recommendStr = "&recommend=1";
    if(refreshSerPos) currentData.setSerPos(currentData.getNextSerPos());
    String page = "&page=" + pageNumber + "&ser_pos=" + currentData.getSerPos();

    String search = "";
    if(!searchWord.trim().isEmpty()){
      search = "&s_type=all&serVal=" + searchWord;
    }

    Document pageRawData =  Jsoup.connect(GALLERY_URL + "?id=" + galleryCode + page + search + recommendStr)
                        .userAgent(userAgent)
                        .header("Origin", "http://m.dcinside.com")
                        .header("Referer", "http://m.dcinside.com/login.php?r_url=m.dcinside.com%2Findex.php")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .header("Accept-Encoding", "gzip, deflate")
                        .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
                        .cookies(loginCookie)
                        .timeout(4000)
                        .get();

    setNextSerPosValue(currentData, pageRawData);
    return pageRawData;
  }

  // 다음 serpos값을 저장하는 메소드
  private void setNextSerPosValue(CurrentData currentData, Document pageRawData) {
      int nextSerPos = getNextSerPosValue(pageRawData);
      currentData.setNextSerPos(nextSerPos);
  }

  // document에서 serpos값을 얻어오는 메소드
  private int getNextSerPosValue(Document pageRawData) {
    Elements a = pageRawData.select(".new-paging.type1").select("a");
    if(a.size() > 0){
      try {
        String url = a.last().attr("abs:href");
        String serPosStr = url.split("ser_pos=")[1].split("&")[0];
        return Integer.parseInt(serPosStr);
      } catch(Exception e){ return 0; }
    }
    return 0;
  }


  // Element를 SimpleArticle 객체의 리스트로 변경하는 메소드
  private List<SimpleArticle> getListData(Elements elements) {

    List<SimpleArticle> simpleArticles = new ArrayList<>();

    for (Element e : elements) {
      Elements span = e.select("span.info span").not("[style]"); // 검색 결과의 색상 span 제외
      int spanSize = span.size();

      SimpleArticle simpleArticle = new SimpleArticle();
      simpleArticle.setTitle(e.select(".title .txt").first().text());
      simpleArticle.setUrl(e.attr("abs:href"));
      simpleArticle.setCommentCount(getCommentCount(e));
      simpleArticle.setArticleType(getArticleType(e));
      simpleArticle.setUserInfo(new UserInfo(e.select(".info .name").first().text(),
                                            CommonService.getUserType(e),
                                            span.get(1).text()));

      if (simpleArticle.getUserInfo().getUserType() == UserInfo.UserType.FLOW){
        simpleArticle.getUserInfo().setNickname(
          simpleArticle.getUserInfo().getNickname() + span.get(1).text()
        );
      }

      simpleArticle.setDate(span.get(2).text());
      simpleArticle.setRecommendCount(Integer.parseInt(span.get(spanSize - 1).html()));
      simpleArticle.setViewCount(Integer.parseInt(span.get(spanSize - 4).html()));

      simpleArticles.add(simpleArticle);
    }

    return simpleArticles;
  }

  // 댓글 수 읽어내는 메소드
  private int getCommentCount(Element e) {
    String commentCount = e.select(".title .txt_num").first().text()
            .split("/")[0]
            .replaceAll("\\D+", "");
    int result = 0;
    if (!commentCount.isEmpty())
      result = Integer.parseInt(commentCount);
    return result;
  }


  // 글 타입 읽어내는 메소드
  private SimpleArticle.ArticleType getArticleType(Element e) {
    Elements icoPic = e.select(".ico_pic");

    if (icoPic.hasClass("ico_p_y"))
      return SimpleArticle.ArticleType.IMG;
    else if (icoPic.hasClass("ico_t"))
      return SimpleArticle.ArticleType.NO_IMG;
    else if (icoPic.hasClass("ico_mv"))
      return SimpleArticle.ArticleType.MOV;
    else
      return SimpleArticle.ArticleType.RECOMMAND;
  }


}

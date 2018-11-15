package dc.maitetsufd.service;


import dc.maitetsufd.models.ArticleDetail;
import dc.maitetsufd.models.Comment;
import dc.maitetsufd.models.UserInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @since 2017-04-21
 */
public enum ArticleDetailService {
  getInstance;

  private static final String MODIFY_URL = "http://m.dcinside.com/write/lovegame/modify/";

  /**
   * 게시물의 상세 정보를 얻어내는 메소드
   *
   * @param loginCookie 로그인 정보
   * @param userAgent   모바일 기기 UserAgent
   * @param articleUrl  게시물의 URL
   * @return 게시물 상세 정보
   * @throws IOException the io exception
   */
  public ArticleDetail getArticleDetail(Map<String, String> loginCookie, String userAgent, String articleUrl) throws IOException {
    Document articleRawData = getArticleRawData(loginCookie, userAgent, articleUrl);
    ArticleDetail article = new ArticleDetail();

    String articleNumber = articleRawData.select("input#no").first().val();
    article.setNo(articleNumber);

    article.setBoardId(articleRawData.select("input#id").first().val());

    article.setTitle(articleRawData.select("div.gallview-tit-box span.tit").first().text());
    setUserInfoAndDate(article, articleRawData.select("div.gallview-tit-box ul.ginfo2").first());

    article.setUrl(articleUrl);

    // 조회수/추천수
    Elements thumbTags = articleRawData.select("div.gall-thum-btm-inner ul.ginfo2 li");
    article.setViewCount(Integer.parseInt(thumbTags.get(0).text().replace("조회수 ", "")));
    article.setRecommendCount(Integer.parseInt(thumbTags.get(1).text().replace("추천 ", "")));
    article.setNoRecommendCount(Integer.parseInt(articleRawData.select("li.reco-down span#nonrecomm_btn").text()));

    // 본문
    article.setContentDataList(getContentData(articleRawData));

    // 댓글들
    article.setComments(getComments(articleRawData.select("ul.all-comment-lst li")));

    article.setCommentWriteData(getCommentWriteData(articleRawData));
    article.setCommentDeleteData(getCommentDeleteData(articleRawData));
    article.setArticleDeleteData(getArticleDeleteData(articleRawData));
//    article.setRecommendData(getRecommendData(articleRawData.getElementsByTag("script")));

    // 수정가능하면
    Elements editButtons = articleRawData.select("div.btn-justify-area button");
    if(editButtons.size() >= 3) {
      article.setModifyUrl(MODIFY_URL + articleNumber);
    }


    return article;
  }

  // 유저명/작성시간
  private void setUserInfoAndDate(ArticleDetail article, Element e) {
    Elements li = e.select("li");

    // 유저정보
    String userUrl = "";
    Element nextElement = e.nextElementSibling();
    if (nextElement != null) {
      String[] urls = nextElement.select("div.rt > a")
                      .first().attr("abs:href").split("/");
      userUrl = urls[urls.length-1];
    }

    UserInfo userInfo = new UserInfo(li.get(0).ownText(),
                                    userUrl,
                                    CommonService.getUserType(li.get(0)),
                                    "");

    article.setUserInfo(userInfo);
    article.setDate(li.get(1).ownText());

  }

  // 새로운 내용 파서. 순서를 유지한다.
  public List<ArticleDetail.ContentData> getContentData(Document articleRawData) throws IOException {
    List<ArticleDetail.ContentData> list = new ArrayList<>();

    Element contentTd = articleRawData.select("div.thum-txtin").first();

    // 의미없는 태그 제거
    contentTd.select("div + br").remove();


    ArticleDetail.ContentData contentData = new ArticleDetail.ContentData();
    for (Node node : contentTd.childNodes()) {
        contentData = nodeToData(list, contentData, node);
    }
    list.add(contentData);
    return list;
  }

  // 노드를 데이터로 변환하는 메소드
  private ArticleDetail.ContentData nodeToData(List<ArticleDetail.ContentData> list, ArticleDetail.ContentData contentData, Node node) {

    if (node.nodeName().equals("object")) {
      // child node 중 embed를 얻어낸다
      for (Node childNode : node.childNodes()) {
        if(childNode.nodeName().equals("embed")) {
          return nodeToData(list, contentData, childNode);
        }
      }

    } else if (node.nodeName().equals("a")) {
      if(node.childNodeSize() == 0) return contentData; // 비어있는 a태그 제외

      for (Node aNode : node.childNodes()) {
        contentData.setLinkUrl(node.attr("abs:href").trim());
        contentData = nodeToData(list, contentData, aNode);
      }

    } else if (node.nodeName().equals("br")) {
        contentData.getText().append("\n");

    } else if (node.nodeName().equals("img")) {
      // 이미지는 무조건 새 행으로 개행한다
      list.add(contentData);
      contentData = new ArticleDetail.ContentData();
      contentData.setImageUrl(node.attr("abs:src"));
      list.add(contentData);
      return new ArticleDetail.ContentData();

//    } else if(node.nodeName().equals("iframe")) {
//      list.add(contentData);
//      contentData = new ArticleDetail.ContentData();
//      contentData.getText().append("iframe");
//      contentData.setLinkUrl(node.attr("abs:src"));
//      list.add(contentData);
//      return new ArticleDetail.ContentData();

    } else if (node.nodeName().equals("embed") || node.nodeName().equals("iframe")) {
      list.add(contentData);
      contentData = new ArticleDetail.ContentData();
      contentData.setEmbedUrl(node.attr("abs:src"));
      list.add(contentData);
      return new ArticleDetail.ContentData();

    } else  if(node.nodeName().equals("p") || node.nodeName().equals("div")) {

        // 무조건 새로운 행에 표시되는 태그
        list.add(contentData);
        contentData = new ArticleDetail.ContentData();

        if(node.childNodeSize() == 1 && node.childNode(0).nodeName().equals("br")) {
          contentData.getText().append(" "); // blankText
          list.add(contentData);
          return new ArticleDetail.ContentData();

        }

        for(Node childNode : node.childNodes()) {
          contentData = nodeToData(list, contentData, childNode);
        }
        list.add(contentData);
        return new ArticleDetail.ContentData();

      } else if(node.nodeName().equals("#text") && !node.toString().trim().isEmpty()) {
      // 문자열 노드
        contentData.getText()
                .append(node.toString().replace("\n", ""));


      } else if(node.nodeName().equals("table")) {
      // 테이블 노드. 무조건 새로운 행에 표시되며,
      // TR 태그마다 새로운 행으로 표시된다
        list.add(contentData);

        Document tableDoc = Jsoup.parse(node.toString());
        Elements elements = tableDoc.select("tr");
        for(Element trElement : elements) {
          contentData = new ArticleDetail.ContentData();

          for(Element tdElement : trElement.select("td")) {
              for (Node tdNode : tdElement.childNodes()) {
                contentData = nodeToData(list, contentData, tdNode);
              }
            contentData.getText().append("&nbsp;&nbsp;");
          }

          list.add(contentData);
        }

        return new ArticleDetail.ContentData();

    } else { //그 외 태그
      for (Node childNode : node.childNodes()) {
        nodeToData(list, contentData, childNode);
      }
    }

      return contentData;
  }


  // 게시물의 rawData를 얻어오는 메소드
  private Document getArticleRawData(Map<String, String> loginCookie, String userAgent, String articleUrl) throws IOException {
    try {
      return Jsoup.connect(articleUrl)
              .userAgent(userAgent)
              .cookies(loginCookie)
              .timeout(3000)
              .header("Origin", "http://m.dcinside.com")
              .header("Referer", "http://m.dcinside.com/login.php?r_url=m.dcinside.com%2Findex.php")
              .header("Content-Type", "application/x-www-form-urlencoded")
              .get();
    } catch (Exception e) {
      return getArticleRawData(loginCookie, userAgent, articleUrl);
    }
  }


  // 게시물 삭제를 위한 정보를 얻어오는 메소드
  private ArticleDetail.ArticleDeleteData getArticleDeleteData(Element element) {
    ArticleDetail.ArticleDeleteData articleDeleteData = new ArticleDetail.ArticleDeleteData();
    articleDeleteData.setNo(element.select("#no").val());
    articleDeleteData.setId(element.select("#id").val());
    return articleDeleteData;
  }


  // 댓글 삭제 정보를 읽는 메소드
  private ArticleDetail.CommentDeleteData getCommentDeleteData(Document doc) {
    ArticleDetail.CommentDeleteData delData = new ArticleDetail.CommentDeleteData();

    delData.setId(doc.select("#id").attr("value"));
    delData.setNo(doc.select("#no").attr("value"));
    delData.setBoard_id(doc.select("#board_id").attr("value"));
    delData.setBest_chk(doc.select("#best_chk").attr("value"));
    delData.setCsrfToken(doc.select("meta[name=csrf-token]").attr("content"));

    return delData;
  }

  // 댓글 작성 정보를 읽는 메소드
  private ArticleDetail.CommentWriteData getCommentWriteData(Document doc) {
    ArticleDetail.CommentWriteData cwd = new ArticleDetail.CommentWriteData();

    cwd.setMode("com_write");
    cwd.setId(doc.select("#id").attr("value"));
    cwd.setNo(doc.select("#no").attr("value"));
    cwd.setBest_chk(doc.select("#best_chk").attr("value"));
    cwd.setBoard_id(doc.select("#board_id").attr("value"));
    cwd.setCpage(doc.select("#cpage").attr("value"));
    cwd.setSubject(doc.select("div.gallview-tit-box span.tit").first().text());
    cwd.setCsrfToken(doc.select("meta[name=csrf-token]").attr("content"));

    cwd.setHoneyKey(doc.select("#firstname").attr("name"));

    String value = doc.select("#firstname").val();
    cwd.setHoneyValue(value);
    if (value.isEmpty()) {
      cwd.setHoneyValue("1");
    }
    return cwd;
  }


  // 댓글들을 읽어오는 메소드
  private List<Comment> getComments(Elements commentElements) {
    List<Comment> comments = new ArrayList<>();

    for (Element e : commentElements) {
      try {
        if (e.hasClass("paging")) { // 다음 페이지 버튼
          continue;

        }else if (e.select("div.delted").first() != null) { // 삭제된 댓글
          Comment comment = new Comment(
                  false,
                  new UserInfo("", "", UserInfo.UserType.EMPTY, ""),
                  "",
                  e.select("div.delted").first().text(),
                  "",
                  "",
                  "",
                  ""
          );
          comments.add(comment);
          continue;
        }

        // 닉네임 및 아이피 감지
        String nickname = e.select("a.nick").first().ownText();
        String gallogId = "", userIp = "";
        Element gallog = e.select("span.blockCommentId").first();
        if (gallog != null) {
          gallogId = gallog.ownText();
        }
        Element user = e.select("span.blockCommentIp").first();
        if (user != null) {
          userIp = user.ownText();
        }

        // 삭제 가능한 댓글만 표시
        String deleteCode = e.attr("no");
        if (e.select("div.comment-del").first() == null) {
          deleteCode = "";
        }

        // 보이스 댓글 / iframe URL
        String voiceUrl = e.select(".voice-box").attr("vr_copy");
        if (voiceUrl == null || voiceUrl.isEmpty()) {
          voiceUrl = e.select("p.txt iframe").attr("abs:src");
        }

        Comment comment = new Comment(
                e.hasClass("comment-add"),
                new UserInfo(nickname,
                        gallogId,
                        CommonService.getUserType(e.select("a.nick").first()),
                        userIp),
                userIp,
                e.select("p.txt").text().trim(),
                e.select("span.date").text(),
                e.select("p.txt img").attr("abs:src"),
                voiceUrl,
                deleteCode
        );
        comments.add(comment);
      } catch (Exception e2) {
        // 읽을 수 없는 댓글은 무시
      }
    }


    return comments;
  }

}
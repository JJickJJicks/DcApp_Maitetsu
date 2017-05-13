package dc.maitetsu.service;


import dc.maitetsu.models.ArticleDetail;
import dc.maitetsu.models.Comment;
import dc.maitetsu.models.UserInfo;
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

  /**
   * 게시물의 상세 정보를 얻어내는 메소드
   *
   * @param loginCookie 로그인 정보
   * @param userAgent   모바일 기기 UserAgent
   * @param articleUrl  게시물의 URL
   * @return 게시물 상세 정보
   * @throws IOException the io exception
   */
  ArticleDetail getArticleDetail(Map<String, String> loginCookie, String userAgent, String articleUrl) throws IOException {
    Document articleRawData = getArticleRawData(loginCookie, userAgent, articleUrl);
    ArticleDetail article = new ArticleDetail();

    article.setTitle(articleRawData.select(".tit_view").first().text());
    Elements userEles = articleRawData.select("span.info_edit > span").first().children();
    setUserInfoAndDate(article, userEles);

    article.setUrl(articleUrl);
    article.setViewCount(Integer.parseInt(articleRawData.select(".txt_info .num").first().text()));
    article.setRecommendCount(Integer.parseInt(articleRawData.select("#recomm_btn").html()));
    article.setContentDataList(getContentData(articleRawData));
    article.setComments(getComments(articleRawData.select(".inner_best")));
    article.setCommentWriteData(getCommentWriteData(articleRawData));
    article.setCommentDeleteData(getCommentDeleteData(articleRawData));
    article.setArticleDeleteData(getArticleDeleteData(articleRawData.select("#board_del").first()));
    article.setRecommendData(getRecommendData(articleRawData.getElementsByTag("script")));

    // 수정가능하면
    Element editButton = articleRawData.select("button.edit").first();
    if(editButton != null) {
      String modifyUrl = editButton.attr("onClick")
                                    .replaceAll("'", "")
                                    .replace("location.href=", "");
      article.setModifyUrl(modifyUrl);
    }


    return article;
  }

  private void setUserInfoAndDate(ArticleDetail article, Elements userElements) {
    if(userElements.size() > 2) {
      UserInfo userInfo = new UserInfo(userElements.first().text(),
              CommonService.getUserType(userElements.get(1)));
      article.setUserInfo(userInfo);
      article.setDate(userElements.get(2).text());
    } else {
      UserInfo userInfo = new UserInfo(userElements.first().text()
                            + "(" + userElements.parents().next().select(".ip").text() + ")",
                                    UserInfo.UserType.FLOW);
      article.setUserInfo(userInfo);
      article.setDate(userElements.get(1).text());
    }
  }

  // 새로운 내용 파서. 순서를 유지한다.
  public List<ArticleDetail.ContentData> getContentData(Document articleRawData) throws IOException {
    List<ArticleDetail.ContentData> list = new ArrayList<>();

    // 무조건 맨 위에 나오는 이미지들
    Element contentImgs = articleRawData.select("p.contents_img").first();
    for (Element content : contentImgs.children()) {
      if(!content.nodeName().equals("a")) continue;

      String imageUrl = content.select("a > img").attr("abs:src");
      String onClickUrl = content.select("a").attr("onclick");


      ArticleDetail.ContentData contentData = new ArticleDetail.ContentData();
      contentData.setImageUrl(imageUrl);
      if(!onClickUrl.isEmpty()) { // 스케일링 되지않은 큰 이미지 주소
        String linkUrl = onClickUrl.split("\'")[1];
        String imageLinkUrl = imageUrl + linkUrl.split("\\?")[1];
        contentData.setLinkUrl(imageLinkUrl);
      }
      list.add(contentData);
    }

    Element contentTd = articleRawData.select("div#memo_img > table > tbody > tr > td").first();

    ArticleDetail.ContentData contentData = new ArticleDetail.ContentData();
    for (Node node : contentTd.childNodes()) {
        contentData = nodeToData(list, contentData, node);
    }
    list.add(contentData);
    return list;
  }

  // 노드를 데이터로 변환하는 메소드
  public ArticleDetail.ContentData nodeToData(List<ArticleDetail.ContentData> list, ArticleDetail.ContentData contentData, Node node) {

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

    } else if (node.nodeName().equals("embed")) {
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

  // 개념글 추천 데이터를 얻어내는 메소드
  private ArticleDetail.RecommendData getRecommendData(Elements scripts) {
    ArticleDetail.RecommendData recommendData = new ArticleDetail.RecommendData();

    for (Element script : scripts) {
      for (DataNode dataNode : script.dataNodes()) {
        String wholeData = dataNode.getWholeData();
        String[] arr = wholeData.split(";");
        for (String s : arr) {
          if (s.contains("&gno="))
            recommendData.setGno(s.split("gno=")[1].split("\"")[0].trim());
          else if (s.contains("&ip="))
            recommendData.setIp(s.split("ip=")[1].split("\"")[0].trim());
          else if (s.contains("&gserver="))
            recommendData.setGserver(s.split("gserver=")[1].split("\"")[0].trim());
          else if (s.contains("&category_no="))
            recommendData.setCategory_no(s.split("category_no=")[1].split("\"")[0].trim());
          else if (s.contains("&ko_name="))
            recommendData.setKo_name(s.split("ko_name=")[1].split("\"")[0].trim());
          else if (s.contains("&gall_id="))
            recommendData.setGall_id(s.split("gall_id=")[1].split("\"")[0].trim());
          else if (s.contains("no=")) {
            recommendData.setNo(s.split("no=")[1].split("\"")[0].trim());
          }
        }
      }
    }

    return recommendData;
  }


  // 게시물의 rawData를 얻어오는 메소드
  private Document getArticleRawData(Map<String, String> loginCookie, String userAgent, String articleUrl) throws IOException {
    return Jsoup.connect(articleUrl)
            .userAgent(userAgent)
            .cookies(loginCookie)
            .timeout(3000)
            .header("Origin", "http://m.dcinside.com")
            .header("Referer", "http://m.dcinside.com/login.php?r_url=m.dcinside.com%2Findex.php")
            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("Accept-Encoding", "gzip, deflate")
            .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
            .get();
  }


  // 게시물 삭제를 위한 정보를 얻어오는 메소드
  private ArticleDetail.ArticleDeleteData getArticleDeleteData(Element element) {
    ArticleDetail.ArticleDeleteData articleDeleteData = new ArticleDetail.ArticleDeleteData();
    articleDeleteData.setNo(element.select("#no").val());
    articleDeleteData.setId(element.select("#id").val());
    articleDeleteData.setMode(element.select("#mode").val());
    articleDeleteData.setPage(element.select("#page").val());
    articleDeleteData.setUser_no(element.select("#user_no").val());
    return articleDeleteData;
  }


  // 댓글 삭제 정보를 읽는 메소드
  private ArticleDetail.CommentDeleteData getCommentDeleteData(Document doc) {
    ArticleDetail.CommentDeleteData delData = new ArticleDetail.CommentDeleteData();

    delData.setId(doc.select("#id").attr("value"));
    delData.setNo(doc.select("#no").attr("value"));
    delData.setBoard_id(doc.select("#board_id").attr("value"));
    delData.setBest_chk(doc.select("#best_chk").attr("value"));
    delData.setBest_comno(doc.select("#best_comno").attr("value"));
    delData.setBest_comid(doc.select("#best_comid").attr("value"));
    delData.setUser_no(doc.select("#user_no").attr("value"));
    delData.setMode("comment_del");

    return delData;
  }

  // 댓글 작성 정보를 읽는 메소드
  private ArticleDetail.CommentWriteData getCommentWriteData(Document doc) {
    ArticleDetail.CommentWriteData cwd = new ArticleDetail.CommentWriteData();

    cwd.setMode(doc.select("#mode").attr("value"));
    cwd.setVoice_file(doc.select("#voice_file").attr("value"));
    cwd.setNo(doc.select("#no").attr("value"));
    cwd.setId(doc.select("#id").attr("value"));
    cwd.setBoard_id(doc.select("#board_id").attr("value"));
    cwd.setUser_no(doc.select("#user_no").attr("value"));
    cwd.setKo_name(doc.select("#ko_name").attr("value"));
    cwd.setSubject(doc.select("#subject").attr("value"));
    cwd.setBoard_name(doc.select("#board_name").attr("value"));
    cwd.setDate_time(doc.select("#date_time").attr("value"));
    cwd.setIp(doc.select("#ip").attr("value"));
    cwd.setBest_chk(doc.select("#best_chk").attr("value"));
    cwd.setUserToken(doc.select("#userToken").attr("value"));

    return cwd;
  }


  // 댓글들을 읽어오는 메소드
  private List<Comment> getComments(Elements innerBestSpan) {
    List<Comment> comments = new ArrayList<>();

    for (Element e : innerBestSpan) {

      Comment comment = new Comment(
              new UserInfo(e.select(".id").text()
                      .replace("[", "")
                      .replace("]", "").trim(),
                      CommonService.getUserType(e.select("a span").first())),
              e.select(".ip").text().trim(),
              e.select(".title .txt").text().trim(),
              e.select(".info .date").text(),
              e.select(".title .txt img").attr("abs:src"),
              getCommentDeleteUrl(e.select(".btn_delete").first())
      );

      comments.add(comment);
    }


    return comments;
  }

  // 댓글의 삭제 url을 읽는 메소드
  private String getCommentDeleteUrl(Element delBtn) {
    if(delBtn == null) return "";

    String value = delBtn.attr("href");
    if (value.isEmpty()) return "";
    String[] s = value.split("'");
    if (s.length > 0) return s[1];
    else return "";
  }

}
package dc.maitetsufd.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @since 2017-04-21
 */

@Data
public class ArticleDetail implements Serializable{
  private String title;
  private UserInfo userInfo;
  private String date;
  private int recommendCount;
  private int noRecommendCount;
  private int viewCount;
  private String url;
  private String boardId;
  private String no;
  private String modifyUrl;

  private List<Comment> comments = new ArrayList<>();
  private List<ContentData> contentDataList;
  private CommentWriteData commentWriteData;
  private CommentDeleteData commentDeleteData;
  private RecommendData recommendData;
  private ArticleDeleteData articleDeleteData;

  @Data
  @NoArgsConstructor
  public static class ContentData implements Serializable {
    private StringBuilder text = new StringBuilder();
    private String imageUrl = "";
    private String linkUrl = "";
    private String embedUrl = "";

    public boolean isEmpty(){
      return text.toString().isEmpty() && imageUrl.isEmpty() && linkUrl.isEmpty() && embedUrl.isEmpty();
    }
  }



  /**
   * @since 2017-04-21
   */
  @Data
  public static class CommentDeleteData implements Serializable {
    private String comment_no;
    private String id;
    private String no;
    private String best_chk;
    private String board_id;
    private String con_key;
    private String csrfToken;
  }

  /**
   * @since 2017-04-21
   */
  @Data
  public static class CommentWriteData implements Serializable {
    private String comment_memo;
    private String comment_nick;
    private String comment_pw;
    private String mode = "com_write";
    private String comment_no;
    private String id;
    private String no;
    private String best_chk;
    private String subject;
    private String board_id;
    private String reple_id;
    private String cpage;
    private String con_key;
    private String csrfToken;
  }

  /**
   * @since 2017-04-22
   */

  @Data
  public static class RecommendData implements Serializable {
    private String no;
    private String gall_id;
    private String ko_name;
    private String category_no;
    private String gserver;
    private String ip;
    private String gno;
  }

  /**
   * @since 2017-04-22
   */
  @Data
  public static class ArticleDeleteData implements Serializable {

    private String no;
    private String id;
    private String con_key;

  }
}

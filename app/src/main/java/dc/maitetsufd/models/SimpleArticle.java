package dc.maitetsufd.models;

import lombok.Data;

import java.io.Serializable;

/**
 * @since 2017-04-21
 */

@Data
public class SimpleArticle implements Serializable{

  private String title;
  private String type;
  private String date;
  private int commentCount;
  private int viewCount;
  private int recommendCount;
  private String url;
  private UserInfo userInfo;
  private ArticleType articleType;

  public enum ArticleType {
    IMG, NO_IMG, MOV, RECOMMAND
  }
}

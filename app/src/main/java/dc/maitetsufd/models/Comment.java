package dc.maitetsufd.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @since 2017-04-21
 */

@Data
@AllArgsConstructor
public class Comment implements Serializable {
  private boolean addComment;
  private UserInfo userInfo;
  private String ip;
  private String content;
  private String date;
  private String imgUrl;
  private String deleteCode;
}

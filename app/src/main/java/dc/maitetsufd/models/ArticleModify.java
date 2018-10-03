package dc.maitetsufd.models;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @since 2017-05-07
 */
@Data
public class ArticleModify implements Serializable{
  private String title;
  private String content;
  private List<AttachFile> attachFileList;
  private Map<String, String> articleWriteDataList;
  private List<String> deleteFileList = new ArrayList<>();

  @Data
  public static class AttachFile implements Serializable{
    private String fno;
    private String order;
    private String name;
    private String src;
  }
}

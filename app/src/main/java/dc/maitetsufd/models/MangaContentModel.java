package dc.maitetsufd.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @since 2017-04-29
 */
@Data
public class MangaContentModel {
  private String no;
  private String url;
  private String origin;
  private String title;
  private int episodeNum;
  private List<String> imagesUrls;
  private List<MaruEpisode> episodes;

  @Data
  @AllArgsConstructor
  public static class MaruEpisode {
    private String episodeName;
    private String episodeNo;
  }
}

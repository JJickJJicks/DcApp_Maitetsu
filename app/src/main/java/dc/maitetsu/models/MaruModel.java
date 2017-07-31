package dc.maitetsu.models;

import lombok.Data;

import java.util.List;

/**
 * @since 2017-04-29
 */
@Data
public class MaruModel {
  private String no;
  private String url;
  private String origin;
  private List<String> imagesUrls;
}

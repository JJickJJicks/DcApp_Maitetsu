package dc.maitetsu.models;

import lombok.Data;

import java.util.List;

/**
 * @author Park Hyo Jun
 * @since 2017-04-29
 */
@Data
public class MaruModel {
  private String url;
  private List<String> imagesUrls;
}

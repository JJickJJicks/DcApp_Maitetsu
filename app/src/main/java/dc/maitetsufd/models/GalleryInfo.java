package dc.maitetsufd.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @since 2017-04-22
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GalleryInfo implements Serializable {
  private String galleryName;
  private String galleryCode;
}

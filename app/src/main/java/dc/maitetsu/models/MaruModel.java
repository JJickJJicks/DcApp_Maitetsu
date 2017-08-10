package dc.maitetsu.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @since 2017-04-29
 */


@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaruModel implements Serializable{
  private String no;
  private String thumbUrl;
  private String title;
  private String date;
  private boolean isViewerModel;
}

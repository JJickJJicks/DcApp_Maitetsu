package dc.maitetsufd.models;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @since 2017-04-22
 */
@Data
public class DcConPackage implements Serializable {

  private String dccon_package;
  private String dccon_package_src;
  public List<DcCon> dcCons = new ArrayList<>();

  @Data
  public static class DcCon implements Serializable {
    private String dccon_package;
    private String dccon_src;
    private String dccon_detail;
  }
}

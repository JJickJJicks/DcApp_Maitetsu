package dc.maitetsu.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @since 2017-04-21
 */
@Data
@AllArgsConstructor
public class UserInfo implements Serializable {

  private String nickname;
  private UserType userType;
  private String ipAdd;

  public enum UserType {
    FIX_GALLOG, FLOW_GALLOG, FLOW
  }
}

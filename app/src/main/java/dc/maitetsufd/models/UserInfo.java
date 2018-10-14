package dc.maitetsufd.models;

import lombok.*;

import java.io.Serializable;

/**
 * @since 2017-04-21
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class UserInfo implements Serializable {

  private String nickname;
  private String gallogId;
  private UserType userType;
  private String ipAdd;

  public enum UserType {
    SUBMANAGER_GALLOG, MANAGER_GALLOG, FIX_GALLOG, FLOW_GALLOG, FLOW, EMPTY
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof UserInfo)) return false;
    UserInfo userInfo = (UserInfo) obj;
    if (!this.gallogId.isEmpty() // 갤로그 아이디 차단
            && userInfo.gallogId.equals(this.gallogId)) return true;
    return userInfo.nickname.equals(this.nickname) && userInfo.userType == this.userType;
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }
}

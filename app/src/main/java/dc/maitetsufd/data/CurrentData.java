package dc.maitetsufd.data;

import dc.maitetsufd.models.DcConPackage;
import dc.maitetsufd.models.GalleryInfo;
import dc.maitetsufd.models.UserInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @since 2017-04-22
 */

@Data
public class CurrentData implements Serializable {
  public static boolean resetMode = false;

  private String id;
  private String pw;
  private Map<String, String> loginCookies;
  private Map<String, String> maruCookies;
  private List<GalleryInfo> myGalleryList = new ArrayList<>();
  private List<UserInfo> filterUserList = new ArrayList<>();
  private List<DcConPackage> dcConPackages = new ArrayList<>();
  private Map<String, Long> recommendList = new HashMap<>();
  private int recommendCount = 0;
  private int page = 1;
  private String searchWord = "";
  private boolean fastLogin;
  private boolean imageCheck;
  private boolean dcconCheck;
  private boolean telcomFilter;
  private boolean flowFilter;
  private boolean articleTabVib;
  private boolean articleCloseVib;
  private boolean darkTheme;
  private boolean touchImageOpen;
  private boolean maruViewer;
  private boolean movieIgnore;
  private long lastLogin;
  private int serPos;
  private int nextSerPos;
  private boolean isSplitLoad;

  public GalleryInfo getGalleryInfo() {
    GalleryInfo galleryInfo;
    if (myGalleryList.size() > 0)
      galleryInfo = myGalleryList.get(0);
    else
      galleryInfo = new GalleryInfo("HIT", "hit");
    return galleryInfo;
  }

}

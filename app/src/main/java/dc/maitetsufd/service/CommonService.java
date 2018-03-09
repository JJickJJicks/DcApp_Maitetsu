package dc.maitetsufd.service;

import dc.maitetsufd.models.UserInfo;
import org.jsoup.nodes.Element;

/**
 * @since 2017-04-21
 */
class CommonService {

  // 유저 타입 읽어내는 메소드
  static UserInfo.UserType getUserType(Element e) {
    if(e == null) return UserInfo.UserType.FLOW;

    Element userFlow = e.select(".nick_comm,.nick_mnr_comm").first();
    if(userFlow == null) return UserInfo.UserType.FLOW;
    else if (userFlow.hasClass("flow")
              || userFlow.hasClass("ic_gc_m") // 매니저
              || userFlow.hasClass("ic_sc_m")) // 부매니저
      return UserInfo.UserType.FIX_GALLOG;
    else
      return UserInfo.UserType.FLOW_GALLOG;
  }

}

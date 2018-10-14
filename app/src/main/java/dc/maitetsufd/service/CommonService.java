package dc.maitetsufd.service;

import dc.maitetsufd.models.UserInfo;
import org.jsoup.nodes.Element;

/**
 * @since 2017-04-21
 */
class CommonService {

  // 유저 타입 읽어내는 메소드
  static UserInfo.UserType getUserType(Element e) {
    if (e == null) return UserInfo.UserType.FLOW;

    Element userFlow = e.select("span.sp-nick").first();
    if (userFlow == null) return UserInfo.UserType.FLOW;
    else if (userFlow.hasClass("gonick"))
      return UserInfo.UserType.FIX_GALLOG;

    else if (userFlow.hasClass("m-gonick") || userFlow.hasClass("m-nogonick")) // 매니저
      return UserInfo.UserType.MANAGER_GALLOG;

    else if (userFlow.hasClass("sub-gonick") || userFlow.hasClass("sub-nogonick")) // 부매니저
      return UserInfo.UserType.SUBMANAGER_GALLOG;

    else
      return UserInfo.UserType.FLOW_GALLOG;
  }

}

package dc.maitetsufd.utils;

import dc.maitetsufd.data.CurrentData;
import dc.maitetsufd.models.Comment;
import dc.maitetsufd.models.SimpleArticle;
import dc.maitetsufd.models.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 2017-04-24
 *
 * 차단된 유저 글을 지워버림
 */
public class UserFilter {

  public static void setSimpleArticles(CurrentData currentData, List<SimpleArticle> simpleArticles) {
    List<SimpleArticle> deleteList = new ArrayList<>();
    for (SimpleArticle simpleArticle : simpleArticles) {
      UserInfo userInfo = simpleArticle.getUserInfo();
      if (currentData.getFilterUserList().contains(userInfo)
              || checkIp(currentData.getFilterUserList(), userInfo.getIpAdd())) deleteList.add(simpleArticle);
      else if (currentData.isTelcomFilter()
              && currentData.isFlowFilter()
              && userInfo.getUserType() == UserInfo.UserType.FLOW) deleteList.add(simpleArticle);
    }
    simpleArticles.removeAll(deleteList);
    deleteList.clear();
  }

public static void setComments(CurrentData currentData, List<Comment> comments) {
    List<Comment> deleteList = new ArrayList<>();
    for(Comment comment : comments) {
      UserInfo userInfo = comment.getUserInfo();
      String ip = userInfo.getIpAdd();
      if (currentData.getFilterUserList().contains(userInfo)
              || checkIp(currentData.getFilterUserList(), ip)) deleteList.add(comment);
      else if(currentData.isTelcomFilter()
              && currentData.isFlowFilter()
              && userInfo.getUserType() == UserInfo.UserType.FLOW) deleteList.add(comment);
      else if(currentData.isTelcomFilter()
              && TelcomIp.get().contains(ip)) deleteList.add(comment);
    }
    comments.removeAll(deleteList);
    deleteList.clear();
}

private static boolean checkIp(List<UserInfo> blockedUser, String ip) {
  for (UserInfo userInfo : blockedUser) {
    if(userInfo.getIpAdd().contains(ip)
            && !ip.isEmpty()) return true;
  }
  return false;
}


}

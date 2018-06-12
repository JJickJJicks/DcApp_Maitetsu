package dc.maitetsufd.utils;

import android.util.Log;
import dc.maitetsufd.data.CurrentData;
import dc.maitetsufd.models.Comment;
import dc.maitetsufd.models.SimpleArticle;
import dc.maitetsufd.models.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 2017-04-24
 * <p>
 * 차단된 유저 글을 지워버림
 */
public class ContentFilter {

  public static void setSimpleArticles(CurrentData currentData, List<SimpleArticle> simpleArticles) {
    List<SimpleArticle> deleteList = new ArrayList<>();
    for (SimpleArticle simpleArticle : simpleArticles) {
      UserInfo userInfo = simpleArticle.getUserInfo();
      if (currentData.getFilterUserList().contains(userInfo) // 유저 차단
              || checkIp(currentData.getFilterUserList(), userInfo.getIpAdd())) deleteList.add(simpleArticle);
      else if (currentData.isTelcomFilter() // 유동 차단
              && currentData.isFlowFilter()
              && userInfo.getUserType() == UserInfo.UserType.FLOW) deleteList.add(simpleArticle);
      else if (currentData.isTelcomFilter() && TelcomIp.get().contains(userInfo.getIpAdd())) // 통신사 아이피 차단
        deleteList.add(simpleArticle);
      else if (checkBlockWord(currentData.getBlockWordList(), simpleArticle.getTitle().toUpperCase())) // 단어 차단
        deleteList.add(simpleArticle);
    }
    simpleArticles.removeAll(deleteList);
    deleteList.clear();
  }

  /**
   * 댓글 삭제
   *
   * @param currentData
   * @param comments
   */
  public static void setComments(CurrentData currentData, List<Comment> comments) {
    List<Comment> deleteList = new ArrayList<>();
    for (Comment comment : comments) {
      UserInfo userInfo = comment.getUserInfo();
      String ip = userInfo.getIpAdd();
      if (currentData.getFilterUserList().contains(userInfo) //
              || checkIp(currentData.getFilterUserList(), ip)) deleteList.add(comment);
      else if (currentData.isTelcomFilter() // 유동차단
              && currentData.isFlowFilter()
              && userInfo.getUserType() == UserInfo.UserType.FLOW) deleteList.add(comment);
      else if (currentData.isTelcomFilter() // 통신사 아이피 차단
              && TelcomIp.get().contains(ip)) deleteList.add(comment);
      else if (checkBlockWord(currentData.getBlockWordList(), comment.getContent().toUpperCase())) // 단어차단
        deleteList.add(comment);
    }
    comments.removeAll(deleteList);
    deleteList.clear();
  }

  /**
   * IP체크
   *
   * @param blockedUser
   * @param ip
   * @return
   */
  private static boolean checkIp(List<UserInfo> blockedUser, String ip) {
    for (UserInfo userInfo : blockedUser) {
      if (userInfo.getIpAdd().contains(ip)
              && !ip.isEmpty()) return true;
    }
    return false;
  }

  /**
   * 차단 단어 체크
   * @param blockWords
   * @param content
   * @return
   */
  private static boolean checkBlockWord(List<String> blockWords, String content) {
    for (String word: blockWords) {
      if (content.contains(word)) return true;
    }
    return false;
  }


}

package dc.maitetsufd.service;

import android.util.Log;
import dc.maitetsufd.models.DcConPackage;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @since 2017-04-22
 */
enum DcConService {
  getInstance;

  private static final String DCINSIDE_MAIN = "http://m.dcinside.com";
  private static final String DCCON_LIST_URL = "http://m.dcinside.com/dccon/dccon_box_tpl.php";
  private static final String DCCON_DETAIL_URL = "http://m.dcinside.com/dccon/dccon_tpl.php";

  /**
   * 사용가능한 디씨콘 리스트를 얻어오는 메소드
   *
   * @param loginCookie 유저 로그인 정보
   * @param userAgent   모바일 기기 UserAgent
   * @return 디씨콘 리스트
   * @throws IOException the io exception
   */
  List<DcConPackage> getDcConList(Map<String, String> loginCookie, String userAgent) {
    Document dcConRawData = getDcConTabRawData(loginCookie, userAgent);
    List<DcConPackage> dcConPackages = getDcConPackage(dcConRawData.select(".dccon-tab img"));

    for(int i=0; i<dcConPackages.size(); i++){
      Document dcConDetailRawData = getDcConRawData(loginCookie, userAgent, i);
      setDcConPackageDetail(dcConPackages, i, dcConDetailRawData
                                              .select("button.dccon-icon-btn"));
    }

    return dcConPackages;
  }


  // DcCon 리스트의 rawData를 얻어내는 메소드
  private Document getDcConTabRawData(Map<String, String> loginCookie, String userAgent) {
    Connection.Response response;
    try {
      response = Jsoup.connect(DCCON_LIST_URL)
                      .userAgent(userAgent)
                      .header("Host", "m.dcinside.com")
                      .header("Origin", DCINSIDE_MAIN)
                      .referrer(DCINSIDE_MAIN)
                      .header("X-Requested-With", "XMLHttpRequest")
                      .ignoreContentType(true)
                      .ignoreHttpErrors(true)
                      .cookies(loginCookie)
                      .method(Connection.Method.POST)
                      .execute();
      loginCookie.putAll(response.cookies());
      return response.parse();

    } catch(Exception e) {
      return getDcConTabRawData(loginCookie, userAgent);

    }
  }

  // DcCon 상세정보 rawData를 얻어내는 메소드
  private Document getDcConRawData(Map<String, String> loginCookie, String userAgent, int i) {

    try {
      Connection.Response response = Jsoup.connect(DCCON_DETAIL_URL)
                                          .userAgent(userAgent)
                                          .header("Host", "m.dcinside.com")
                                          .header("Origin", DCINSIDE_MAIN)
                                          .referrer(DCINSIDE_MAIN)
                                          .header("X-Requested-With", "XMLHttpRequest")
                                          .ignoreContentType(true)
                                          .ignoreHttpErrors(true)
                                          .cookies(loginCookie)
                                          .data("idx", String.valueOf(i + 1))
                                          .method(Connection.Method.POST)
                                          .execute();

      return response.parse();
    } catch (Exception e) {
      return getDcConRawData(loginCookie, userAgent, i);

    }
  }

  // 디시콘 패키지 리스트를 얻는 메소드
  private List<DcConPackage> getDcConPackage(Elements elements) {
    List<DcConPackage> dcConPackages = new ArrayList<>();

    for (Element e : elements) {
      DcConPackage dcConPackage = new DcConPackage();
      dcConPackage.setDccon_package_src(e.attr("abs:src"));

      dcConPackages.add(dcConPackage);
    }

    dcConPackages.remove(0); // 최근 사용한 디씨콘은 제외
    return dcConPackages;
  }


  // dcConPackage 상세 정보를 얻어오는 메소드
  private void setDcConPackageDetail(List<DcConPackage> dcConPackages, int i, Elements elements) {

    for (Element e : elements) {
      DcConPackage.DcCon dcCon = new DcConPackage.DcCon();

      String dccon_package = e.attr("data-dccon-package");
      dcCon.setDccon_package(dccon_package);
      dcCon.setDccon_detail(e.attr("data-dccon-detail"));
      dcCon.setDccon_src(e.attr("data-dccon-src"));


      if(dcConPackages.get(i).getDccon_package() != null
          && !dcConPackages.get(i).getDccon_package().equals(dccon_package) ){
        i++;
      }

      dcConPackages.get(i).setDccon_package(dccon_package);
      dcConPackages.get(i).getDcCons().add(dcCon);
    }
  }


}

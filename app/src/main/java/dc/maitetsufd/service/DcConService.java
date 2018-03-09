package dc.maitetsufd.service;

import dc.maitetsufd.models.DcConPackage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @since 2017-04-22
 */
enum DcConService {
  getInstance;

  private static final String DCCON_LIST_URL = "http://m.dcinside.com/dccon/dccon_box_tpl.php";

  /**
   * 사용가능한 디씨콘 리스트를 얻어오는 메소드
   *
   * @param loginCookie 유저 로그인 정보
   * @param userAgent   모바일 기기 UserAgent
   * @return 디씨콘 리스트
   * @throws IOException the io exception
   */
  List<DcConPackage> getDcConList(Map<String, String> loginCookie, String userAgent) throws IOException {
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
  private Document getDcConTabRawData(Map<String, String> loginCookie, String userAgent) throws IOException {
    return Jsoup
            .connect("http://m.dcinside.com/dccon/dccon_box_tpl.php")
            .userAgent(userAgent)
            .header("Origin", "http://m.dcinside.com")
            .header("Accept", "text/html, */*; q=0.01")
            .header("Accept-Encoding", "gzip, deflate")
            .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
            .header("X-Requested-With", "XMLHttpRequest")
            .ignoreContentType(true)
            .cookies(loginCookie)
            .post();
  }

  // DcCon 상세정보 rawData를 얻어내는 메소드
  private Document getDcConRawData(Map<String, String> loginCookie, String userAgent, int i) throws IOException {
    return Jsoup
            .connect("http://m.dcinside.com/dccon/dccon_tpl.php")
            .userAgent(userAgent)
            .header("Origin", "http://m.dcinside.com")
            .header("Referer", "http://m.dcinside.com")
            .header("Accept", "text/html, */*; q=0.01")
            .header("Accept-Encoding", "gzip, deflate")
            .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
            .header("X-Requested-With", "XMLHttpRequest")
            .ignoreContentType(true)
            .cookies(loginCookie)
            .data("idx", String.valueOf(i + 1))
            .post();
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

package dc.maitetsu.service;

import dc.maitetsu.models.GalleryInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Park Hyo Jun
 * @since 2017-04-22
 */
enum GalleryListService {
  getInstance;

  private static final String GALLERY_SEARCH_URL = "http://m.dcinside.com/search_keyword.php";

  List<GalleryInfo> searchGallery(String userAgent, String name) throws IOException {
    Document searchRawData = Jsoup.connect(GALLERY_SEARCH_URL)
            .userAgent(userAgent)
            .header("Origin", "http://m.dcinside.com")
            .header("Referer", "http://m.dcinside.com")
            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
            .header("Accept-Encoding", "gzip, deflate")
            .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
            .header("X-Requested-With", "XMLHttpRequest")
            .data("search_gall", name)
            .data("search_type", "gall_name")
            .get();

    return getGalleryInfoFromRawData(searchRawData);
  }

  // RawData를 GalleryInfo 리스트로 바꾸는 메소드
  private List<GalleryInfo> getGalleryInfoFromRawData(Document searchRawData) {
    List<GalleryInfo> result = new ArrayList<>();

    Elements h4Elements = searchRawData.select("h4.result_tit");
    for(Element e : h4Elements) {
      String resultType = e.select("span").first().text();
      if(resultType.contains("갤러리 검색결과") || resultType.contains("마이너 갤러리 검색결과")){
        elementToGalleryInfo(e.nextElementSibling(), result);
      }
    }

      return result;
  }



  // li Element를 GalleryInfo 객체로 바꾸는 메소드
  private void elementToGalleryInfo(Element e, List<GalleryInfo> galleryInfos) {
    Elements aElements = e.select("a");
    for(Element ae : aElements) {
      GalleryInfo galleryInfo = new GalleryInfo();
      galleryInfo.setGalleryName(ae.text());
      galleryInfo.setGalleryCode(ae.attr("abs:href").split("=")[1]);
      galleryInfos.add(galleryInfo);
    }
  }




}

package dc.maitetsufd.service;

import dc.maitetsufd.models.GalleryInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @since 2017-04-22
 */
public enum GalleryListService {
  getInstance;

  private static final String DCINSIDE_MAIN = "http://m.dcinside.com";
  private static final String GALLERY_SEARCH_URL = "http://m.dcinside.com/search_g";

  public List<GalleryInfo> searchGallery(String userAgent, String name) throws IOException {
    Document searchRawData = Jsoup.connect(GALLERY_SEARCH_URL)
                                  .userAgent(userAgent)
                                  .header("Host", "m.dcinside.com")
                                  .header("Origin", DCINSIDE_MAIN)
                                  .referrer(DCINSIDE_MAIN)
                                  .header("X-Requested-With", "XMLHttpRequest")
                                  .data("keyword", name)
                                  .get();

    return getGalleryInfoFromRawData(searchRawData);
  }

  // RawData를 GalleryInfo 리스트로 바꾸는 메소드
  private List<GalleryInfo> getGalleryInfoFromRawData(Document searchRawData) {
    List<GalleryInfo> result = new ArrayList<>();

    Elements liElements = searchRawData.select("ul.gall-lst li");
    for (Element liElement : liElements) {
      String href = liElement.select("a").first().attr("abs:href");
      String name = liElement.text();

      if (href.contains("http://wiki.dcinside.com/"))
        continue;

      String[] urls = href.split("/");

      GalleryInfo info = new GalleryInfo();
      info.setGalleryName(name);
      info.setGalleryCode(urls[urls.length-1]);
      result.add(info);
    }

    return result;
  }

}

package dc.maitetsufd.service;

import dc.maitetsufd.models.MangaContentModel;
import dc.maitetsufd.models.MangaSimpleModel;

import java.io.IOException;
import java.util.List;

/**
 * @author Park Hyo Jun
 * @since 2017-08-14
 */
public interface IMangaService {
  List<MangaSimpleModel> getSimpleModels(String userAgent, int page, String keyword) throws IOException;
  MangaContentModel getContentModel(String userAgent, String no, boolean isViewerModel, int count) throws IOException;

}

package dc.maitetsu.service;

import dc.maitetsu.models.MangaContentModel;
import dc.maitetsu.models.MangaSimpleModel;
import dc.maitetsu.ui.fragment.MangaViewerFragment;

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

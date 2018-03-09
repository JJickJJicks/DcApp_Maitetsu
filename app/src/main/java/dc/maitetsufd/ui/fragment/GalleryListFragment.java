package dc.maitetsufd.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import dc.maitetsufd.R;
import dc.maitetsufd.data.CurrentData;
import dc.maitetsufd.data.CurrentDataManager;
import dc.maitetsufd.ui.viewmodel.GalleryListViewModel;

/**
 *  갤러리 리스트 프래그먼트.
 */
public class GalleryListFragment extends Fragment {
  public GalleryListViewModel galleryListViewModel;

  public GalleryListFragment() {
  }

  public static GalleryListFragment newInstance() {
    return new GalleryListFragment();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_gallery_list, container, false);
    CurrentData currentData = CurrentDataManager.getInstance(this.getContext());
    galleryListViewModel = new GalleryListViewModel(this, view, currentData);
    return view;
  }



  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    if(getView() != null)
      imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
  }



}

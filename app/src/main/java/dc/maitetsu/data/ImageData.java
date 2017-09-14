package dc.maitetsu.data;

import android.util.SparseArray;

import java.lang.ref.WeakReference;

/**
 * @since 2017-05-12
 */
public class ImageData {
  private static SparseArray<WeakReference<byte[]>> holdImageBytes;

  public synchronized static void setImageBytes(SparseArray<WeakReference<byte[]>> imageBytes) {holdImageBytes = imageBytes; }
  public synchronized static SparseArray<WeakReference<byte[]>> getHoldImageBytes() { return holdImageBytes; }
  public synchronized static void clearHoldImageBytes(){ if(holdImageBytes != null) holdImageBytes.clear(); }

}

package dc.maitetsu.data;

import android.util.SparseArray;

/**
 * @since 2017-05-12
 */
public class ImageData {
  private static SparseArray<byte[]> holdImageBytes;

  public synchronized static void setImageBytes(SparseArray<byte[]> imageBytes) {holdImageBytes = imageBytes; }
  public synchronized static SparseArray<byte[]> getHoldImageBytes() { return holdImageBytes; }
  public synchronized static void clearHoldImageBytes(){ if(holdImageBytes != null) holdImageBytes.clear(); }

}

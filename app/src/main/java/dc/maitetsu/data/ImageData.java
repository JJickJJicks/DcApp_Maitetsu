package dc.maitetsu.data;

import java.util.Map;

/**
 * @since 2017-05-12
 */
public class ImageData {
  private static Map<Integer, byte[]> holdImageBytes;

  public synchronized static void setImageBytes(Map<Integer, byte[]> imageBytes) {holdImageBytes = imageBytes; }
  public synchronized static Map<Integer, byte[]> getHoldImageBytes() { return holdImageBytes; }
  public synchronized static void clearHoldImageBytes(){ if(holdImageBytes != null) holdImageBytes.clear(); }

}

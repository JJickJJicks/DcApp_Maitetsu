package dc.maitetsu.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author Park Hyo Jun
 * @since 2017-04-28
 *
 * 앱에서 사용되는 백그라운드 쓰레드.
 */
public class ThreadPoolManager {
  private static ExecutorService serviceEc = Executors.newFixedThreadPool(2, new ThreadFactory() {
    @Override
    public Thread newThread(Runnable runnable) {
                                        Thread t = Executors.defaultThreadFactory().newThread(runnable);
                                        t.setDaemon(true);
                                        return t;
                                      }
                                    });

  private static ExecutorService activityEc = Executors.newSingleThreadExecutor(new ThreadFactory() {
                                          @Override
                                          public Thread newThread(Runnable runnable) {
                                                                              Thread t = Executors.defaultThreadFactory().newThread(runnable);
                                                                              t.setDaemon(true);
                                                                              return t;
                                          }
                                        });


  public static ExecutorService getServiceEc() {
    return serviceEc;
  }
  public static ExecutorService getActivityEc() { return activityEc; }

}

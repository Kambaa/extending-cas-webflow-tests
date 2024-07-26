package org.apereo.cas.config;

import java.net.InetAddress;
import java.net.UnknownHostException;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang.StringUtils;

@UtilityClass
public class AppInfo {

  private static String host;
  private static String env;


  /**
   * hostname arg.
   *
   * @return {@link String}
   */
  public static String hostName() {
    if (StringUtils.isBlank(host)) {
      return host = calculateHost();
    }
    return host;
  }

  /**
   * location arg.
   *
   * @return {@link String}
   */
  public static String getEnvName() {
    if (StringUtils.isBlank(env)) {
      String fromK8 = System.getenv("myenv");
      String fromDev = System.getProperty("spring.profiles.active");
      return env = StringUtils.isBlank(fromK8) ? fromDev : fromK8;
    }
    return env;
  }

  private String calculateHost() {
    String hostname = System.getenv("HOSTNAME");
    if (StringUtils.isBlank(hostname)) {
      hostname = System.getProperty("computer.name");
    }
    if (StringUtils.isBlank(hostname)) {
      try {
        InetAddress addr = InetAddress.getLocalHost();
        hostname = addr.getHostName();
      } catch (UnknownHostException e) {
        System.err.println("Error: " + e.getMessage());
      }
    }
    if (hostname != null && hostname.length() > 5) {
      int i = hostname.lastIndexOf("-");
      return hostname.substring(i + 1);
    }
    return hostname;
  }

}
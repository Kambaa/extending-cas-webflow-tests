package org.apereo.cas.config;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

public class WebFlowCustomData implements Serializable {

  public String getRegistrationUrl() {
    String env = AppInfo.getEnvName();
    return String.format(
        "https://localhost:8443/registration-form",
        "prod".equalsIgnoreCase(env) ? "" : "-" + env
    );
  }

  public String getRandomVideoSrc() {
    List<String> vids = List.of(
        "/cas/themes/customtheme/img/big-buck-bunny_trailer.webm",
        "/cas/themes/customtheme/img/earth.webm",
        "/cas/themes/customtheme/img/shatter.webm"
    );
    return vids.get(new Random().nextInt(3));
  }

}

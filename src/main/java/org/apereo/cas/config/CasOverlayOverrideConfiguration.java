package org.apereo.cas.config;

import java.io.Serializable;
import java.util.List;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.pm.PasswordManagementService;
import org.apereo.cas.util.crypto.CipherExecutor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

@Configuration(value = "CasOverlayOverrideConfiguration", proxyBeanMethods = false)
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class CasOverlayOverrideConfiguration {

  @Autowired
  CasConfigurationProperties casConfigurationProperties;
  @Autowired
  CipherExecutor<Serializable, String> passwordManagementCipherExecutor;

  @Bean
  public PasswordManagementService passwordChangeService(
      CasConfigurationProperties casConfigurationProperties,
      List<InMemoryUser> inMemoryUserList
  ) {
    return new InMemoryPasswordManagementService(
        casConfigurationProperties.getAuthn().getPm(),
        passwordManagementCipherExecutor,
        "InternalPassChangeService",
        null,
        inMemoryUserList
    );
  }
}

package org.apereo.cas.config;

import java.util.List;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlan;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.ServicesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.apereo.cas.authentication.AuthenticationHandler;

@Configuration("InMemoryAuthenticationEventExecutionPlanConfiguration")
@EnableConfigurationProperties( {CasConfigurationProperties.class})
public class InMemoryAuthnHandlerConfig implements AuthenticationEventExecutionPlanConfigurer {

  @Autowired
  private CasConfigurationProperties casProperties;

  @Autowired
  private PrincipalFactory principalFactory;

  @Autowired
  private ServicesManager servicesManager;

  @Bean
  public List<InMemoryUser> customUserList() {
    return List.of(
        // You can use a temporary email from https://temp-mail.org
        new InMemoryUser("demo", "demo", "larab33568@digdy.com")
    );
  }

  /* here in 4th parameter, we can set the order of this handler is executed by CAS,
    if there are multiple authentication stratagies are avaible */
  @Bean
  public AuthenticationHandler inMemoryAuthenticationHandler() {

    final InMemoryAuthnHandler handler =
        new InMemoryAuthnHandler(
            "DEMO-IN-MEMORY-AUTHN",
            servicesManager,
            principalFactory,
            0,
            customUserList()
        );
    return handler;
  }

  @Override
  public void configureAuthenticationExecutionPlan(AuthenticationEventExecutionPlan plan) {
    plan.registerAuthenticationHandler(inMemoryAuthenticationHandler());
  }

}

package org.apereo.cas.config;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.web.flow.CasWebflowConfigurer;
import org.apereo.cas.web.flow.CasWebflowConstants;
import org.apereo.cas.web.flow.CasWebflowExecutionPlan;
import org.apereo.cas.web.flow.CasWebflowExecutionPlanConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;

@AutoConfiguration
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class WebFlowConfig implements CasWebflowExecutionPlanConfigurer {

  @Autowired
  private CasConfigurationProperties casProperties;

  /**
   * Login için flow definition registry.
   */
  @Autowired
  @Qualifier(CasWebflowConstants.BEAN_NAME_LOGIN_FLOW_DEFINITION_REGISTRY)
  private FlowDefinitionRegistry loginFlowDefinitionRegistry;

  /**
   * Logout için flow definition registry.
   */
  @Autowired
  @Qualifier(CasWebflowConstants.BEAN_NAME_LOGOUT_FLOW_DEFINITION_REGISTRY)
  private FlowDefinitionRegistry logoutFlowDefinitionRegistry;

  @Autowired
  private ConfigurableApplicationContext applicationContext;

  @Autowired
  private FlowBuilderServices flowBuilderServices;

  @ConditionalOnMissingBean(name = "loginFlowCustomDecoratorConfigurer")
  @Bean
  public CasWebflowConfigurer loginFlowCustomDecoratorConfigurer() {
    WebFlowCustomDecorator webFlowCustomDecorator = new WebFlowCustomDecorator(flowBuilderServices,
        loginFlowDefinitionRegistry, applicationContext, casProperties);
    webFlowCustomDecorator.setLogoutFlowDefinitionRegistry(logoutFlowDefinitionRegistry);
    return webFlowCustomDecorator;
  }

  @Override
  public void configureWebflowExecutionPlan(final CasWebflowExecutionPlan plan) {
    plan.registerWebflowConfigurer(loginFlowCustomDecoratorConfigurer());
  }
}

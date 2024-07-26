package org.apereo.cas.config;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.web.flow.configurer.AbstractCasWebflowConfigurer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;

public class WebFlowCustomDecorator extends AbstractCasWebflowConfigurer {
  public WebFlowCustomDecorator(FlowBuilderServices flowBuilderServices,
                                FlowDefinitionRegistry flowDefinitionRegistry,
                                ConfigurableApplicationContext applicationContext,
                                CasConfigurationProperties casProperties) {
    super(flowBuilderServices, flowDefinitionRegistry, applicationContext, casProperties);
  }

  @Override
  protected void doInitialize() {
    // This works as explained in the docs. You'll see customData working in the main page and video element will be random on each refresh.
    super.createFlowVariable(super.getLoginFlow(), "customData", WebFlowCustomData.class);

    // This does not work
    // super.createFlowVariable(super.getFlow("logout"), "customData", WebFlowCustomData.class);

    // But if you register the logoutFlowDefinitionRegistry by calling the parent class's
    // setLogoutFlowDefinitionRegistry method like done in the WebFlowConfig class,
    // this code will register the variable in logout and you'll see that logoutFlow has the customData.
    super.createFlowVariable(super.getLogoutFlow(), "customData", WebFlowCustomData.class);

    // This does not work either.
    super.createFlowVariable(super.getFlow("pswdreset"), "customData", WebFlowCustomData.class);
  }
}
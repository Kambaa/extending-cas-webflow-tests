package org.apereo.cas.config;

import com.google.common.collect.Maps;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.security.auth.login.FailedLoginException;
import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;

public class InMemoryAuthnHandler extends AbstractUsernamePasswordAuthenticationHandler {

  private final List<InMemoryUser> inMemoryUsers;

  protected InMemoryAuthnHandler(String name,
                                 ServicesManager servicesManager,
                                 PrincipalFactory principalFactory,
                                 Integer order, List<InMemoryUser> inMemoryUsers) {
    super(name, servicesManager, principalFactory, order);
    this.inMemoryUsers = inMemoryUsers;
  }

  @Override
  protected AuthenticationHandlerExecutionResult authenticateUsernamePasswordInternal(
      UsernamePasswordCredential credential, String originalPassword)
      throws GeneralSecurityException, PreventedException {

    Optional<InMemoryUser> foundUser = inMemoryUsers.stream().filter(
        u -> credential.getUsername().equalsIgnoreCase(u.getUsername()) && u.getPassword()
            .equalsIgnoreCase(originalPassword)
    ).findFirst();

    InMemoryUser matchedInMemoryUser = foundUser.orElse(null);

    if (null == matchedInMemoryUser) {
      throw new FailedLoginException("Given login info not found on the given User list.");
    }
    Map<String, List<Object>> attributes =
        Maps.<String, List<Object>>newHashMap(
            Map.of("email", List.of(matchedInMemoryUser.getEmail()),
                "username", List.of(matchedInMemoryUser.getUsername()),
                "password", List.of(matchedInMemoryUser.getPassword())
            )
        );

    return createHandlerResult(credential,
        this.principalFactory.createPrincipal(matchedInMemoryUser.getUsername(), attributes),
        new ArrayList<>(0));
  }

}

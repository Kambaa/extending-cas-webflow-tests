package org.apereo.cas.config;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import org.apereo.cas.configuration.model.support.pm.PasswordManagementProperties;
import org.apereo.cas.pm.InvalidPasswordException;
import org.apereo.cas.pm.PasswordChangeRequest;
import org.apereo.cas.pm.PasswordHistoryService;
import org.apereo.cas.pm.PasswordManagementQuery;
import org.apereo.cas.pm.impl.BasePasswordManagementService;

import org.apereo.cas.authentication.Credential;
import org.apereo.cas.util.crypto.CipherExecutor;

public class InMemoryPasswordManagementService extends BasePasswordManagementService {

  private final List<InMemoryUser> inMemoryUserList;

  public InMemoryPasswordManagementService(
      PasswordManagementProperties properties,
      CipherExecutor<Serializable, String> cipherExecutor,
      String issuer,
      PasswordHistoryService passwordHistoryService,
      List<InMemoryUser> inMemoryUserList) {
    super(properties, cipherExecutor, issuer, passwordHistoryService);
    this.inMemoryUserList = inMemoryUserList;
  }

  @Override
  public String findEmail(PasswordManagementQuery query) {
    Optional<InMemoryUser> foundUser = this.inMemoryUserList.stream()
        .filter(u -> u.getUsername().equalsIgnoreCase(query.getUsername()))
        .findFirst();
    return foundUser.map(InMemoryUser::getEmail).orElse(null);
  }

  @Override
  public String findUsername(final PasswordManagementQuery query) {
    Optional<InMemoryUser> foundUser = this.inMemoryUserList.stream()
        .filter(u -> u.getEmail().equalsIgnoreCase(query.getEmail()))
        .findFirst();
    return foundUser.map(InMemoryUser::getUsername).orElse(null);
  }

  @Override
  public boolean changeInternal(Credential credential, PasswordChangeRequest bean)
      throws InvalidPasswordException {
    for (InMemoryUser inMemoryUser : inMemoryUserList) {
      if (inMemoryUser.getUsername().equalsIgnoreCase(credential.getId())) {
        inMemoryUser.setPassword(bean.getPassword());
      }
    }
    return true;
  }
}

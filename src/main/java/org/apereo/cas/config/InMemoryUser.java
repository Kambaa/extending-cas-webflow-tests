package org.apereo.cas.config;

import java.io.Serializable;

public class InMemoryUser implements Serializable {
  private static final long serialVersionUID = 1L;
  private final String username;
  private String password;
  private final String email;

  public InMemoryUser(String username, String password, String email) {
    this.username = username;
    this.password = password;
    this.email = email;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getEmail() {
    return email;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}

package com.infoworld.jwt;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Service
final class UserServiceImpl implements UserService {

  Map<String, User> users = new HashMap<>()
  {{
       put("Matt", new User("0","matt","idg"));
  }};

  @Override
  public User save(final User user) {
    return users.put(user.getId(), user);
  }

  @Override
  public Optional<User> find(final String id) {
    return ofNullable(users.get(id));
  }

  @Override
  public Optional<User> findByUsername(final String username) {
    return users
      .values()
      .stream()
      .filter(u -> Objects.equals(username, u.getUsername()))
      .findFirst();
  }
}


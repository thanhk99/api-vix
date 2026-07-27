package vix.local.api.modules.identity.domain.repository;

import vix.local.api.modules.identity.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    User save(User user);
}

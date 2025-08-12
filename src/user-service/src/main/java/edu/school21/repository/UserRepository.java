package edu.school21.repository;

import edu.school21.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM users u WHERE u.username = :username")
    Optional<User> findByUsername(String username);

    @Query("SELECT EXISTS (SELECT 1 FROM users u WHERE u.username = :username)")
    boolean existsUserByUsername(String username);
}

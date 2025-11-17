package edu.school21.repository;

import edu.school21.entity.UserLogMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserLogMessageRepository extends JpaRepository<UserLogMessage, UUID> {
}

package edu.school21.service;

import edu.school21.entity.CollectionImage;
import edu.school21.entity.User;
import edu.school21.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id: %s not found".formatted(id)));
    }

    public CollectionImage findImageInCollectionByImageId(Long userId, Long imagId) {
        return findById(userId)
                .getCollection()
                .getImages()
                .stream()
                .filter(c -> Objects.equals(c.getImageId(), imagId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        "Изображение с ID " + imagId + " не найдено в коллекции пользователя " + userId));
    }

    public Long findIdByUsername(String username) {
        User user = findByUsername(username);
        return user.getId();
    }

    private User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User with username: %s not found".formatted(username)));
    }

    public User save(User user) {
        checkExistUserByUsername(user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public boolean isImageInCollection(User user, Long imageId) {
        return user.getCollection()
                .getImages().stream()
                .anyMatch(i -> Objects.equals(i.getImageId(), imageId));
    }

    private void checkExistUserByUsername(String username) {
        if (userRepository.existsUserByUsername(username)) {
            throw new EntityExistsException("User with username: %s already exists".formatted(username));
        }
    }
}

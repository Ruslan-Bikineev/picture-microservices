package edu.school21.service;

import edu.school21.constants.CacheNames;
import edu.school21.dto.response.UserCollectionImageRsDto;
import edu.school21.dto.response.UserRsDto;
import edu.school21.entity.User;
import edu.school21.repository.UserRepository;
import edu.school21.utils.MapperUtil;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {

    private final MapperUtil mapperUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Cacheable(cacheNames = CacheNames.USERS, key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public List<UserRsDto> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).stream()
                .map(mapperUtil::toUserRsDto)
                .collect(Collectors.toList());
    }

    @Cacheable(cacheNames = CacheNames.USER_COLLECTION, key = "#userId")
    public UserCollectionImageRsDto findUserCollection(long userId) {
        return userRepository.findById(userId)
                .map(User::getCollection)
                .map(mapperUtil::toUserCollectionImageRsDto)
                .orElseThrow(() -> new EntityNotFoundException("Коллекция пользователя с ID " + userId + " не была найдена"));
    }

    @Cacheable(cacheNames = CacheNames.USER_BY_USERNAME, key = "#username")
    public UserRsDto findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(mapperUtil::toUserRsDto)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с username %s не был найден".formatted(username)));
    }

    @Caching(
            evict = {
                    @CacheEvict(cacheNames = CacheNames.USERS, allEntries = true),
            },
            put = {
                    @CachePut(cacheNames = CacheNames.USER_BY_USERNAME, key = "#result.username"),
            })
    public UserRsDto save(User user) {
        checkExistUserByUsername(user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userRepository.save(user);
        return mapperUtil.toUserRsDto(user);
    }

    private void checkExistUserByUsername(String username) {
        if (userRepository.existsUserByUsername(username)) {
            throw new EntityExistsException("Пользователь с username %s уже существует".formatted(username));
        }
    }
}


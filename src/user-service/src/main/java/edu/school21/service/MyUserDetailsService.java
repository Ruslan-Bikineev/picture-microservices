package edu.school21.service;

import edu.school21.constants.CacheNames;
import edu.school21.entity.User;
import edu.school21.entity.security.CachedUser;
import edu.school21.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Cacheable(value = CacheNames.USER_DETAILS_BY_USERNAME, key = "#username")
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username not found: " + username));
        return mapToCachedUser(user);
    }

    @Cacheable(value = CacheNames.USER_DETAILS_BY_USER_ID, key = "#id")
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long id) throws UsernameNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User with id not found: " + id));
        return mapToCachedUser(user);
    }

    private CachedUser mapToCachedUser(User user) {
        return CachedUser.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
    }
}

package com.hayet.fertility.service;

import com.hayet.fertility.domain.Authority;
import com.hayet.fertility.domain.User;
import com.hayet.fertility.repository.AuthorityRepository;
import com.hayet.fertility.repository.UserRepository;
import com.hayet.fertility.service.dto.AdminUserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class AdminService {

    private static final Logger LOG = LoggerFactory.getLogger(AdminService.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthorityRepository authorityRepository;

    private final CacheManager cacheManager;

    public AdminService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        AuthorityRepository authorityRepository,
        CacheManager cacheManager
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
        this.cacheManager = cacheManager;
    }

    /**
     * Update all information for a specific admin, and return the modified admin.
     *
     * @param originalAdmin user to update.
     * @return updated admin.
     */
    public Optional<AdminUserDTO> updateAdmin(AdminUserDTO originalAdmin) {
        return Optional.of(userRepository.findById(originalAdmin.getId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(newAdmin -> {
                this.clearUserCaches(newAdmin);
                newAdmin.setLogin(originalAdmin.getLogin().toLowerCase());
                newAdmin.setFirstName(originalAdmin.getFirstName());
                newAdmin.setLastName(originalAdmin.getLastName());
                if (originalAdmin.getEmail() != null) {
                    newAdmin.setEmail(originalAdmin.getEmail().toLowerCase());
                }
                newAdmin.setImageUrl(originalAdmin.getImageUrl());
                newAdmin.setActivated(originalAdmin.isActivated());
                newAdmin.setNotificationPreference(originalAdmin.getNotificationPreference());
                newAdmin.setLangKey(originalAdmin.getLangKey());
                Set<Authority> managedAuthorities = newAdmin.getAuthorities();
                managedAuthorities.clear();
                originalAdmin
                    .getAuthorities()
                    .stream()
                    .map(authorityRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(managedAuthorities::add);
                userRepository.save(newAdmin);
                this.clearUserCaches(newAdmin);
                LOG.debug("Changed Information for Admin: {}", newAdmin);
                return newAdmin;
            })
            .map(AdminUserDTO::new);
    }

    @Transactional(readOnly = true)
    public Optional<User> findOneWithAuthoritiesByEmailIgnoreCase(String login) {
        return userRepository.findOneWithAuthoritiesByEmailIgnoreCase(login);
    }

    private void clearUserCaches(User user) {
        Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE)).evict(user.getLogin());
        if (user.getEmail() != null) {
            Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE)).evict(user.getEmail());
        }
    }
}

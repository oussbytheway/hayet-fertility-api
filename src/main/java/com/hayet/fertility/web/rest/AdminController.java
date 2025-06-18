package com.hayet.fertility.web.rest;

import com.hayet.fertility.domain.User;
import com.hayet.fertility.repository.UserRepository;
import com.hayet.fertility.security.AuthoritiesConstants;
import com.hayet.fertility.service.MailService;
import com.hayet.fertility.service.AdminService;
import com.hayet.fertility.service.dto.AdminUserDTO;
import com.hayet.fertility.web.rest.errors.EmailAlreadyUsedException;
import com.hayet.fertility.web.rest.errors.LoginAlreadyUsedException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

import java.util.Optional;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class AdminController {

    private static final Logger LOG = LoggerFactory.getLogger(AdminController.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserRepository userRepository;

    private final AdminService adminService;

    private final MailService mailService;

    public AdminController(UserRepository userRepository, AdminService adminService, MailService mailService) {
        this.userRepository = userRepository;
        this.adminService = adminService;
        this.mailService = mailService;
    }

    /**
     * {@code GET /admin/:email} : get the "email" admin.
     *
     * @param email the email of the admin to find.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the "email" admin, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/admin/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<AdminUserDTO> getOne(@PathVariable("id") String email) {
        LOG.debug("REST request to get Admin with email : {}", email);
        return ResponseUtil.wrapOrNotFound(adminService.findOneWithAuthoritiesByEmailIgnoreCase(email).map(AdminUserDTO::new));
    }

    /**
     * {@code PUT /admin/users} : Updates an existing admin.
     *
     * @param admin the admin to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated user.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already in use.
     * @throws LoginAlreadyUsedException {@code 400 (Bad Request)} if the login is already in use.
     */
    @PutMapping("/admins")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<AdminUserDTO> update(
        @Valid @RequestBody AdminUserDTO admin
    ) {
        LOG.debug("REST request to update User : {}", admin);
        Optional<User> original = userRepository.findOneByEmailIgnoreCase(admin.getEmail());
        if (original.isPresent() && (!original.orElseThrow().getId().equals(admin.getId()))) {
            throw new EmailAlreadyUsedException();
        }
        original = userRepository.findOneByLogin(admin.getLogin().toLowerCase());
        if (original.isPresent() && (!original.orElseThrow().getId().equals(admin.getId()))) {
            throw new LoginAlreadyUsedException();
        }
        Optional<AdminUserDTO> updatedUser = adminService.updateAdmin(admin);

        return ResponseUtil.wrapOrNotFound(
            updatedUser,
            HeaderUtil.createAlert(applicationName, "An admin is updated with identifier " + admin.getEmail(), admin.getEmail())
        );
    }
}

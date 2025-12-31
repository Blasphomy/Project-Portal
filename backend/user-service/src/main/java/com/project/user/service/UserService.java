package com.project.user.service;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;

public interface UserService {
    ReactiveUserDetailsService reactiveUserDetailsService();
}

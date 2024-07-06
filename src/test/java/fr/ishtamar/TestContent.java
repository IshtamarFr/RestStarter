package fr.ishtamar;

import fr.ishtamar.starter.user.UserInfo;

import static fr.ishtamar.starter.security.SecurityConfig.passwordEncoder;

public class TestContent {
    public UserInfo initialUser=UserInfo.builder()
            .name("Ishta")
            .email("test@test.com")
            .password(passwordEncoder().encode("123456"))
            .roles("ROLE_USER")
            .build();

    public UserInfo initialUser2=UserInfo.builder()
            .name("Pal")
            .email("test17@test.com")
            .password(passwordEncoder().encode("654321"))
            .roles("ROLE_USER")
            .build();
}

package fr.ishtamar.starter.integration.service;

import fr.ishtamar.starter.auth.ModifyUserRequest;
import fr.ishtamar.starter.user.UserInfo;
import fr.ishtamar.starter.user.UserInfoRepository;
import fr.ishtamar.starter.user.UserInfoServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static fr.ishtamar.starter.security.SecurityConfig.passwordEncoder;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserInfoServiceImplIT {
    @Autowired
    UserInfoServiceImpl userInfoServiceImpl;
    @Autowired
    UserInfoRepository repository;

    final static UserInfo initialUser=UserInfo.builder()
        .name("Ishta")
        .email("test@test.com")
        .password(passwordEncoder().encode("123456"))
        .roles("ROLE_USER")
        .build();

    final static UserInfo initialUser2=UserInfo.builder()
        .name("Tamshi")
        .email("test10@test.com")
        .password(passwordEncoder().encode("Aa123456!"))
        .roles("ROLE_USER,ROLE_ADMIN")
        .build();

    @BeforeEach
    void init() {
        this.clean();
        repository.save(initialUser);
    }

    @AfterEach
    void clean() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("When I load user by username, I get correct email and name")
    void testLoadUserByUserName() {
        //Given

        //When
        UserInfo user = userInfoServiceImpl.getUserByUsername("test@test.com");
        //Then
        assertThat(user.getName()).isEqualTo("Ishta");
    }

    @Test
    @DisplayName("When I modify all my infos with correct data, it is done")
    void testPutInfosWithAllData() {
        //Given
        repository.save(initialUser);
        repository.save(initialUser2);

        ModifyUserRequest modifyUserRequest= ModifyUserRequest.builder()
                .email("test9999@test.com")
                .name("Ishta2")
                .oldPassword("123456")
                .password("Aa123456!")
                .build();

        //When
        UserInfo userInfo=userInfoServiceImpl.modifyUser("test@test.com",modifyUserRequest);

        //Then
        assertThat(userInfo.getEmail()).isEqualTo("test9999@test.com");
        assertThat(userInfo.getName()).isEqualTo("Ishta2");
    }

    @Test
    @DisplayName("When I modify some of my infos with correct data, it is done")
    void testPutInfosWithSomeData() {
        //Given
        repository.save(initialUser);
        repository.save(initialUser2);

        ModifyUserRequest modifyUserRequest= ModifyUserRequest.builder()
                .name("Ishta2")
                .oldPassword("123456")
                .password("Aa123456!")
                .build();

        //When
        UserInfo userInfo=userInfoServiceImpl.modifyUser("test@test.com",modifyUserRequest);

        //Then
        assertThat(userInfo.getEmail()).isEqualTo("test@test.com");
        assertThat(userInfo.getName()).isEqualTo("Ishta2");
    }

    @Test
    @DisplayName("When I modify infos with incorrect email, it is not done")
    void testPutInfosWithIncorrectEmail() {
        //Given
        repository.save(initialUser);
        repository.save(initialUser2);

        ModifyUserRequest modifyUserRequest= ModifyUserRequest.builder()
                .email("test10@test.com")
                .name("Ishta17")
                .password("Aa123456!")
                .build();

        //When
        try {
            userInfoServiceImpl.modifyUser("test@test.com", modifyUserRequest);
        } catch(Exception e){
            //Nothing to do here
        }

        //Then
        UserInfo userInfo=repository.findByEmail("test@test.com").get();
        assertThat(userInfo.getName()).isEqualTo("Ishta");
    }

    @Test
    @DisplayName("When I modify infos with incorrect name, it is not done")
    void testPutInfosWithIncorrectName() {
        //Given
        repository.save(initialUser);
        repository.save(initialUser2);

        ModifyUserRequest modifyUserRequest= ModifyUserRequest.builder()
                .email("test673@test.com")
                .name("Tamshi")
                .password("Aa123456!")
                .build();

        //When
        try {
            userInfoServiceImpl.modifyUser("test@test.com", modifyUserRequest);
        } catch(Exception e){
            //Nothing to do here
        }

        //Then
        UserInfo userInfo=repository.findByEmail("test@test.com").get();
        assertThat(userInfo.getName()).isEqualTo("Ishta");
        assertThat(repository.findByEmail("test673@test.com")).isNotPresent();
    }
}

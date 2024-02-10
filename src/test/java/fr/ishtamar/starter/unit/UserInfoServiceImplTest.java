package fr.ishtamar.starter.unit;

import fr.ishtamar.starter.user.UserInfo;
import fr.ishtamar.starter.exceptionhandler.BadCredentialsException;
import fr.ishtamar.starter.exceptionhandler.EntityNotFoundException;
import fr.ishtamar.starter.user.UserInfoRepository;
import fr.ishtamar.starter.user.UserInfoServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserInfoServiceImplTest {
    @MockBean
    UserInfoRepository userInfoRepository;
    @Autowired
    UserInfoServiceImpl userInfoServiceImpl;

    final static UserInfo mockUser=UserInfo.builder()
            .name("mockTest")
            .password("123456")
            .email("mock@testmock.com")
            .build();

    @Test
    @DisplayName("When I try to save a user, service is called")
    void testAddUser() {
        //Given

        //When
        userInfoServiceImpl.createUser(mockUser);

        //Then
        verify(userInfoRepository,times(1)).save(mockUser);
    }

    @Test
    @DisplayName("When I try to add a user with same name or Email, an error is thrown")
    void testAddAlreadyExistingUser() {
        //Given
        when(userInfoRepository.findByEmail("mock@testmock.com")).thenReturn(Optional.of(mockUser));

        //When
        assertThatThrownBy(()->userInfoServiceImpl.createUser(mockUser)).isInstanceOf(BadCredentialsException.class);

        //Then
        verify(userInfoRepository,times(0)).save(mockUser);
    }

    @Test
    @DisplayName("When I try to get user by invalid id, an error is thrown")
    void testGetUserByInvalidId() {
        //Given

        //When
        assertThatThrownBy(()->userInfoServiceImpl.getUserById(42L)).isInstanceOf(EntityNotFoundException.class);

        //Then
        verify(userInfoRepository,times(1)).findById(42L);
    }
}

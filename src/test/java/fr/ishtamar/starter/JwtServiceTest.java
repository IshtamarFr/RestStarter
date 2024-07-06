package fr.ishtamar.starter;

import fr.ishtamar.starter.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
class JwtServiceTest {
    @MockBean
    UserDetails userDetails;
    @Autowired
    JwtService jwtService;

    @Test
    void testGenerateTokenIsSetUp() {
        //Given

        //When
        String jwt=jwtService.generateToken("test@test.com");

        //Then
        assertThat(jwt).isNotEmpty();
    }

    @Test
    void testGenerateTokenIsValid() {
        //Given
        when(userDetails.getUsername()).thenReturn("test@test.com");

        //When
        String jwt = jwtService.generateToken("test@test.com");

        //Then
        assertThat(jwtService.validateToken(jwt,userDetails)).isTrue();
    }

    @Test
    void testGenerateTokenIsNotValid() {
        //Given
        when(userDetails.getUsername()).thenReturn("test123456@test.com");

        //When
        String jwt = jwtService.generateToken("test@test.com");

        //Then
        assertThat(jwtService.validateToken(jwt,userDetails)).isFalse();
    }
}

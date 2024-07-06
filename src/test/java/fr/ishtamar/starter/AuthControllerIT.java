package fr.ishtamar.starter;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ishtamar.TestContent;
import fr.ishtamar.starter.user.UserInfo;
import fr.ishtamar.starter.exceptionhandler.EntityNotFoundException;
import fr.ishtamar.starter.auth.AuthRequest;
import fr.ishtamar.starter.auth.CreateUserRequest;
import fr.ishtamar.starter.auth.ModifyUserRequest;
import fr.ishtamar.starter.user.UserInfoRepository;
import fr.ishtamar.starter.security.JwtService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static fr.ishtamar.starter.security.SecurityConfig.passwordEncoder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIT {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserInfoRepository repository;
    @Autowired
    JwtService jwtService;

    ObjectMapper mapper=new ObjectMapper();

    @BeforeEach
    @AfterEach
    void clean() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("When I get to welcome endpoint, I get an answer")
    public void testWelcomeIsOk() throws Exception {
        //Given

        //When
        this.mockMvc.perform(get("/auth/welcome"))

                //Then
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Welcome")));
    }

    @Test
    @DisplayName("When I try to add an user with existing email, I get a bad request")
    void testRegisterEmailIsAlreadyUsed() throws Exception {
        //Given
        TestContent tc=new TestContent();
        repository.save(tc.initialUser);
        CreateUserRequest mockUser=CreateUserRequest.builder()
                .name("Ishta")
                .email("test@test.com")
                .password("Aa123456!")
                .build();

        //When
        this.mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(mockUser)))
                //Then
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("When I try to add an user, all is fine and user is found")
    void testRegisterUser() throws Exception {
        //Given
        TestContent tc=new TestContent();
        repository.save(tc.initialUser);
        CreateUserRequest mockRequest=CreateUserRequest.builder()
                .name("HardToDestroyReptile")
                .email("test682@test.com")
                .password("Aa123456!")
                .build();

        //When
        this.mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(mockRequest)))

                //Then
                .andExpect(status().isOk());
        assertThat(repository.findByEmail("test682@test.com")).isPresent();
    }

    @Test
    @DisplayName("When I try to login as a valid user, I get a valid answer and a token")
    void testLogin() throws Exception {
        //Given
        TestContent tc=new TestContent();
        repository.save(tc.initialUser);
        AuthRequest mockRequest=AuthRequest.builder()
                .email("test@test.com")
                .password("123456")
                .build();

        //When
        this.mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(mockRequest)))
                //Then
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("token")));
    }

    @Test
    @DisplayName("When I try to login as an invalid user, I get a forbidden")
    void testLoginAsInvalidUser() throws Exception {
        //Given
        TestContent tc=new TestContent();
        repository.save(tc.initialUser);
        AuthRequest mockRequest=AuthRequest.builder()
                .email("test@test.com")
                .password("1234567")
                .build();

        //When
        this.mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(mockRequest)))

                //Then
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("When I try to login as an inexistant user, I get a bad request")
    void testLoginAsInexistantUser() throws Exception {
        //Given
        TestContent tc=new TestContent();
        repository.save(tc.initialUser);
        AuthRequest mockRequest=AuthRequest.builder()
                .email("test258@test.com")
                .password("123456")
                .build();

        //When
        this.mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(mockRequest)))

                //Then
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("When I try to get my data as valid authentified user, all is OK and password is obfuscated")
    void testMeIsOK() throws Exception {
        //Given
        TestContent tc=new TestContent();
        repository.save(tc.initialUser);
        String jwt=jwtService.generateToken(tc.initialUser.getEmail());

        //When
        this.mockMvc.perform(MockMvcRequestBuilders.get("/auth/me")
                        .header("Authorization","Bearer "+jwt))

        //Then
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Ishta")))
                .andExpect(content().string(CoreMatchers.not(containsString("word"))));

    }

    @Test
    @DisplayName("When I try to get my data as unauthentified, it is forbidden")
    void testMeAsUnauthentifiedIsForbidden() throws Exception {
        //Given
        TestContent tc=new TestContent();
        repository.save(tc.initialUser);

        //When
        this.mockMvc.perform(MockMvcRequestBuilders.get("/auth/me"))

        //Then
                .andExpect(status().isForbidden());

    }

    @Test
    @DisplayName("When I try to get my data as invalid user, it is forbidden")
    void testMeAsInvalidUserIsForbidden() throws EntityNotFoundException {
        //Given
        TestContent tc=new TestContent();
        repository.save(tc.initialUser);
        String jwt=jwtService.generateToken("fake@hacker.com");

        //When-Then
        assertThatThrownBy(()->this.mockMvc.perform(MockMvcRequestBuilders.get("/auth/me")
                .header("Authorization","Bearer "+jwt))).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("When I try to update my data with valid infos, it is OK and a token is generated")
    void testPutMeIsOKWithAllData() throws Exception {
        //Given
        TestContent tc=new TestContent();
        repository.save(tc.initialUser);
        String jwt=jwtService.generateToken("test@test.com");
        ModifyUserRequest mockRequest=ModifyUserRequest.builder()
                .name("Ishta")
                .email("test17@test.com")
                .oldPassword("123456")
                .password("J'aime le cassoulet 17!")
                .build();

        //When
        this.mockMvc.perform(MockMvcRequestBuilders.put("/auth/me")
                .header("Authorization","Bearer "+jwt)
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(mockRequest)))

        //Then
                .andExpect(status().isOk());

        UserInfo candidate=repository.findByEmail("test17@test.com").get();
        assertThat(candidate.getName()).isEqualTo("Ishta");
        assertThat(candidate.getPassword().length()).isGreaterThan(40);
    }

    @Test
    @DisplayName("When I try to update my data with valid but already taken infos, it returns BadCredentialsException")
    void testPutMeEmailIsAlreadyTakenAndMissingData() throws Exception {
        //Given
        TestContent tc=new TestContent();
        repository.save(tc.initialUser);
        repository.save(tc.initialUser2);
        String jwt=jwtService.generateToken("test@test.com");
        ModifyUserRequest mockRequest=ModifyUserRequest.builder()
                .email("test17@test.com")
                .oldPassword("123456")
                .password("J'aime le cassoulet 17!")
                .build();

        //When
        this.mockMvc.perform(MockMvcRequestBuilders.put("/auth/me")
                        .header("Authorization","Bearer "+jwt)
                        .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(mockRequest)))

                //Then
                .andExpect(status().isBadRequest());

        Optional<UserInfo> candidate=repository.findByEmail("test@test.com");
        assertThat(candidate).isPresent();
        UserInfo user=candidate.get();
        assertThat(user.getName()).isEqualTo("Ishta");
    }

    @Test
    @DisplayName("When I try to update my data with invalid oldPassword, it is BadCredentialsException")
    void testPutMeWithIncorrectOldPassword() throws Exception {
        //Given
        TestContent tc=new TestContent();
        repository.save(tc.initialUser);
        String jwt=jwtService.generateToken("test@test.com");
        ModifyUserRequest mockRequest=ModifyUserRequest.builder()
                .name("Ishta")
                .email("test17@test.com")
                .oldPassword("1234567")
                .password("J'aime le cassoulet 17!")
                .build();

        //When
        this.mockMvc.perform(MockMvcRequestBuilders.put("/auth/me")
                        .header("Authorization","Bearer "+jwt)
                        .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(mockRequest)))

                //Then
                .andExpect(status().isBadRequest());

        Optional<UserInfo> candidate=repository.findByEmail("test@test.com");
        assertThat(candidate).isPresent();
        UserInfo user=candidate.get();
        assertThat(user.getName()).isEqualTo("Ishta");
    }
}

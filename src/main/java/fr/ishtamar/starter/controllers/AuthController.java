package fr.ishtamar.starter.controllers;

import fr.ishtamar.starter.auth.AuthRequest;
import fr.ishtamar.starter.auth.CreateUserRequest;
import fr.ishtamar.starter.auth.ModifyUserRequest;
import fr.ishtamar.starter.exceptionhandler.GenericException;
import fr.ishtamar.starter.user.UserDto;
import fr.ishtamar.starter.user.UserInfo;
import fr.ishtamar.starter.exceptionhandler.BadCredentialsException;
import fr.ishtamar.starter.exceptionhandler.EntityNotFoundException;
import fr.ishtamar.starter.user.UserMapper;
import fr.ishtamar.starter.security.JwtService;
import fr.ishtamar.starter.user.UserInfoService;
import fr.ishtamar.starter.util.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserInfoService service;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final EmailService emailService;

    public AuthController(UserInfoService service, JwtService jwtService, AuthenticationManager authenticationManager, UserMapper userMapper, EmailService emailService) {
        this.service=service;
        this.jwtService=jwtService;
        this.userMapper=userMapper;
        this.emailService=emailService;
    }

    @Value("${fr.ishtamar.starter.register-confirmation}")
    private boolean requireConfirmation;

    @Operation(hidden=true)
    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome this endpoint is not secure";
    }

    @PostMapping("/sendmemail/{address}")
    @PreAuthorize("hasRole('USER')")
    public void testSendMail(@PathVariable final String address) {
        emailService.sendSimpleMessage(address,"Test message from RestStarter","Sending email from RestStarter WebApp works !");
    }

    @Operation(summary = "register new user",responses={
            @ApiResponse(responseCode="200", description = "User successfully created"),
            @ApiResponse(responseCode="400", description = "User already exists")
    })
    @PostMapping("/register")
    public Map<String,String> addNewUser(@Valid @RequestBody CreateUserRequest request) throws ConstraintViolationException {
        String token;
        Map<String,String>map=new HashMap<>();

        UserInfo userInfo=UserInfo.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .roles("ROLE_USER")
                .build();

        if (requireConfirmation){
            token= RandomStringUtils.randomAlphanumeric(15);
            userInfo.setToken(token);
            Long id=service.createUser(userInfo).getId();
            map.put("info","New user successfully created. Waiting for account to be validated");
            emailService.sendValidationLink(request.getEmail(),id,token);
        } else {
            service.createUser(userInfo);
            map.put("token",jwtService.generateToken(userInfo.getEmail()));
        }
        return map;
    }

    @Operation(summary = "logins user and returns JWT",responses={
            @ApiResponse(responseCode="200", description = "Token successfully created"),
            @ApiResponse(responseCode="404", description = "Username not found")
    })
    @PostMapping("/login")
    public Map<String,String> authenticateAndGetToken(@RequestBody @Valid AuthRequest authRequest) throws UsernameNotFoundException {
        UserInfo user=service.getUserByUsername(authRequest.getEmail());
        boolean isAuthenticated= BCrypt.checkpw(authRequest.getPassword(),user.getPassword());

        if (requireConfirmation){
            if (user.getToken()==null || user.getToken().length()<15) {
                //User account is validated. We now connect him if password is OK or if the forgotten password token is correct
                if (isAuthenticated || (user.getToken()!=null && authRequest.getPassword().equals(user.getToken()))) {
                    Map<String, String> map = new HashMap<>();
                    map.put("token", jwtService.generateToken(authRequest.getEmail()));
                    user.setToken(null);
                    service.resetUserToken(user);
                    return map;
                } else {
                    throw new BadCredentialsException();
                }
            } else {
                //User account is not validated. Request is denied
                throw new GenericException("Your account has not been validated yet. Request a new link if needed");
            }
        } else {
            if (isAuthenticated) {
                Map<String, String> map = new HashMap<>();
                map.put("token", jwtService.generateToken(authRequest.getEmail()));
                return map;
            } else {
                throw new UsernameNotFoundException("invalid user request !");
            }
        }
    }

    @Operation(summary = "gets personal data from logged in user",responses={
            @ApiResponse(responseCode="200", description = "Personal data is displayed"),
            @ApiResponse(responseCode="403", description = "Access unauthorized")
    })
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public UserDto userProfile(@RequestHeader(value="Authorization",required=false) String jwt) {
        return userMapper.toDto(service.getUserByUsername(jwtService.extractUsername(jwt.substring(7))));
    }

    @Operation(summary = "changes personal data from logged in user",responses={
            @ApiResponse(responseCode="200", description = "Personal data is changed, new JWT is displayed"),
            @ApiResponse(responseCode="400", description = "Email is already used or password is not valid"),
            @ApiResponse(responseCode="403", description = "Access unauthorized")
    })
    @PutMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public Map<String,String> userModifyProfile(
            @RequestHeader(value="Authorization",required=false) String jwt,
            @Valid @RequestBody ModifyUserRequest request
    ) throws EntityNotFoundException, BadCredentialsException, ConstraintViolationException {
        UserInfo candidate=service.modifyUser(jwtService.extractUsername(jwt.substring(7)),request);

        //prepare a new JWT to show (not executed if there's an error before)
        Map<String,String>map=new HashMap<>();
        map.put("token",jwtService.generateToken(candidate.getEmail()));

        return map;
    }

    @Operation(summary = "new user tries to validate their account",responses={
            @ApiResponse(responseCode="200", description = "Account is validated"),
            @ApiResponse(responseCode="400", description = "Id-Token is invalid, or account already validated"),
            @ApiResponse(responseCode="404", description = "User is not found")
    })
    @PutMapping("/validate")
    public Map<String,String> validateNewUser(
            @RequestParam final Long id,
            @RequestParam final String token
    ) throws EntityNotFoundException, BadCredentialsException,GenericException {
        UserInfo user=service.getUserById(id);
        if (user.getToken()!=null) {
            if (user.getToken().equals(token)) {
                service.validateUser(user);

                Map<String, String> map = new HashMap<>();
                map.put("token", jwtService.generateToken(user.getEmail()));
                return map;
            } else {
                throw new BadCredentialsException();
            }
        } else {
            throw new GenericException("This account has already been activated");
        }
    }

    @Operation(summary = "user requires a temporary password token",responses={
            @ApiResponse(responseCode="200", description = "token has been sent to email"),
            @ApiResponse(responseCode="404", description = "User is not found")
    })
    @PostMapping("/forgotten")
    public String forgottenPassword(
            @RequestParam final String email,
            @RequestParam final String language
    ) throws EntityNotFoundException, GenericException {
        UserInfo user=service.getUserByUsername(email);

        if (user.getToken()==null || user.getToken().length()<15){
            //User account is validated, we can serve a new temporary password
            String token= RandomStringUtils.randomAlphanumeric(10);
            user.setToken(token);
            service.modifyUser(user);
            emailService.sendTemporaryPassword(user.getEmail(),token);
            return "A temporary password has been sent if this address exists";
        }else {
            //User account is not validated, we can serve a new validation link
            String token= RandomStringUtils.randomAlphanumeric(15);
            user.setToken(token);
            service.modifyUser(user);
            emailService.sendValidationLink(user.getEmail(),user.getId(),token);
            return "A new link for validation has been sent";
        }
    }
}

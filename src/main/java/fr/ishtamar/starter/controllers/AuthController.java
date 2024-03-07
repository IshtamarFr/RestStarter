package fr.ishtamar.starter.controllers;

import fr.ishtamar.starter.auth.AuthRequest;
import fr.ishtamar.starter.auth.CreateUserRequest;
import fr.ishtamar.starter.auth.ModifyUserRequest;
import fr.ishtamar.starter.user.UserDto;
import fr.ishtamar.starter.user.UserInfo;
import fr.ishtamar.starter.exceptionhandler.BadCredentialsException;
import fr.ishtamar.starter.exceptionhandler.EntityNotFoundException;
import fr.ishtamar.starter.user.UserMapper;
import fr.ishtamar.starter.security.JwtService;
import fr.ishtamar.starter.user.UserInfoService;
import fr.ishtamar.starter.user.UserInfoServiceImpl;
import fr.ishtamar.starter.util.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserInfoService service;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final EmailService emailService;

    static final String TOKEN="token";

    public AuthController(UserInfoServiceImpl service, JwtService jwtService, AuthenticationManager authenticationManager, UserMapper userMapper, EmailService emailService) {
        this.service=service;
        this.jwtService=jwtService;
        this.authenticationManager=authenticationManager;
        this.userMapper=userMapper;
        this.emailService=emailService;
    }

    @Operation(hidden=true)
    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome this endpoint is not secure";
    }

    @PostMapping("/sendmemail/{address}")
    @Secured("ROLE_ADMIN")
    public void testSendMail(@PathVariable final String address) {
        emailService.sendSimpleMessage(address,"Test message from RestStarter","Sending email from RestStarter WebApp works !");
    }

    @Operation(summary = "register new user",responses={
            @ApiResponse(responseCode="200", description = "User successfully created"),
            @ApiResponse(responseCode="400", description = "User already exists")
    })
    @PostMapping("/register")
    public Map<String,String> addNewUser(@Valid @RequestBody CreateUserRequest request) throws ConstraintViolationException {
        UserInfo userInfo=UserInfo.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .roles("ROLE_USER")
                .build();

        service.createUser(userInfo);
        Map<String,String>map=new HashMap<>();
        map.put(TOKEN,jwtService.generateToken(userInfo.getEmail()));
        return map;
    }

    @Operation(summary = "logins user and returns JWT",responses={
            @ApiResponse(responseCode="200", description = "Token successfully created"),
            @ApiResponse(responseCode="404", description = "Username not found")
    })
    @PostMapping("/login")
    public Map<String,String> authenticateAndGetToken(@RequestBody AuthRequest authRequest) throws UsernameNotFoundException {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            Map<String,String>map=new HashMap<>();
            map.put(TOKEN,jwtService.generateToken(authRequest.getEmail()));
            return map;
        } else {
            throw new UsernameNotFoundException("invalid user request !");
        }
    }

    @Operation(summary = "gets personal data from logged in user",responses={
            @ApiResponse(responseCode="200", description = "Personal data is displayed"),
            @ApiResponse(responseCode="403", description = "Access unauthorized")
    })
    @GetMapping("/me")
    @Secured("ROLE_USER")
    public UserDto userProfile(@RequestHeader(value="Authorization",required=false) String jwt) {
        return userMapper.toDto(service.getUserByUsername(jwtService.extractUsername(jwt.substring(7))));
    }

    @Operation(summary = "changes personal data from logged in user",responses={
            @ApiResponse(responseCode="200", description = "Personal data is changed, new JWT is displayed"),
            @ApiResponse(responseCode="400", description = "Email is already used or password is not valid"),
            @ApiResponse(responseCode="403", description = "Access unauthorized")
    })
    @PutMapping("/me")
    @Secured("ROLE_USER")
    public Map<String,String> userModifyProfile(
            @RequestHeader(value="Authorization",required=false) String jwt,
            @Valid @RequestBody ModifyUserRequest request
    ) throws EntityNotFoundException, BadCredentialsException, ConstraintViolationException {
        UserInfo candidate=service.modifyUser(jwtService.extractUsername(jwt.substring(7)),request);

        //prepare a new JWT to show (not executed if there's an error before)
        Map<String,String>map=new HashMap<>();
        map.put(TOKEN,jwtService.generateToken(candidate.getEmail()));

        return map;
    }
}

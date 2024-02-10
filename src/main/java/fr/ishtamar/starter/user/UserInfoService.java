package fr.ishtamar.starter.user;

import fr.ishtamar.starter.exceptionhandler.BadCredentialsException;
import fr.ishtamar.starter.exceptionhandler.EntityNotFoundException;
import fr.ishtamar.starter.auth.ModifyUserRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserInfoService extends UserDetailsService {
    @Override
    UserDetails loadUserByUsername(String username) throws EntityNotFoundException;

    void createUser(UserInfo userInfo) throws BadCredentialsException;

    UserInfo getUserByUsername(String username) throws EntityNotFoundException;

    UserInfo getUserById(Long id) throws EntityNotFoundException;

    UserInfo modifyUser(String username, ModifyUserRequest request) throws BadCredentialsException;
}

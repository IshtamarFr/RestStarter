package fr.ishtamar.starter.user;

import fr.ishtamar.starter.exceptionhandler.BadCredentialsException;
import fr.ishtamar.starter.exceptionhandler.EntityNotFoundException;
import fr.ishtamar.starter.auth.ModifyUserRequest;
import fr.ishtamar.starter.security.UserInfoDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserInfoServiceImpl implements UserInfoService {
    private final UserInfoRepository repository;

    public UserInfoServiceImpl(UserInfoRepository repository) {
        this.repository = repository;
    }

    private static final PasswordEncoder encoder=new BCryptPasswordEncoder();

    @Override
    public UserDetails loadUserByUsername(String username) throws EntityNotFoundException {
        Optional<UserInfo> userDetail = repository.findByEmail(username);

        // Converting userDetail to UserDetails
        return userDetail.map(UserInfoDetails::new)
                .orElseThrow(() -> new EntityNotFoundException(UserDetails.class,"username",username));
    }

    @Override
    public void createUser(UserInfo userInfo) throws BadCredentialsException {
        Optional<UserInfo> userDetail = repository.findByEmail(userInfo.getEmail());
        if (userDetail.isPresent()){
            throw new BadCredentialsException();
        } else {
            userInfo.setPassword(encoder.encode(userInfo.getPassword()));
            repository.save(userInfo);
        }
    }

    @Override
    public UserInfo getUserByUsername(String username) throws EntityNotFoundException {
        return repository.findByEmail(username)
                .orElseThrow(() -> new EntityNotFoundException(UserInfo.class,"email",username));
    }

    @Override
    public UserInfo getUserById(Long id) throws EntityNotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UserDetails.class,"id",id.toString()));
    }

    @Override
    public UserInfo modifyUser(String username, ModifyUserRequest request) throws BadCredentialsException, EntityNotFoundException {
        UserInfo userInfo=this.getUserByUsername(username);

        if(encoder.matches(request.getOldPassword(),userInfo.getPassword())) {
            if (request.getName() != null && !request.getName().isEmpty()) userInfo.setName(request.getName());
            if (request.getEmail() != null && !request.getEmail().isEmpty()) userInfo.setEmail(request.getEmail());
            if (request.getPassword() != null && !request.getPassword().isEmpty())
                userInfo.setPassword(encoder.encode(request.getPassword()));

            try {
                return repository.save(userInfo);
            } catch (Exception e) {
                throw new BadCredentialsException();
            }
        } else {
            throw new BadCredentialsException();
        }
    }
}

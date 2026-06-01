package consoli.resume.security;

import consoli.resume.entity.UserEntity;
import consoli.resume.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService
        implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(
            String email
    )

            throws UsernameNotFoundException {

        UserEntity user =

                userRepository
                        .findByEmail(
                                email
                        )
                        .orElseThrow(

                                () ->

                                        new UsernameNotFoundException(
                                                "User not found"
                                        )

                        );

        return new User(

                user.getEmail(),
                user.getPassword(),

                List.of(

                        new SimpleGrantedAuthority(

                                "ROLE_"
                                        +
                                        user.getRole()

                        )

                )

        );
    }
}
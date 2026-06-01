package consoli.resume;

import consoli.resume.entity.ResumeEntity;
import consoli.resume.entity.UserEntity;
import consoli.resume.entity.UserRole;
import consoli.resume.repository.ResumeRepository;
import consoli.resume.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ResumeRepositoryTest {

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private UserRepository userRepository;

    @AutoConfigureTestDatabase(
            replace =
                    AutoConfigureTestDatabase.Replace.ANY
    )

    @Test
    void shouldFindResumeByUser() {

        UserEntity user =
                new UserEntity();

        user.setName(
                "Matheus"
        );

        user.setEmail(
                "matheus@email.com"
        );

        user.setPassword(
                "123"
        );

        user.setRole(
                UserRole.USER
        );

        UserEntity savedUser =
                userRepository.save(
                        user
                );

        ResumeEntity resume =
                new ResumeEntity();

        resume.setUser(
                savedUser
        );

        resume.setJobTitle(
                "Java Developer"
        );

        resume.setCompany(
                "Tech Corp"
        );

        resume.setCreatedAt(
                LocalDateTime.now()
        );

        resumeRepository.save(
                resume
        );

        var page =

                resumeRepository
                        .findByUser(

                                savedUser,

                                org.springframework.data.domain
                                        .PageRequest
                                        .of(
                                                0,
                                                5
                                        )

                        );

        assertEquals(
                1,
                page.getTotalElements()
        );
    }
}
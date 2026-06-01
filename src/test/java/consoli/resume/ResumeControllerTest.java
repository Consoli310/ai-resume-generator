package consoli.resume;

import consoli.resume.ai.AIClient;
import consoli.resume.security.JwtFilter;
import consoli.resume.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        properties = {
                "jwt.secret=test-secret-key-test-secret-key-test-secret-key",
                "gemini.api-key=test",

                "spring.datasource.url=jdbc:h2:mem:testdb",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=",

                "spring.jpa.hibernate.ddl-auto=create-drop"
        }
)
@AutoConfigureMockMvc
class ResumeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AIClient aiClient;

    @MockBean
    private JwtService jwtService;

    @Test
    void shouldReturnForbiddenWhenHistoryWithoutToken()
            throws Exception {

        mockMvc.perform(

                        get(
                                "/api/resume/history"
                        )

                )
                .andExpect(
                        status().isForbidden()
                );
    }
}
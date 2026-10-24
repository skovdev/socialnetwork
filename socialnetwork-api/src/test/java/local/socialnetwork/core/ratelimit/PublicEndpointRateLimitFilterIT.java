package local.socialnetwork.core.ratelimit;

import local.socialnetwork.BaseIntegrationTest;

import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;

import org.springframework.test.context.TestPropertySource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = "socialnetwork.security.request-rate-limit.max-requests=3")
class PublicEndpointRateLimitFilterIT extends BaseIntegrationTest {

    @Test
    void resendVerification_isThrottledPerIp_afterMaxRequests() throws Exception {
        var body = "{\"email\":\"nobody@example.com\"}";
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post("/api/v1/auth/resend-verification")
                            .contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(post("/api/v1/auth/resend-verification")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().exists(HttpHeaders.RETRY_AFTER));
    }

    @Test
    void otherEndpoints_areNotThrottled() throws Exception {
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"username\":\"ghost" + i + "\",\"password\":\"x\"}"))
                    .andExpect(status().is4xxClientError());
        }
    }
}

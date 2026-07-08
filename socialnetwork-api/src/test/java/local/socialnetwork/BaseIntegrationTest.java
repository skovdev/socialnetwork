package local.socialnetwork;

import com.fasterxml.jackson.databind.ObjectMapper;

import local.socialnetwork.config.TestAwsSecretsConfig;
import local.socialnetwork.config.TestContainersConfig;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.context.annotation.Import;

import org.springframework.test.context.ActiveProfiles;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.transaction.annotation.Transactional;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.sesv2.SesV2Client;

/**
 * Base class for integration tests.
 *
 * <ul>
 *   <li>Loads the full Spring application context with a real PostgreSQL Testcontainer.</li>
 *   <li>AWS-dependent beans (Secrets Manager, SES, S3) are replaced by lightweight test doubles.</li>
 *   <li>{@code @Transactional} rolls back each test so the database stays clean between runs.</li>
 * </ul>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({TestContainersConfig.class, TestAwsSecretsConfig.class})
@Transactional
public abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    /** Replaces the real SesV2Client so registration tests never attempt live email delivery. */
    @MockitoBean
    SesV2Client sesV2Client;

    /** Replaces the real S3Client/S3Presigner so avatar tests never attempt live S3 calls. */
    @MockitoBean
    protected S3Client s3Client;

    @MockitoBean
    protected S3Presigner s3Presigner;
}

package gr.hua.dit.StudyRooms.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Ρύθμιση OpenAPI/Swagger για το REST API της εφαρμογής StudyRooms.
 *
 * Η κλάση:
 * - Ρυθμίζει JWT Bearer authentication για το Swagger UI
 * - Κάνει default όλα τα API endpoints να απαιτούν Authorization header
 * - Ομαδοποιεί και εμφανίζει μόνο τα endpoints του πακέτου web.rest
 *   που αντιστοιχούν στα paths /api/v1/**
 */
@Configuration
public class OpenApiConfig {

    public static final String BEARER_AUTH = "BearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("StudyRooms API")
                        .version("v1")
                        .description("Stateless REST API for StudyRooms (JWT protected)")
                )
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                        )
                )
                // Κάνει default όλα τα endpoints να ζητάνε Authorization δλδ Bearer <token>
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH));
    }

    @Bean
    public GroupedOpenApi groupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("api")
                .packagesToScan("gr.hua.dit.StudyRooms.web.rest")
                .pathsToMatch("/api/v1/**")
                .build();
    }
}
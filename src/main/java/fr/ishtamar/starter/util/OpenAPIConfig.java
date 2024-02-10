package fr.ishtamar.starter.util;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Value("${fr.ishtamar.starter.dev-url}")
    private String devUrl;

    @Value("${fr.ishtamar.starter.prod-url}")
    private String prodUrl;

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

    @Bean
    public OpenAPI OpenApi() {
        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Server URL in Development environment");

        Server prodServer = new Server();
        prodServer.setUrl(prodUrl);
        prodServer.setDescription("Server URL in Production environment");

        Contact contact = new Contact();
        contact.setEmail("test@test.com");
        contact.setName("test");
        contact.setUrl("https://test.com");

        Info info = new Info()
                .title("Test API")
                .version("1.0")
                .contact(contact)
                .description("This API exposes endpoints as a starter.").termsOfService("https://test.com");

        return new OpenAPI().addSecurityItem(new SecurityRequirement().
                addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes
                        ("Bearer Authentication", createAPIKeyScheme())).info(info).servers(List.of(devServer, prodServer));
    }
}

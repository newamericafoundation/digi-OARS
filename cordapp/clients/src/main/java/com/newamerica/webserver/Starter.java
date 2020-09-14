package com.newamerica.webserver;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.corda.client.jackson.JacksonSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jackson.JsonComponentModule;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static org.springframework.boot.WebApplicationType.SERVLET;

/**
 * Our Spring Boot application.
 */
@SpringBootApplication
@EnableSwagger2
public class Starter {

    //Register any other custom (de)Serializer classes.
    @Bean
    public Module jsonComponentModule() {
        return new JsonComponentModule();
    }

    //Force Spring/Jackson to use only provided Corda ObjectMapper for serialization.
    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(@Autowired NodeRPCConnection rpcConnection) {
        ObjectMapper mapper = JacksonSupport.createDefaultMapper(rpcConnection.proxy, new JsonFactory(), true);
        mapper.registerModule(jsonComponentModule());

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(mapper);
        return converter;
    }

    @Bean
    public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.basePackage("com.newamerica.webserver")).build();
    }
    /**
     * Starts our Spring Boot application.
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Starter.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setWebApplicationType(SERVLET);
        app.run(RestServiceCorsApplication.class, args);
    }
}
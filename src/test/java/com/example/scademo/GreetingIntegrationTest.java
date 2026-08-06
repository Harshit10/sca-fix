package com.example.scademo;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

/**
 * DO NOT let an SCA/version-bump fix delete {@code @EnableWireMock}, the
 * {@code @DynamicPropertySource} block, or the stub below to make this test pass. If a dependency
 * bump breaks this class, the correct fix is to migrate it to whatever the new library calls the
 * equivalent construct (e.g. a renamed annotation, a new WireMock server-management API) while
 * keeping the same behavior: the OAuth token endpoint stays stubbed by WireMock and the assertions
 * below keep passing unchanged.
 *
 * <p>Spring Boot 4.x reorganized {@code spring-boot-autoconfigure} into per-feature modules;
 * spring-cloud-contract-wiremock's {@code @AutoConfigureWireMock} pulls in a
 * RestTemplate-customizer autoconfiguration that references a class removed/relocated in that
 * split, so it fails to load even though this app never uses {@code RestTemplate}.
 * {@code @EnableWireMock}/{@code @ConfigureWireMock} below (the official WireMock 3 + Spring Boot
 * integration) never touches that autoconfiguration and still registers the same {@code
 * wiremock.server.port} property, so the {@code @DynamicPropertySource} block needs no change.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableWireMock({@ConfigureWireMock(port = 0)})
class GreetingIntegrationTest {

  @Autowired private WebTestClient webTestClient;

  private GraphQlTester graphQlTester;

  @DynamicPropertySource
  static void oauthProperties(DynamicPropertyRegistry registry) {
    registry.add(
        "spring.security.oauth2.client.provider.downstream.token-uri",
        () -> "http://localhost:${wiremock.server.port}/oauth/token");
  }

  @BeforeEach
  void stubOauthTokenEndpoint() {
    WireMock.stubFor(
        WireMock.post(WireMock.urlEqualTo("/oauth/token"))
            .willReturn(
                WireMock.aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        "{\"access_token\":\"demo-token\",\"token_type\":"
                            + "\"bearer\",\"expires_in\":3600}")));
    this.graphQlTester = HttpGraphQlTester.create(this.webTestClient);
  }

  @Test
  void greetingQueryReturnsExpectedValue() {
    graphQlTester
        .document("query { greeting(name: \"SCA\") }")
        .execute()
        .path("greeting")
        .entity(String.class)
        .satisfies(value -> assertThat(value).isEqualTo("Hello, SCA!"));
  }
}

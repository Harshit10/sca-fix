package com.example.scademo.graphql;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestClient;

/**
 * Deliberately trivial GraphQL data fetcher. It calls out to an "external" OAuth-protected
 * downstream (stubbed by WireMock in tests) so the demo exercises the same
 * OAuth-token-during-context-startup shape described in the integration test scaffolding.
 */
@Controller
public class GreetingResolver {

  private final RestClient restClient;

  public GreetingResolver(RestClient.Builder builder) {
    this.restClient = builder.build();
  }

  @QueryMapping
  public String greeting(@Argument String name) {
    return "Hello, " + name + "!";
  }
}

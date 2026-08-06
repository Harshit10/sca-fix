package com.example.scademo.graphql;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

class GreetingResolverUnitTest {

    @Test
    void greetingFormatsName() {
        GreetingResolver resolver = new GreetingResolver(RestClient.builder());
        assertThat(resolver.greeting("World")).isEqualTo("Hello, World!");
    }
}

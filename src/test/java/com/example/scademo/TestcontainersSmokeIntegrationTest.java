package com.example.scademo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * Exercises the {@code testcontainersVersion} pin in isolation from Spring/ WireMock so a
 * Testcontainers bump can be verified on its own. Do not delete the container wiring below to dodge
 * a failing bump — migrate it instead.
 */
@Testcontainers
class TestcontainersSmokeIntegrationTest {

  @Container
  static GenericContainer<?> echoServer =
      new GenericContainer<>(DockerImageName.parse("hashicorp/http-echo:latest"))
          .withCommand("-text=sca-demo")
          .withExposedPorts(5678);

  @Test
  void containerStartsAndIsReachable() {
    assertThat(echoServer.isRunning()).isTrue();
  }
}

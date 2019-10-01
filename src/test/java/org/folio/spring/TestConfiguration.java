package org.folio.spring;

import io.vertx.core.Context;
import io.vertx.core.Vertx;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Import(ApplicationConfig.class)
@PropertySource("classpath:test-application.properties")
public class TestConfiguration {

  @Bean
  public Vertx vertx(){
    //Initialize empty vertx object to be used by ApplicationConfig
    return Vertx.vertx();
  }

  @Bean
  public Context context(){
    //Initialize empty context
    return Vertx.vertx().getOrCreateContext();
  }
}

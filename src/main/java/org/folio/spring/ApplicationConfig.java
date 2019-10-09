package org.folio.spring;

import static org.folio.rest.exc.RestExceptionHandlers.baseBadRequestHandler;
import static org.folio.rest.exc.RestExceptionHandlers.baseNotFoundHandler;
import static org.folio.rest.exc.RestExceptionHandlers.baseUnauthorizedHandler;
import static org.folio.rest.exc.RestExceptionHandlers.baseUnprocessableHandler;
import static org.folio.rest.exc.RestExceptionHandlers.completionCause;
import static org.folio.rest.exc.RestExceptionHandlers.generalHandler;
import static org.folio.rest.exc.RestExceptionHandlers.logged;
import static org.folio.rest.exceptions.CustomFieldTypeExceptionHandler.customFieldTypeValidationHandler;

import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

import org.folio.common.pf.PartialFunction;
import org.folio.db.exc.translation.DBExceptionTranslator;
import org.folio.db.exc.translation.DBExceptionTranslatorFactory;

@Configuration
@ComponentScan(basePackages = {
  "org.folio.repository",
  "org.folio.service",
  "org.folio.validate"})
public class ApplicationConfig {

  @Bean
  public PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
    PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
    configurer.setLocation(new ClassPathResource("custom-fields-application.properties"));
    return configurer;
  }

  @Bean
  public PartialFunction<Throwable, Response> customFieldsExcHandler() {
    return logged(baseBadRequestHandler()
      .orElse(baseNotFoundHandler())
      .orElse(baseUnauthorizedHandler())
      .orElse(customFieldTypeValidationHandler())
      .orElse(baseUnprocessableHandler())
      .orElse(generalHandler())
      .compose(completionCause()));
  }

  @Bean
  public DBExceptionTranslator excTranslator(@Value("${db.exception.translator.name:postgresql}") String translatorName) {
    DBExceptionTranslatorFactory factory = DBExceptionTranslatorFactory.instance();
    return factory.create(translatorName);
  }
}

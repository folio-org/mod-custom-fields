package org.folio.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(
  ignoreUnknown = true
)
public class User {
    private String username;
    private String id;
}

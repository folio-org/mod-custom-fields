package org.folio.model;

import java.util.List;

import lombok.Value;

@Value
public class RecordUpdate {

  String refId;
  List<String> optionIdsToDelete;
  List<String> defaultIds;
}

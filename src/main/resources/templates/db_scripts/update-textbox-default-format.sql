UPDATE
   custom_fields
SET
   jsonb = jsonb || jsonb_set(jsonb, '{textField}', '{"fieldFormat": "TEXT"}')
WHERE
   jsonb ->> 'type' IN ('TEXTBOX_SHORT', 'TEXTBOX_LONG')
   AND (jsonb -> 'textField' IS NULL OR jsonb -> 'textField' -> 'fieldFormat' IS NULL);

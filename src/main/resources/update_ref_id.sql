CREATE OR REPLACE FUNCTION update_ref_id()
RETURNS TRIGGER AS $$
DECLARE
    newRefId text;
    oldRefId text;
    newCustomFieldName text;
    oldCustomFieldName text;
BEGIN
  newRefId = NEW.jsonb->'refId';
  oldRefId = OLD.jsonb->'refId';
  newCustomFieldName = NEW.jsonb->'name';
  oldCustomFieldName = OLD.jsonb->'name';

  if newRefId ISNULL                         then     newRefId := oldRefId;   end if;
  if newCustomFieldName = oldCustomFieldName then     newRefId := oldRefId;   end if;

  NEW.jsonb = jsonb_set(NEW.jsonb, '{refId}' ,  newRefId::jsonb , false);

  RETURN NEW;
END;
$$ language 'plpgsql';
DROP TRIGGER IF EXISTS update_ref_id_trigger ON custom_fields;
CREATE TRIGGER update_ref_id_trigger BEFORE UPDATE ON custom_fields FOR EACH ROW EXECUTE PROCEDURE update_ref_id();

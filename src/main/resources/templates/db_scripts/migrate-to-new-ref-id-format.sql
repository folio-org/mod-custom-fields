CREATE OR REPLACE FUNCTION ${myuniversity}_${mymodule}.camel_case_format(instr varchar) RETURNS varchar AS $$
DECLARE
BEGIN
  RETURN lower(left(instr, 1)) || right(regexp_replace(initcap(instr), '-|_1', '', 'g'), -1);
END;
$$ LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION ${myuniversity}_${mymodule}.update_users_ref_ids() RETURNS TRIGGER
AS $update_users_ref_ids$
  DECLARE
  	newRefId text;
    oldRefId text;
  BEGIN
    newRefId = NEW.jsonb->>'refId';
    oldRefId = OLD.jsonb->>'refId';
  UPDATE
     ${myuniversity}_${mymodule}.users
  SET
  jsonb = jsonb_set(jsonb, '{customFields}', ((jsonb->'customFields' || jsonb_build_object(newRefId, jsonb->'customFields'->oldRefId))) - oldRefId) WHERE jsonb->'customFields'->oldRefId IS NOT NULL;
    RETURN NEW;
  END;
$update_users_ref_ids$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS update_users_ref_ids_trigger ON ${myuniversity}_${mymodule}.custom_fields;
CREATE TRIGGER update_users_ref_ids_trigger
BEFORE UPDATE ON ${myuniversity}_${mymodule}.custom_fields FOR EACH ROW EXECUTE PROCEDURE ${myuniversity}_${mymodule}.update_users_ref_ids();

DROP TRIGGER IF EXISTS update_ref_id_trigger ON ${myuniversity}_${mymodule}.custom_fields;

UPDATE
${myuniversity}_${mymodule}.custom_fields
SET
jsonb = jsonb_set(jsonb, '{refId}', to_jsonb(${myuniversity}_${mymodule}.camel_case_format(jsonb->>'refId')));

DROP TRIGGER IF EXISTS update_users_ref_ids_trigger ON ${myuniversity}_${mymodule}.custom_fields;
DROP FUNCTION IF EXISTS ${myuniversity}_${mymodule}.camel_case_format;
DROP FUNCTION IF EXISTS ${myuniversity}_${mymodule}.update_users_ref_ids;

CREATE TRIGGER update_ref_id_trigger BEFORE UPDATE ON ${myuniversity}_${mymodule}.custom_fields FOR EACH ROW EXECUTE PROCEDURE ${myuniversity}_${mymodule}.update_ref_id();

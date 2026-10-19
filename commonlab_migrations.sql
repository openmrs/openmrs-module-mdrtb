set foreign_key_checks = 0;

/* USERS */
-- Preserve users which already exist
create table _users_new 
select distinct system_id, username, password, salt, secret_question, secret_answer, creator, date_created, changed_by, date_changed, person_id, retired, retired_by, date_retired, retire_reason, uuid from users 
where user_id  > 2;
-- Remvove all but admin and demon
delete from users where user_id  > 2;
-- Adjust auto-increment
alter table users auto_increment = 3;
-- Import users
insert into users (user_id, system_id, username, password, salt, secret_question, secret_answer, creator, date_created, changed_by, date_changed, person_id, retired, retired_by, date_retired, retire_reason, uuid) 
select 0, system_id, username, password, salt, secret_question, secret_answer, creator, date_created, changed_by, date_changed, person_id, retired, retired_by, date_retired, retire_reason, uuid from _users 
where user_id  > 2;
-- Import preserved users
insert into users (system_id, username, password, salt, secret_question, secret_answer, creator, date_created, changed_by, date_changed, person_id, retired, retired_by, date_retired, retire_reason, uuid) 
select system_id, username, password, salt, secret_question, secret_answer, creator, date_created, changed_by, date_changed, person_id, retired, retired_by, date_retired, retire_reason, uuid from _users_new
where username not in (select distinct username from users);
-- Providers
truncate provider;
insert into provider (provider_id, person_id, name, identifier, creator, date_created, changed_by, date_changed, retired, retired_by, date_retired, retire_reason, uuid) 
select provider_id, person_id, name, identifier, creator, date_created, changed_by, date_changed, retired, retired_by, date_retired, retire_reason, uuid from _provider;

-- Cleanup
drop table _users_new;


/* PEOPLE */
-- Import from person
insert ignore into person (person_id, gender, birthdate, birthdate_estimated, dead, death_date, cause_of_death, creator, date_created, changed_by, date_changed, voided, voided_by, date_voided, void_reason, uuid) 
select person_id, gender, birthdate, birthdate_estimated, dead, death_date, cause_of_death, creator, date_created, changed_by, date_changed, voided, voided_by, date_voided, void_reason, uuid from `_person` p ;
-- person_name
insert ignore into person_name (person_name_id, preferred, person_id, prefix, given_name, middle_name, family_name_prefix, family_name, family_name2, family_name_suffix, `degree`, creator, date_created, voided, voided_by, date_voided, void_reason, changed_by, date_changed, uuid) 
select person_name_id, preferred, person_id, prefix, given_name, middle_name, family_name_prefix, family_name, family_name2, family_name_suffix, `degree`, creator, date_created, voided, voided_by, date_voided, void_reason, changed_by, date_changed, uuid from _person_name ;
-- person_address
insert ignore into person_address (person_address_id, person_id, preferred, address1, address2, city_village, state_province, postal_code, country, latitude, longitude, start_date, end_date, creator, date_created, voided, voided_by, date_voided, void_reason, county_district, address3, address4, address5, address6, date_changed, changed_by, uuid) 
select person_address_id, person_id, preferred, address1, address2, city_village, state_province, postal_code, country, latitude, longitude, start_date, end_date, creator, date_created, voided, voided_by, date_voided, void_reason, county_district, address3, address4, address5, address6, date_changed, changed_by, uuid from _person_address ;
-- person_attribute_type
insert ignore into person_attribute_type (person_attribute_type_id, name, description, format, foreign_key, searchable, creator, date_created, changed_by, date_changed, retired, retired_by, date_retired, retire_reason, edit_privilege, sort_weight, uuid) 
values (9, 'волонтер (volunteer)', '', 'java.lang.String', NULL, 0, 1, '2011-11-08 11:31:01', 1, '2012-11-28 16:32:26', 1, 1, '2012-11-28 16:27:52', 'Not needed', NULL, 7.0, '8bd42c8c-52a9-4d6b-aede-e277706f9bec');
-- person_attribute
insert ignore into person_attribute (person_attribute_id, person_id, value, person_attribute_type_id, creator, date_created, changed_by, date_changed, voided, voided_by, date_voided, void_reason, uuid) 
select person_attribute_id, person_id, value, person_attribute_type_id, creator, date_created, changed_by, date_changed, voided, voided_by, date_voided, void_reason, uuid from _person_attribute ;


/* ROLES */
insert ignore into `role` select * from _role;
-- Role Privileges
insert ignore into role_privilege (`role`, privilege) 
select `role`, privilege from _role_privilege;
-- Parent-child role map
insert ignore into role_role (parent_role, child_role) 
select parent_role, child_role from `_role_role`;
-- User roles
insert ignore into user_role (user_id, role) 
select user_id, role from _user_role;



/* USER PROPERTY */
insert ignore into user_property (user_id, property, property_value) 
select user_id, property, property_value from _user_property 
where user_id > 1;


/* CONCEPT CLASS */
truncate concept_class ;
insert into concept_class (concept_class_id, name, description, creator, date_created, retired, retired_by, date_retired, retire_reason, uuid, date_changed, changed_by) 
select concept_class_id, name, description, creator, date_created, retired, retired_by, date_retired, retire_reason, uuid, date_changed, changed_by from _concept_class 
where name not in (select name from concept_class);
insert into concept_class (name, description, creator, date_created, retired, uuid) values ('Frequency', '', 1, current_timestamp(), 0, UUID());


/* CONCEPT DATATYPE */
truncate concept_datatype;
insert into concept_datatype (concept_datatype_id, name, hl7_abbreviation, description, creator, date_created, retired, retired_by, date_retired, retire_reason, uuid) 
select concept_datatype_id, name, hl7_abbreviation, description, creator, date_created, retired, retired_by, date_retired, retire_reason, uuid from _concept_datatype 
where name not in (select name from concept_datatype);


/* CONCEPT */
-- Import concepts
truncate concept ;
insert into concept (concept_id, retired, short_name, description, form_text, datatype_id, class_id, is_set, creator, date_created, version, changed_by, date_changed, retired_by, date_retired, retire_reason, uuid) 
select concept_id, retired, short_name, description, form_text, datatype_id, class_id, is_set, creator, date_created, version, changed_by, date_changed, retired_by, date_retired, retire_reason, uuid from _concept;


/* CONCEPT NAME */
truncate concept_name ;
insert into concept_name (concept_id, name, locale, locale_preferred, creator, date_created, concept_name_type, voided, voided_by, date_voided, void_reason, uuid, date_changed, changed_by) 
select concept_id, name, locale, locale_preferred, creator, date_created, concept_name_type, voided, voided_by, date_voided, void_reason, uuid, date_changed, changed_by from _concept_name;

-- Some SHORT names are found as preferred. This will throw an error on Editing concepts, therefore applying the fix
create table deleteme
select concept_name_id from concept_name where locale_preferred = 1 and concept_name_type = 'SHORT';
update concept_name set locale_preferred = 0 where concept_name_type = 'SHORT' and concept_name_id in (select d.concept_name_id from deleteme as d);
update concept_name set locale_preferred = 1 where concept_name_type = 'FULLY_SPECIFIED' and concept_name_id in (select d.concept_name_id from deleteme as d);
drop table deleteme;

/* CONCEPT NAME TAG */
truncate concept_name_tag ;
insert into concept_name_tag (tag, description, creator, date_created, voided, voided_by, date_voided, void_reason, uuid, date_changed, changed_by) 
select tag, description, creator, date_created, voided, voided_by, date_voided, void_reason, uuid, date_changed, changed_by from _concept_name_tag;


/* CONCEPT NAME TAG MAP */
truncate concept_name_tag_map ;
insert ignore into concept_name_tag_map (concept_name_id, concept_name_tag_id) 
select concept_name_id, concept_name_tag_id from _concept_name_tag_map;


/* CONCEPT DESCRIPTION */
truncate concept_description ;
insert into concept_description (concept_id, description, locale, creator, date_created, changed_by, date_changed, uuid) 
select concept_id, description, locale, creator, date_created, changed_by, date_changed, uuid from _concept_description;


/* CONCEPT ANSWER */
truncate concept_answer ;
insert into concept_answer (concept_id, answer_concept, answer_drug, creator, date_created, sort_weight, uuid) 
select concept_id, answer_concept, answer_drug, creator, date_created, sort_weight, uuid from _concept_answer;


/* CONCEPT NUMERIC */
truncate concept_numeric ;
insert into concept_numeric (concept_id, hi_absolute, hi_critical, hi_normal, low_absolute, low_critical, low_normal, units, allow_decimal, display_precision) 
select concept_id, hi_absolute, hi_critical, hi_normal, low_absolute, low_critical, low_normal, units, allow_decimal, display_precision from _concept_numeric;


/* CONCEPT REFERENCE SOURCE */
select * from concept_reference_source;
insert ignore into concept_reference_source (concept_source_id, name, description, hl7_code, creator, date_created, retired, retired_by, date_retired, retire_reason, uuid, unique_id, date_changed, changed_by) 
select concept_source_id, name, description, hl7_code, creator, date_created, retired, retired_by, date_retired, retire_reason, uuid, unique_id, date_changed, changed_by from _concept_reference_source;


/* CONCEPT REFERENCE TERM */
truncate concept_reference_term;
-- Import new reference terms
insert into concept_reference_term (concept_source_id, name, code, version, creator, date_created, retired, uuid) 
select distinct 2 as concept_source_id, cn.name, c.concept_id as code, '1.0' as version, 1, current_timestamp(), 0 as retired, uuid() from concept as c 
inner join concept_name as cn on cn.concept_id = c.concept_id and cn.locale = 'en' and cn.concept_name_type = 'FULLY_SPECIFIED'
where c.concept_id in (select c2.concept_id from `_concept` as c2);


/* CONCEPT REFERENCE MAP */
truncate concept_reference_map;
-- Import ignore because not all codes will map to concepts
insert ignore into concept_reference_map (concept_reference_term_id, concept_map_type_id, creator, date_created, concept_id, uuid) 
select distinct concept_reference_term_id, 1, 1, current_timestamp(), code, uuid() from concept_reference_term;


/* CONCEPT SET */
truncate concept_set ;
insert into concept_set (concept_id, concept_set, sort_weight, creator, date_created, uuid) 
select concept_id, concept_set, sort_weight, creator, date_created, uuid from _concept_set;


/* DRUG */
truncate drug;
insert into drug (drug_id, concept_id, name, combination, dosage_form, maximum_daily_dose, minimum_daily_dose, route, creator, date_created, retired, changed_by, date_changed, retired_by, date_retired, retire_reason, uuid, strength, dose_limit_units) 
select d.drug_id, d.concept_id, d.name, d.combination, d.dosage_form, d.maximum_daily_dose, d.minimum_daily_dose, d.route, d.creator, d.date_created, d.retired, d.changed_by, d.date_changed, d.retired_by, d.date_retired, d.retire_reason, d.uuid, d.dose_strength, cn.concept_id as units from _drug as d 
left join concept_name as cn on cn.name = d.units and cn.locale = 'en' and cn.voided = 0;


/* ENCOUNTER ROLE */
insert ignore into encounter_role (encounter_role_id, name, description, creator, date_created, changed_by, date_changed, retired, retired_by, date_retired, retire_reason, uuid) 
select encounter_role_id, name, description, creator, date_created, changed_by, date_changed, retired, retired_by, date_retired, retire_reason, uuid from _encounter_role;


/* ENCOUNTER TYPE */
-- ONLY when the data previously exists 
-- Update the encounter type in form dependency to ensure integrity with existing types
-- update form set encounter_type = encounter_type + (select max(encounter_type_id) + 1 from _encounter_type);
-- Increase the encounter type 
-- update encounter_type set encounter_type_id = encounter_type_id + (select max(encounter_type_id) + 1 from _encounter_type);
-- Import encounter types 
truncate encounter_type;
insert into encounter_type (encounter_type_id, name, description, creator, date_created, retired, retired_by, date_retired, retire_reason, uuid) 
select encounter_type_id, name, description, creator, date_created, retired, retired_by, date_retired, retire_reason, uuid from _encounter_type ;
-- Insert the missing drug order encounter type
insert into encounter_type (name, description, creator, date_created, retired, uuid) 
values ('Drug Order', 'Created to attach with Orders for Openmrs 2x.', 1, current_timestamp(), 0, uuid());


/* LOCATION */ 
truncate location_tag_map;
truncate location_attribute;
truncate location_attribute_type;
truncate location;

-- Add hierarchy level attribute type
insert into location_attribute_type (name,description,datatype,datatype_config,preferred_handler,handler_config,min_occurs,max_occurs,creator,date_created,changed_by,date_changed,retired,retired_by,date_retired,retire_reason,uuid) VALUES
('LEVEL','The geographical hierarchy level of the location','org.openmrs.customdatatype.datatype.SpecifiedTextOptionsDatatype',NULL,'org.openmrs.web.attribute.handler.SpecifiedTextOptionsDropdownHandler','UNKNOWN,REGION,SUBREGION,DISTRICT,FACILITY',0,1,1,'2023-01-01',1,'2023-01-17 18:50:28',0,NULL,NULL,NULL,'6b738ed1-78b3-4cdb-81f6-7fdc5da20a3d');

-- Import Unknown location
insert into location (location_id, name, description, country, state_province, county_district, creator, date_created, retired, retired_by, date_retired, retire_reason, parent_location, uuid) 
select location_id, name, description, 'Точикистон (Tajikistan)' as country, state_province, county_district, 1 as creator, date_created, retired, retired_by, date_retired, retire_reason, parent_location, uuid from _location l 
where location_id = 1;

-- Import Regions
insert into location (location_id, name, description, country, state_province, county_district, creator, date_created, retired, retired_by, date_retired, retire_reason, parent_location, uuid) 
select location_id, name, description, 'Точикистон (Tajikistan)' as country, state_province, county_district, 1 as creator, date_created, retired, retired_by, date_retired, retire_reason, parent_location, uuid from _location l 
where `level` = 'REGION';
-- Add level ATTRIBUTE as REGION
insert into location_attribute (location_id, attribute_type_id, value_reference, uuid, creator, date_created) 
select l.location_id, lat.location_attribute_type_id as attribute_type_id, 'REGION', UUID(), 1 as creator, current_timestamp() as date_created from _location as l
inner join location_attribute_type as lat on lat.name = 'LEVEL'
where l.level = 'REGION';

-- Import Sub-Regions
insert into location (location_id, name, description, country, state_province, county_district, creator, date_created, retired, parent_location, uuid) 
select l.location_id, l.name, l.description, 'Точикистон (Tajikistan)' as country, l.state_province, l.county_district, 1 as creator, l.date_created, l.retired, p.location_id as parent_location, l.uuid from _location l 
inner join _location as p on p.name = l.parent_location 
where l.`level` = 'SUBREGION';
-- Add level ATTRIBUTE as SUBREGION
insert into location_attribute (location_id, attribute_type_id, value_reference, uuid, creator, date_created) 
select l.location_id, lat.location_attribute_type_id as attribute_type_id, 'SUBREGION', UUID(), 1 as creator, current_timestamp() as date_created from _location as l 
inner join location_attribute_type as lat on lat.name = 'LEVEL' 
where l.level = 'SUBREGION';

-- Import Districts
insert into location (location_id, name, description, country, state_province, county_district, creator, date_created, retired, retired_by, date_retired, retire_reason, parent_location, uuid) 
select l.location_id, l.name, l.description, 'Точикистон (Tajikistan)' as country, l.state_province, l.county_district, 1 as creator, l.date_created, l.retired, l.retired_by, l.date_retired, l.retire_reason, p.location_id as parent_location, l.uuid from _location l 
inner join _location as p on p.name = l.parent_location 
where l.`level` = 'DISTRICT' ;
-- Add level ATTRIBUTE as DISTRICT
insert into location_attribute (location_id, attribute_type_id, value_reference, uuid, creator, date_created) 
select l.location_id, lat.location_attribute_type_id as attribute_type_id, 'DISTRICT', UUID(), 1 as creator, current_timestamp() as date_created from _location as l 
inner join location_attribute_type as lat on lat.name = 'LEVEL' 
where l.level = 'DISTRICT';

-- Import Facilities
insert into location (location_id, name, description, country, state_province, county_district, creator, date_created, retired, retired_by, date_retired, retire_reason, parent_location, uuid) 
select l.location_id, l.name, l.description, 'Точикистон (Tajikistan)' as country, l.state_province, l.county_district, 1 as creator, l.date_created, l.retired, l.retired_by, l.date_retired, l.retire_reason, p.location_id as parent_location, l.uuid from _location l 
inner join _location as p on p.name = l.parent_location 
where l.`level` = 'FACILITY';
-- Add level ATTRIBUTE as FACILITY
insert into location_attribute (location_id, attribute_type_id, value_reference, uuid, creator, date_created) 
select l.location_id, lat.location_attribute_type_id as attribute_type_id, 'FACILITY', UUID(), 1 as creator, current_timestamp() as date_created from _location as l 
inner join location_attribute_type as lat on lat.name = 'LEVEL' 
where l.level = 'FACILITY';

-- Import locations will NULL parents
insert into location (location_id, name, description, country, state_province, county_district, creator, date_created, retired, retired_by, date_retired, retire_reason, uuid) 
select l.location_id, l.name, l.description, 'Точикистон (Tajikistan)' as country, l.state_province, l.county_district, 1 as creator, l.date_created, l.retired, l.retired_by, l.date_retired, l.retire_reason, l.uuid from _location l 
where l.parent_location is null and l.parent_location not in (select name from location);
-- Add level ATTRIBUTE as FACILITY
insert into location_attribute (location_id, attribute_type_id, value_reference, uuid, creator, date_created) 
select l.location_id, lat.location_attribute_type_id as attribute_type_id, 'DISTRICT', UUID(), 1 as creator, current_timestamp() as date_created from _location as l 
inner join location_attribute_type as lat on lat.name = 'LEVEL' 
where l.level = 'DISTRICT' and l.parent_location not in (select name from location);
-- All remaining locations
insert into location (location_id, name, description, country, state_province, county_district, creator, date_created, retired, retired_by, date_retired, retire_reason, uuid) 
select l.location_id, l.name, l.description, 'Точикистон (Tajikistan)' as country, l.state_province, l.county_district, 1 as creator, l.date_created, l.retired, l.retired_by, l.date_retired, l.retire_reason, l.uuid from _location l 
where l.parent_location is null and l.retired = 1;

-- Location tags
insert ignore into location_tag (name,description,creator,date_created,retired,retired_by,date_retired,retire_reason,uuid,changed_by,date_changed) values 
('DOTS Facility','Location allows DOTS patient enrollment',1,'2023-01-17 21:39:09',0,NULL,NULL,NULL,'cabd6ef3-db2d-4e4f-9136-8beb70360ac6',1,'2023-01-17 21:46:27'),
('MDRTB Facility','Location allows MDR-TB patient enrollment',1,'2023-01-17 21:39:31',0,NULL,NULL,NULL,'53ceaf16-22ff-41a8-be31-8e43651c70e5',1,'2023-01-17 21:46:36'),
('Login Location','Allow user to Login from this location',1,'2023-01-17 21:41:10',0,NULL,NULL,NULL,'a68911ad-21a2-4590-9967-c2bdaf4a22c2',1,'2023-01-17 21:43:24'),
('Admission Location','Patients may only be admitted to inpatient care',1,'2023-01-17 21:41:35',0,NULL,NULL,NULL,'6af78c25-1246-4b04-9c38-0d8d274a4893',NULL,NULL),
('Transfer Location','Patients can be transferred into this location',1,'2023-01-17 21:42:10',0,NULL,NULL,NULL,'7711ecfc-8a58-44cf-b670-8783b8e633d5',1,'2023-01-17 21:43:41'),
('Laboratory','If this is a Laboratory',1,'2023-01-17 21:42:55',0,NULL,NULL,NULL,'663f8ed3-18cf-48e9-a887-a4665fbd249a',NULL,NULL),
('Culture Lab','This is a Laboratory providing Culture tests',1,'2023-01-17 21:44:14',0,NULL,NULL,NULL,'4703369f-3d6f-433f-b31c-9cf46035b96b',NULL,NULL),
('Prison','This location represents a Prison or Jail',1,'2023-01-17 21:46:54',0,NULL,NULL,NULL,'83922a9c-3a75-4acd-bdaa-24329611230f',NULL,NULL);
-- Insert tags for Unknown location
insert into location_tag_map (location_id, location_tag_id) 
select 1, location_tag_id from location_tag;

-- call purge_location(115, 94);


/* FORMS AND FIELDS*/
truncate field;
insert into field (field_id, name, description, field_type, concept_id, table_name, attribute_name, default_value, select_multiple, creator, date_created, changed_by, date_changed, retired, retired_by, date_retired, retire_reason, uuid) 
select field_id, name, description, field_type, concept_id, table_name, attribute_name, default_value, select_multiple, creator, date_created, changed_by, date_changed, retired, retired_by, date_retired, retire_reason, uuid from _field;

truncate form;
insert into form (form_id, name, version, build, published, xslt, template, description, encounter_type, creator, date_created, changed_by, date_changed, retired, retired_by, date_retired, retired_reason, uuid) 
select form_id, name, version, build, published, xslt, template, description, encounter_type, creator, date_created, changed_by, date_changed, retired, retired_by, date_retired, retired_reason, uuid from _form;

truncate form_field;
insert into form_field (form_field_id, form_id, field_id, field_number, field_part, page_number, parent_form_field, min_occurs, max_occurs, required, changed_by, date_changed, creator, date_created, sort_weight, uuid) 
select form_field_id, form_id, field_id, field_number, field_part, page_number, parent_form_field, min_occurs, max_occurs, required, changed_by, date_changed, creator, date_created, sort_weight, uuid from _form_field;

truncate htmlformentry_html_form;
insert into htmlformentry_html_form (id, form_id, name, xml_data, creator, date_created, changed_by, date_changed, retired, uuid, description, retired_by, date_retired, retire_reason)
select id, form_id, name, xml_data, creator, date_created, changed_by, date_changed, retired, uuid, description, retired_by, date_retired, retire_reason from _htmlformentry_html_form;


/* PROGRAMS */
truncate program;
insert into program (program_id, concept_id, outcomes_concept_id, creator, date_created, changed_by, date_changed, retired, name, description, uuid) 
select program_id, concept_id, outcomes_concept_id, creator, date_created, changed_by, date_changed, retired, name, description, uuid from _program;

truncate program_workflow;
insert into program_workflow (program_workflow_id, program_id, concept_id, creator, date_created, retired, changed_by, date_changed, uuid) 
select program_workflow_id, program_id, concept_id, creator, date_created, retired, changed_by, date_changed, uuid from _program_workflow;

truncate program_workflow_state;
insert into program_workflow_state (program_workflow_state_id, program_workflow_id, concept_id, initial, terminal, creator, date_created, retired, changed_by, date_changed, uuid) 
select program_workflow_state_id, program_workflow_id, concept_id, initial, terminal, creator, date_created, retired, changed_by, date_changed, uuid from _program_workflow_state;



/* PATIENTS */
-- Import Patients
truncate patient;
insert into patient (patient_id, creator, date_created, changed_by, date_changed, voided, voided_by, date_voided, void_reason) 
select patient_id, creator, date_created, changed_by, date_changed, voided, voided_by, date_voided, void_reason from _patient;
-- Import Patient Identifier Types
truncate patient_identifier_type;
insert into patient_identifier_type (patient_identifier_type_id, name, description, format, check_digit, creator, date_created, required, format_description, validator, location_behavior, retired, retired_by, date_retired, retire_reason, uuid, uniqueness_behavior, date_changed, changed_by) 
values (1, 'OpenMRS Identification Number','Unique number used in OpenMRS','',1,1,'2005-09-22 00:00:00',0,NULL,'org.openmrs.patient.impl.LuhnIdentifierValidator',NULL,0,NULL,NULL,NULL,'8d793bee-c2cc-11de-8d13-0010c6dffd0f',NULL,NULL,NULL);
insert into patient_identifier_type (patient_identifier_type_id, name, description, format, check_digit, creator, date_created, required, format_description, validator, location_behavior, retired, retired_by, date_retired, retire_reason, uuid) 
select patient_identifier_type_id, name, description, format, check_digit, creator, date_created, required, format_description, validator, location_behavior, retired, retired_by, date_retired, retire_reason, uuid from _patient_identifier_type;
-- Import Patient Identifiers
truncate patient_identifier;
insert into patient_identifier (patient_identifier_id, patient_id, identifier, identifier_type, preferred, location_id, creator, date_created, date_changed, changed_by, voided, voided_by, date_voided, void_reason, uuid)
select patient_identifier_id, patient_id, identifier, identifier_type, preferred, location_id, creator, date_created, date_changed, changed_by, voided, voided_by, date_voided, void_reason, uuid from _patient_identifier;
-- Import Patient Programs (DOTS/MDRTB)
truncate patient_program;
insert into patient_program (patient_program_id, patient_id, program_id, date_enrolled, date_completed, location_id, outcome_concept_id, creator, date_created, changed_by, date_changed, voided, voided_by, date_voided, void_reason, uuid) 
select patient_program_id, patient_id, program_id, date_enrolled, date_completed, location_id, outcome_concept_id, creator, date_created, changed_by, date_changed, voided, voided_by, date_voided, void_reason, uuid from _patient_program;
-- Import Patient States
truncate patient_state;
insert into patient_state (patient_state_id, patient_program_id, state, start_date, end_date, creator, date_created, changed_by, date_changed, voided, voided_by, date_voided, void_reason, uuid) 
select patient_state_id, patient_program_id, state, start_date, end_date, creator, date_created, changed_by, date_changed, voided, voided_by, date_voided, void_reason, uuid from _patient_state;


/* LOGIC */
create table if not exists logic_rule_definition ( id int(11) NOT NULL AUTO_INCREMENT, uuid char(38) NOT NULL, name varchar(255) NOT NULL, description varchar(1000) DEFAULT NULL, rule_content varchar(2048) NOT NULL, language varchar(255) NOT NULL, creator int(11) NOT NULL DEFAULT '0', date_created datetime NOT NULL, changed_by int(11) DEFAULT NULL, date_changed datetime DEFAULT NULL, retired smallint(6) NOT NULL DEFAULT '0', retired_by int(11) DEFAULT NULL, date_retired datetime DEFAULT NULL, retire_reason varchar(255) DEFAULT NULL, PRIMARY KEY (id), UNIQUE KEY name (name), KEY creator_idx (creator), KEY changed_by_idx (changed_by), KEY retired_by_idx (retired_by), CONSTRAINT changed_by_for_rule_definition FOREIGN KEY (changed_by) REFERENCES users (user_id), CONSTRAINT creator_for_rule_definition FOREIGN KEY (creator) REFERENCES users (user_id), CONSTRAINT retired_by_for_rule_definition FOREIGN KEY (retired_by) REFERENCES users (user_id) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
create table if not exists logic_rule_token ( logic_rule_token_id int(11) NOT NULL AUTO_INCREMENT, creator int(11) NOT NULL, date_created datetime NOT NULL, changed_by int(11) DEFAULT NULL, date_changed datetime DEFAULT NULL, token varchar(512) NOT NULL, class_name varchar(512) NOT NULL, state varchar(512) DEFAULT NULL, uuid char(38) NOT NULL, PRIMARY KEY (logic_rule_token_id), UNIQUE KEY uuid (uuid), KEY token_creator (creator), KEY token_changed_by (changed_by), CONSTRAINT token_changed_by FOREIGN KEY (changed_by) REFERENCES person (person_id), CONSTRAINT token_creator FOREIGN KEY (creator) REFERENCES person (person_id) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
create table if not exists logic_rule_token_tag ( logic_rule_token_id int(11) NOT NULL, tag varchar(512) NOT NULL, KEY token_tag (logic_rule_token_id), CONSTRAINT token_tag FOREIGN KEY (logic_rule_token_id) REFERENCES logic_rule_token (logic_rule_token_id) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
create table if not exists logic_token_registration ( token_registration_id int(11) NOT NULL AUTO_INCREMENT, creator int(11) NOT NULL, date_created datetime NOT NULL, changed_by int(11) DEFAULT NULL, date_changed datetime DEFAULT NULL, token varchar(512) NOT NULL, provider_class_name varchar(512) NOT NULL, provider_token varchar(512) NOT NULL, configuration varchar(2000) DEFAULT NULL, uuid char(38) NOT NULL, PRIMARY KEY (token_registration_id), UNIQUE KEY uuid (uuid), KEY token_registration_creator (creator), KEY token_registration_changed_by (changed_by), CONSTRAINT token_registration_changed_by FOREIGN KEY (changed_by) REFERENCES users (user_id), CONSTRAINT token_registration_creator FOREIGN KEY (creator) REFERENCES users (user_id) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
create table if not exists logic_token_registration_tag ( token_registration_id int(11) NOT NULL, tag varchar(512) NOT NULL, KEY token_registration_tag (token_registration_id), CONSTRAINT token_registration_tag FOREIGN KEY (token_registration_id) REFERENCES logic_token_registration (token_registration_id) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert ignore into logic_rule_definition (id, uuid, name, description, rule_content, `language`, creator, date_created, changed_by, date_changed, retired, retired_by, date_retired, retire_reason) 
select id, uuid, name, description, rule_content, `language`, creator, date_created, changed_by, date_changed, retired, retired_by, date_retired, retire_reason from _logic_rule_definition;

insert ignore into logic_token_registration (token_registration_id, creator, date_created, changed_by, date_changed, token, provider_class_name, provider_token, configuration, uuid) 
select token_registration_id, creator, date_created, changed_by, date_changed, token, provider_class_name, provider_token, configuration, uuid from _logic_token_registration;


/* ENCOUNTER TYPES */
-- Handled in the MDRTB module

/* ENCOUNTER */
truncate encounter;
insert into encounter (encounter_id, encounter_type, patient_id, location_id, form_id, encounter_datetime, creator, date_created, voided, voided_by, date_voided, void_reason, changed_by, date_changed, visit_id, uuid) 
select encounter_id, encounter_type, patient_id, location_id, form_id, encounter_datetime, creator, date_created, voided, voided_by, date_voided, void_reason, changed_by, date_changed, visit_id, uuid from _encounter ;
-- Insert a Drug Order encounter for each Order
insert into encounter (encounter_type, patient_id, location_id, encounter_datetime, creator, date_created, voided, voided_by, date_voided, void_reason, uuid) 
select (select encounter_type_id from encounter_type where name = 'Drug Order') as encounter_type, o.patient_id, 1 as location_id, o.start_date, 1, current_timestamp(), 1, 1, current_timestamp(), 'Automatically created and voided for Openmrs 2x upgrade.', uuid() from _orders as o;

truncate encounter_provider;
insert into encounter_provider (encounter_provider_id, encounter_id, provider_id, encounter_role_id, creator, date_created, changed_by, date_changed, voided, date_voided, voided_by, void_reason, uuid) 
select encounter_provider_id, encounter_id, provider_id, encounter_role_id, creator, date_created, changed_by, date_changed, voided, date_voided, voided_by, void_reason, uuid from _encounter_provider ;
-- Insert provider for Drug Order encounters
insert into encounter_provider (encounter_id, provider_id, encounter_role_id, creator, date_created, voided, date_voided, voided_by, void_reason, uuid) 
select encounter_id, 1, 1, 1, current_timestamp(), 1, current_timestamp(), 1, 'Automatically created and voided for Openmrs 2x upgrade.', uuid() from encounter 
where encounter_type in (select encounter_type_id from encounter_type where name = 'Drug Order');

insert ignore into order_type (order_type_id, name, description, creator, date_created, retired, retired_by, date_retired, retire_reason, uuid, java_class_name, parent) 
select order_type_id, name, description, creator, date_created, retired, retired_by, date_retired, retire_reason, uuid, concat('org.openmrs.', replace(name, ' ', '')), null from _order_type;

insert ignore into obs (obs_id, person_id, concept_id, encounter_id, order_id, obs_datetime, location_id, obs_group_id, accession_number, value_group_id, value_coded, value_coded_name_id, value_drug, value_datetime, value_numeric, value_modifier, value_text, value_complex, comments, creator, date_created, voided, voided_by, date_voided, void_reason, uuid, previous_version) 
select obs_id, person_id, concept_id, encounter_id, order_id, obs_datetime, location_id, obs_group_id, accession_number, value_group_id, value_coded, value_coded_name_id, value_drug, value_datetime, value_numeric, value_modifier, value_text, value_complex, comments, creator, date_created, voided, voided_by, date_voided, void_reason, uuid, previous_version from _obs;

-- Update units to correct concept names
update _drug_order set units = 'MILLIGRAM(S)' where units = 'mg';
update _drug_order set units = 'GRAM(S)' where units = 'g';
update _drug_order set units = 'MILLILITRE(S)' where units = 'ml';



-- Common Lab Module
-- Set the UUID for Specimen Type and Site
update global_property set property_value = '31bf065e-0370-102d-b0e3-001ec94a0cc1' where property = 'commonlabtest.specimenSiteConceptUuid';
update global_property set property_value = '2da61322-bcc5-4c32-b412-1b1ef37f4a25' where property = 'commonlabtest.specimenTypeConceptUuid';

truncate commonlabtest_type;
insert into commonlabtest_type (test_type_id, name, short_name, test_group, requires_specimen, reference_concept_id, description, creator, date_created, changed_by, date_changed, retired, retired_by, date_retired, retire_reason, uuid) 
select test_type_id, name, short_name, test_group, requires_specimen, reference_concept_id, description, creator, date_created, changed_by, date_changed, retired, retired_by, date_retired, retire_reason, uuid from _commonlabtest_type ;


-- Create temporary tables to separate out the results
drop table if exists _temp;
create table _temp 
select e.encounter_id, e.encounter_type, et.name as encounter_name, e.encounter_datetime, e.patient_id, o.obs_id, o.concept_id, cn.name as obs_question, o.value_coded, ocn.name as value_coded_name, o.value_numeric, o.value_text, o.value_datetime from encounter e 
inner join encounter_type et on et.encounter_type_id = e.encounter_type 
inner join obs o on o.encounter_id = e.encounter_id 
inner join concept_name cn on cn.concept_id = o.concept_id and cn.locale = 'en' and cn.concept_name_type = 'FULLY_SPECIFIED'
left join concept_name ocn on ocn.concept_id = o.value_coded and ocn.locale = 'en' and ocn.concept_name_type = 'FULLY_SPECIFIED';

-- Have a look at what constructs exist
select distinct obs_question from _temp 
where value_coded is null and value_numeric is null and value_text is null and value_datetime is null;

create table if not exists _temp_xpert select * from _temp where encounter_id in (select encounter_id from _temp where obs_question = 'TUBERCULOSIS XPERT TEST CONSTRUCT');
create table if not exists _temp_culture select * from _temp where encounter_id in (select encounter_id from _temp where obs_question = 'TUBERCULOSIS CULTURE CONSTRUCT');
create table if not exists _temp_hain select * from _temp where encounter_id in (select encounter_id from _temp where obs_question = 'TUBERCULOSIS HAIN TEST CONSTRUCT');
create table if not exists _temp_microscopy select * from _temp where encounter_id in (select encounter_id from _temp where obs_question = 'MICROSCOPY TEST CONSTRUCT');
create table if not exists _temp_hain2 select * from _temp where encounter_id in (select encounter_id from _temp where obs_question = 'HAIN 2 TEST CONSTRUCT');
create table if not exists _temp_dst1 select * from _temp where encounter_id in (select encounter_id from _temp where obs_question = 'DST1 CONSTRUCT');
create table if not exists _temp_dst1_mgit select * from _temp where encounter_id in (select encounter_id from _temp where obs_question = 'DST1 MGIT CONSTRUCT');
create table if not exists _temp_dst1_lj select * from _temp where encounter_id in (select encounter_id from _temp where obs_question = 'DST1 LJ CONSTRUCT');
create table if not exists _temp_dst2 select * from _temp where encounter_id in (select encounter_id from _temp where obs_question = 'DST2 CONSTRUCT');
create table if not exists _temp_dst2_mgit select * from _temp where encounter_id in (select encounter_id from _temp where obs_question = 'DST2 MGIT CONSTRUCT');
create table if not exists _temp_dst2_lj select * from _temp where encounter_id in (select encounter_id from _temp where obs_question = 'DST2 LJ CONSTRUCT');
create table if not exists _temp_mgit_template select * from _temp where encounter_id in (select encounter_id from _temp where obs_question = 'MGIT RESULT TEMPLATE');
create table if not exists _temp_lj_template select * from _temp where encounter_id in (select encounter_id from _temp where obs_question = 'L-J RESULT TEMPLATE');
create table if not exists _temp_contaminated_template select * from _temp where encounter_id in (select encounter_id from _temp where obs_question = 'CONTAMINATED TUBES RESULT TEMPLATE');


-- First, copy a placeholder order to be used when none other is present
truncate orders;
INSERT INTO orders (order_type_id,concept_id,orderer,encounter_id,instructions,date_activated,auto_expire_date,date_stopped,order_reason,order_reason_non_coded,creator,date_created,voided,voided_by,date_voided,void_reason,patient_id,accession_number,uuid,urgency,order_number,previous_order_id,order_action,comment_to_fulfiller,care_setting,scheduled_date,order_group_id,sort_weight,fulfiller_comment,fulfiller_status) VALUES
(1,410,3,147521,NULL,'2017-05-04 00:00:00',NULL,NULL,NULL,NULL,1,'2023-04-09 07:33:26',0,NULL,NULL,NULL,32072,NULL,'90ef1459-8b33-4a18-936e-2cfdfbe99649','ROUTINE','ORD-1',NULL,'NEW',NULL,1,NULL,NULL,NULL,NULL,NULL);

-- For each patient with a Test, create an Order from respective Lab Encounter (for now set the concept to Microscopy test)
-- First, store orders against all Lab Result encounters including Specimen collection
insert into orders (order_type_id, concept_id, orderer, encounter_id, date_activated, creator, date_created, voided, voided_by, date_voided, void_reason, patient_id, uuid, urgency, order_number, order_action, care_setting) 
select 3 as order_type_id, cn.concept_id, e.creator, e.encounter_id, e.encounter_datetime, p.provider_id as orderer, e.date_created, e.voided, e.voided_by, e.date_voided, e.voided_by, e.patient_id, UUID(), 'ROUTINE' as urgency, concat('ORD-', e.encounter_id) as order_number, 'NEW' as order_action, 1 as care_setting from encounter as e 
inner join users u on u.user_id = e.creator 
inner join provider p on p.person_id = u.person_id 
inner join concept_name cn on cn.name = 'MICROSCOPY TEST CONSTRUCT' and cn.locale = 'en' and cn.concept_name_type = 'FULLY_SPECIFIED' and cn.voided = 0 
where e.encounter_type in (5, 11);


-- Update the order and set the creator where respective user does not exist
update orders set creator = 1 where creator not in (select user_id from users);
update orders set voided_by = 1 where voided_by is not null and voided_by not in (select user_id from users);

/* Create Lab Test records for each Order */
truncate commonlabtest_test;
insert into commonlabtest_test (test_order_id, test_type_id, lab_reference_number, creator, date_created, voided, voided_by, date_voided, void_reason, uuid) 
select o.order_id, (case o.concept_id when 410 then 5 else 7 end) as test_type_id, concat(date_format(o.date_activated, '%Y%m%d'), '-', o.encounter_id) as lab_reference_number, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from orders as o 
where o.order_type_id = 3 and o.voided = 0;


/* Create a PROCESSED sample to ensure data integrity */
truncate commonlabtest_sample ;
insert into commonlabtest_sample (test_order_id, specimen_type, specimen_site, is_expirable, lab_sample_identifier, collector, status, creator, date_created, collection_date, processed_date, uuid) 
select test_order_id, 61 as specimen_type, 491 as specimen_site, 0, uuid() as lab_sample_identifier, creator, 'PROCESSED', creator, date_created, date_created, date_created, uuid() from commonlabtest_test 
where voided = 0;

/* Rather import manually or via Talend job
truncate commonlabtest_attribute_type;
insert into commonlabtest_attribute_type (test_attribute_type_id,test_type_id,name,datatype,min_occurs,max_occurs,datatype_config,sort_weight,description,preferred_handler,hint,group_name,multiset_name,creator,date_created,retired,uuid) 
select test_attribute_type_id,test_type_id,name,datatype,min_occurs,max_occurs,datatype_config,sort_weight,description,preferred_handler,hint,group_name,multiset_name,creator,date_created,retired,uuid from _commonlabtest_attribute_type;
*/

-- Update description to include group name
update commonlabtest_attribute_type set description = concat(description, ' - ', group_name) where group_name is not null and description is null;

/* Tasks
 * 1. commonlabtest_sample.collection_date = DATE COLLECTED
 * 2. commonlabtest_sample.comments = TUBERCULOSIS SPECIMEN COMMENTS
 * 3. Void TUBERCULOSIS TEST DATE ORDERED
 * 4. commonlabtest_sample.processed_date = TUBERCULOSIS TEST RESULT DATE
 */
truncate commonlabtest_attribute;

/* COMMON TEST */
-- Add attributes - DATE COLLECTED (will be repeated for each test time if there are multiple attributes matching)
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, tat.test_attribute_type_id, o.value_datetime, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join commonlabtest_attribute_type as tat on tat.test_type_id = 5 and tat.name = 'DATE COLLECTED' 
where o.concept_id = 188 and o.voided = 0;

-- Add attributes - INVESTIGATION DATE
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, tat.test_attribute_type_id, o.value_datetime, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join commonlabtest_attribute_type as tat on tat.test_type_id = 5 and tat.name = 'INVESTIGATION DATE' 
where o.concept_id = 427 and o.voided = 0;

-- Add attributes - LABORATORY INVESTIGATION NUMBER
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, tat.test_attribute_type_id, o.value_text, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join commonlabtest_attribute_type as tat on tat.test_type_id = 5 and tat.name = 'LABORATORY INVESTIGATION NUMBER' 
where o.concept_id = 428 and o.voided = 0 and o.obs_group_id is null;

-- Add attributes - PURPOSE OF INVESTIGATION
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, tat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join commonlabtest_attribute_type as tat on tat.test_type_id = 5 and tat.name = 'PURPOSE OF INVESTIGATION' 
where o.concept_id = 425 and o.voided = 0;

-- Add attributes - REFERRING FACILITY
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, tat.test_attribute_type_id, o.value_text, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join commonlabtest_attribute_type as tat on tat.test_type_id = 5 and tat.name = 'REFERRING FACILITY' 
where o.concept_id = 498 and o.voided = 0 and length(o.value_text) < 255;

-- Add attributes - REQUESTING MEDICAL FACILITY
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, tat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join commonlabtest_attribute_type as tat on tat.test_type_id = 5 and tat.name = 'REQUESTING MEDICAL FACILITY' 
where o.concept_id = 426 and o.voided = 0;

-- Add attributes - TUBERCULOSIS SAMPLE SOURCE
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, tat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join commonlabtest_attribute_type as tat on tat.test_type_id = 5 and tat.name = 'TUBERCULOSIS SAMPLE SOURCE' 
where o.concept_id = 67 and o.voided = 0;

-- Add attributes - DATE OF REQUEST FOR LABORATORY INVESTIGATION
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, tat.test_attribute_type_id, o.value_datetime, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join commonlabtest_attribute_type as tat on tat.test_type_id = 5 and tat.name = 'DATE OF REQUEST FOR LABORATORY INVESTIGATION' 
where o.concept_id = 589 and o.voided = 0;

-- Add attributes - LAB SPECIALIST NAME
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, tat.test_attribute_type_id, o.value_text, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join commonlabtest_attribute_type as tat on tat.test_type_id = 5 and tat.name = 'LAB SPECIALIST NAME' 
where o.concept_id = 611 and o.voided = 0;

-- Add attributes - TUBERCULOSIS SPECIMEN COMMENTS
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, o.value_text, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 311 
inner join commonlabtest_attribute_type cat on cat.test_type_id = 5 and cat.name = 'TUBERCULOSIS SPECIMEN COMMENTS' 
where o.concept_id = 149 and o.voided = 0;

-- Add attributes - REFERRED BY
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, tat.test_attribute_type_id, o.value_text, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join commonlabtest_attribute_type as tat on tat.test_type_id = 5 and tat.name = 'REFERRED BY' 
where o.concept_id = 497 and o.voided = 0;


/**************/
/* XPERT TEST */
/**************/
-- Add attributes - APPEARANCE OF SPECIMEN
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join commonlabtest_attribute_type cat on cat.test_type_id = 5 and cat.group_name = 'XPERT' and cat.name = 'APPEARANCE OF SPECIMEN' 
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 311 
where o.concept_id = 174 and o.voided = 0;
-- Add attributes - MTB RESULT
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 311 
inner join commonlabtest_attribute_type cat on cat.test_type_id = 5 and cat.group_name = 'XPERT' and cat.name = 'MTB RESULT' 
where o.concept_id = 312 and o.voided = 0;
-- Add attributes - ERROR CODE
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, o.value_text, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 311 
inner join commonlabtest_attribute_type cat on cat.test_type_id = 5 and cat.group_name = 'XPERT' and cat.name = 'ERROR CODE' 
where o.concept_id = 316 and o.voided = 0 and o.value_text regexp '^[0-9]*$';
-- Add attributes - XPERT MTB BURDEN
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 311 
inner join commonlabtest_attribute_type cat on cat.test_type_id = 5 and cat.group_name = 'XPERT' and cat.name = 'XPERT MTB BURDEN' 
where o.concept_id = 318 and o.voided = 0;
-- Add attributes - ISONIAZID RESULT
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 311 
inner join commonlabtest_attribute_type cat on cat.test_type_id = 5 and cat.group_name = 'XPERT' and cat.name = 'ISONIAZID RESULT' 
where o.concept_id = 322 and o.voided = 0;
-- Add attributes - RIFAMPICIN RESULT
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 311 
inner join commonlabtest_attribute_type cat on cat.test_type_id = 5 and cat.group_name = 'XPERT' and cat.name = 'RIFAMPICIN RESULT' 
where o.concept_id = 317 and o.voided = 0;
-- Add attributes - TUBERCULOSIS TEST RESULT DATE
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, o.value_datetime, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 311 
inner join commonlabtest_attribute_type cat on cat.test_type_id = 5 and cat.group_name = 'XPERT' and cat.name = 'TUBERCULOSIS TEST RESULT DATE' 
where o.concept_id = 68 and o.voided = 0;
-- Add attributes - DATE OF SENDING TO CULTURE
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, o.value_datetime, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 311 
inner join commonlabtest_attribute_type cat on cat.test_type_id = 5 and cat.group_name = 'XPERT' and cat.name = 'DATE OF SENDING TO CULTURE' 
where o.concept_id = 607 and o.voided = 0;
-- Add attributes - DATE OF SENDING TO DST
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, o.value_datetime, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 311 
inner join commonlabtest_attribute_type cat on cat.test_type_id = 5 and cat.group_name = 'XPERT' and cat.name = 'DATE OF SENDING TO DST' 
where o.concept_id = 608 and o.voided = 0;


-- Add attributes - MTB RESULT (for HAIN)
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 323 
inner join commonlabtest_attribute_type cat on cat.test_type_id = 5 and cat.group_name = 'HAIN' and cat.name = 'MTB RESULT' 
where o.concept_id = 312 and o.voided = 0;
-- Add attributes - MTB RESULT (for HAIN2)
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 414 
inner join commonlabtest_attribute_type cat on cat.test_type_id = 5 and cat.group_name = 'HAIN2' and cat.name = 'MTB RESULT'
where o.concept_id = 312 and o.voided = 0;


/* DST2 TEST */
-- Collect all Drug results in a separate table
drop table if exists _drug_result;
create table _drug_result 
select o2.encounter_id, ct.test_order_id, ct.test_type_id, o.obs_datetime, concat(cn2.name, ' RESISTANCE') as drug, cn.name as answer, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
left join concept_name cn on cn.concept_id = o.concept_id and cn.concept_name_type = 'FULLY_SPECIFIED' and cn.locale = 'en' 
left join concept_name cn2 on cn2.concept_id = o.value_coded and cn2.concept_name_type = 'FULLY_SPECIFIED' and cn2.locale = 'en' 
where ct.test_type_id = 7 and o.concept_id in (118, 132);

-- insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid) 
select 0, o.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from _drug_result as o 
inner join commonlabtest_attribute_type cat on cat.name = o.drug and cat.test_type_id = o.test_type_id 
inner join concept_name cn on cn.name = o.drug and cn.concept_name_type = 'FULLY_SPECIFIED' and cn.locale = 'en' 
inner join concept_name cn2 on cn2.name = o.answer and cn2.concept_name_type = 'FULLY_SPECIFIED' and cn2.locale = 'en' 
inner join concept c on c.concept_id = cn2.concept_id
where o.voided = 0;

-- Enc, Obs, Q, A, Parent, Parent Q, Parent A from All Encounters
drop table if exists _encounter_results;
create table _encounter_results ;
select o.encounter_id, o.person_id, o.obs_id, o.obs_group_id, cnp.name as parent_concept, o.obs_datetime, o.concept_id, cn.name as question, o.value_numeric, o.value_text, o.value_datetime, o.value_coded, cn2.name as answer, o.location_id, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason from encounter e 
inner join obs o on o.encounter_id = e.encounter_id 
inner join concept_name cn on cn.concept_id = o.concept_id and cn.locale = 'en' and cn.concept_name_type = 'FULLY_SPECIFIED' 
left join concept_name cn2 on cn2.concept_id = o.value_coded and cn2.locale = 'en' and cn2.concept_name_type = 'FULLY_SPECIFIED' 
left join obs op on op.obs_id = o.obs_group_id 
left join concept_name cnp on cnp.concept_id = op.concept_id and cnp.locale = 'en' and cnp.concept_name_type = 'FULLY_SPECIFIED' 
where e.encounter_type in (5, 7, 11) and e.voided = 0 and o.voided = 0;

-- select e.patient_id, et.name, p.concept_id as parent_concept_id, get_concept_name(p.concept_id) as parent, o.obs_id, o.concept_id, get_concept_name(o.concept_id) as concept, o.encounter_id, o.obs_datetime, o.obs_group_id, o.value_coded, get_concept_name(o.value_coded) as answer, o.value_text from obs o 
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from _encounter_results as o 
inner join encounter e on e.encounter_id = o.encounter_id 
inner join encounter_type et on et.encounter_type_id = e.encounter_type 
inner join obs p on p.obs_id = o.obs_group_id 
inner join orders o2 on o2.encounter_id = e.encounter_id 
inner join concept c on c.concept_id = o.concept_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join commonlabtest_attribute_type cat on cat.test_type_id = 5 and cat.name = 'AMIKACIN RESISTANCE' 
where p.concept_id in (118, 138) and o.value_coded = 115;

insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from _encounter_results as o 
inner join encounter e on e.encounter_id = o.encounter_id 
inner join encounter_type et on et.encounter_type_id = e.encounter_type 
inner join obs p on p.obs_id = o.obs_group_id 
inner join orders o2 on o2.encounter_id = e.encounter_id 
inner join concept c on c.concept_id = o.concept_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join commonlabtest_attribute_type cat on cat.test_type_id = 5 and cat.name = 'CAPREOMYCIN RESISTANCE' 
where p.concept_id in (118, 138) and o.value_coded = 107;

insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from _encounter_results as o 
inner join encounter e on e.encounter_id = o.encounter_id 
inner join encounter_type et on et.encounter_type_id = e.encounter_type 
inner join obs p on p.obs_id = o.obs_group_id 
inner join orders o2 on o2.encounter_id = e.encounter_id 
inner join concept c on c.concept_id = o.concept_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join commonlabtest_attribute_type cat on cat.test_type_id = 5 and cat.name = 'CIPROFLOXACIN RESISTANCE' 
where p.concept_id in (118, 138) and o.value_coded = 79;

insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from _encounter_results as o 
inner join encounter e on e.encounter_id = o.encounter_id 
inner join encounter_type et on et.encounter_type_id = e.encounter_type 
inner join obs p on p.obs_id = o.obs_group_id 
inner join orders o2 on o2.encounter_id = e.encounter_id 
inner join concept c on c.concept_id = o.concept_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join commonlabtest_attribute_type cat on cat.test_type_id = 5 and cat.name = 'CLARITHROMYCIN RESISTANCE' 
where p.concept_id in (118, 138) and o.value_coded = 93;

insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from _encounter_results as o 
inner join encounter e on e.encounter_id = o.encounter_id 
inner join encounter_type et on et.encounter_type_id = e.encounter_type 
inner join obs p on p.obs_id = o.obs_group_id 
inner join orders o2 on o2.encounter_id = e.encounter_id 
inner join concept c on c.concept_id = o.concept_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join commonlabtest_attribute_type cat on cat.test_type_id = 5 and cat.name = 'CLOFAZAMINE RESISTANCE' 
where p.concept_id in (118, 138) and o.value_coded = 72;

insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from _encounter_results as o 
inner join encounter e on e.encounter_id = o.encounter_id 
inner join encounter_type et on et.encounter_type_id = e.encounter_type 
inner join obs p on p.obs_id = o.obs_group_id 
inner join orders o2 on o2.encounter_id = e.encounter_id 
inner join concept c on c.concept_id = o.concept_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join commonlabtest_attribute_type cat on cat.test_type_id = 5 and cat.name = 'CYCLOSERINE RESISTANCE' 
where p.concept_id in (118, 138) and o.value_coded = 104;

insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from _encounter_results as o 
inner join encounter e on e.encounter_id = o.encounter_id 
inner join encounter_type et on et.encounter_type_id = e.encounter_type 
inner join obs p on p.obs_id = o.obs_group_id 
inner join orders o2 on o2.encounter_id = e.encounter_id 
inner join concept c on c.concept_id = o.concept_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join commonlabtest_attribute_type cat on cat.test_type_id = 5 and cat.name = 'ETHAMBUTOL RESISTANCE' 
where p.concept_id in (118, 138) and o.value_coded = 113;

insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from _encounter_results as o 
inner join encounter e on e.encounter_id = o.encounter_id 
inner join encounter_type et on et.encounter_type_id = e.encounter_type 
inner join obs p on p.obs_id = o.obs_group_id 
inner join orders o2 on o2.encounter_id = e.encounter_id 
inner join concept c on c.concept_id = o.concept_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join commonlabtest_attribute_type cat on cat.test_type_id = 5 and cat.name = 'ETHIONAMIDE RESISTANCE' 
where p.concept_id in (118, 138) and o.value_coded = 76;

insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from _encounter_results as o 
inner join encounter e on e.encounter_id = o.encounter_id 
inner join encounter_type et on et.encounter_type_id = e.encounter_type 
inner join obs p on p.obs_id = o.obs_group_id 
inner join orders o2 on o2.encounter_id = e.encounter_id 
inner join concept c on c.concept_id = o.concept_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join commonlabtest_attribute_type cat on cat.test_type_id = 5 and cat.name = 'GATIFLOXACIN RESISTANCE' 
where p.concept_id in (118, 138) and o.value_coded = 117;

insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from _encounter_results as o 
inner join encounter e on e.encounter_id = o.encounter_id 
inner join encounter_type et on et.encounter_type_id = e.encounter_type 
inner join obs p on p.obs_id = o.obs_group_id 
inner join orders o2 on o2.encounter_id = e.encounter_id 
inner join concept c on c.concept_id = o.concept_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join commonlabtest_attribute_type cat on cat.test_type_id = 5 and cat.name = 'ISONIAZID RESISTANCE' 
where p.concept_id in (118, 138) and o.value_coded = 92;

insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from _encounter_results as o 
inner join encounter e on e.encounter_id = o.encounter_id 
inner join encounter_type et on et.encounter_type_id = e.encounter_type 
inner join obs p on p.obs_id = o.obs_group_id 
inner join orders o2 on o2.encounter_id = e.encounter_id 
inner join concept c on c.concept_id = o.concept_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join commonlabtest_attribute_type cat on cat.test_type_id = 5 and cat.name = 'KANAMYCIN RESISTANCE' 
where p.concept_id in (118, 138) and o.value_coded = 112;

insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from _encounter_results as o 
inner join encounter e on e.encounter_id = o.encounter_id 
inner join encounter_type et on et.encounter_type_id = e.encounter_type 
inner join obs p on p.obs_id = o.obs_group_id 
inner join orders o2 on o2.encounter_id = e.encounter_id 
inner join concept c on c.concept_id = o.concept_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join commonlabtest_attribute_type cat on cat.test_type_id = 5 and cat.name = 'LEVOFLOXACIN RESISTANCE' 
where p.concept_id in (118, 138) and o.value_coded = 101;

insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from _encounter_results as o 
inner join encounter e on e.encounter_id = o.encounter_id 
inner join encounter_type et on et.encounter_type_id = e.encounter_type 
inner join obs p on p.obs_id = o.obs_group_id 
inner join orders o2 on o2.encounter_id = e.encounter_id 
inner join concept c on c.concept_id = o.concept_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join commonlabtest_attribute_type cat on cat.test_type_id = 5 and cat.name = 'MOXIFLOXACIN RESISTANCE' 
where p.concept_id in (118, 138) and o.value_coded = 109;

insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from _encounter_results as o 
inner join encounter e on e.encounter_id = o.encounter_id 
inner join encounter_type et on et.encounter_type_id = e.encounter_type 
inner join obs p on p.obs_id = o.obs_group_id 
inner join orders o2 on o2.encounter_id = e.encounter_id 
inner join concept c on c.concept_id = o.concept_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join commonlabtest_attribute_type cat on cat.test_type_id = 5 and cat.name = 'OFLOXACIN RESISTANCE' 
where p.concept_id in (118, 138) and o.value_coded = 108;

insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from _encounter_results as o 
inner join encounter e on e.encounter_id = o.encounter_id 
inner join encounter_type et on et.encounter_type_id = e.encounter_type 
inner join obs p on p.obs_id = o.obs_group_id 
inner join orders o2 on o2.encounter_id = e.encounter_id 
inner join concept c on c.concept_id = o.concept_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join commonlabtest_attribute_type cat on cat.test_type_id = 5 and cat.name = 'P-AMINOSALICY RESISTANCE' 
where p.concept_id in (118, 138) and o.value_coded = 98;

insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from _encounter_results as o 
inner join encounter e on e.encounter_id = o.encounter_id 
inner join encounter_type et on et.encounter_type_id = e.encounter_type 
inner join obs p on p.obs_id = o.obs_group_id 
inner join orders o2 on o2.encounter_id = e.encounter_id 
inner join concept c on c.concept_id = o.concept_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join commonlabtest_attribute_type cat on cat.test_type_id = 5 and cat.name = 'PROTHIONAMIDE RESISTANCE' 
where p.concept_id in (118, 138) and o.value_coded = 42;

insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from _encounter_results as o 
inner join encounter e on e.encounter_id = o.encounter_id 
inner join encounter_type et on et.encounter_type_id = e.encounter_type 
inner join obs p on p.obs_id = o.obs_group_id 
inner join orders o2 on o2.encounter_id = e.encounter_id 
inner join concept c on c.concept_id = o.concept_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join commonlabtest_attribute_type cat on cat.test_type_id = 5 and cat.name = 'PYRAZINAMIDE RESISTANCE' 
where p.concept_id in (118, 138) and o.value_coded = 41;

insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from _encounter_results as o 
inner join encounter e on e.encounter_id = o.encounter_id 
inner join encounter_type et on et.encounter_type_id = e.encounter_type 
inner join obs p on p.obs_id = o.obs_group_id 
inner join orders o2 on o2.encounter_id = e.encounter_id 
inner join concept c on c.concept_id = o.concept_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join commonlabtest_attribute_type cat on cat.test_type_id = 5 and cat.name = 'PYRIDOXINE RESISTANCE' 
where p.concept_id in (118, 138) and o.value_coded = 73;

insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from _encounter_results as o 
inner join encounter e on e.encounter_id = o.encounter_id 
inner join encounter_type et on et.encounter_type_id = e.encounter_type 
inner join obs p on p.obs_id = o.obs_group_id 
inner join orders o2 on o2.encounter_id = e.encounter_id 
inner join concept c on c.concept_id = o.concept_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join commonlabtest_attribute_type cat on cat.test_type_id = 5 and cat.name = 'RIFABUTIN RESISTANCE' 
where p.concept_id in (118, 138) and o.value_coded = 114;

insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from _encounter_results as o 
inner join encounter e on e.encounter_id = o.encounter_id 
inner join encounter_type et on et.encounter_type_id = e.encounter_type 
inner join obs p on p.obs_id = o.obs_group_id 
inner join orders o2 on o2.encounter_id = e.encounter_id 
inner join concept c on c.concept_id = o.concept_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join commonlabtest_attribute_type cat on cat.test_type_id = 5 and cat.name = 'RIFAMPICIN RESISTANCE' 
where p.concept_id in (118, 138) and o.value_coded = 74;

insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from _encounter_results as o 
inner join encounter e on e.encounter_id = o.encounter_id 
inner join encounter_type et on et.encounter_type_id = e.encounter_type 
inner join obs p on p.obs_id = o.obs_group_id 
inner join orders o2 on o2.encounter_id = e.encounter_id 
inner join concept c on c.concept_id = o.concept_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join commonlabtest_attribute_type cat on cat.test_type_id = 5 and cat.name = 'STREPTOMYCIN RESISTANCE' 
where p.concept_id in (118, 138) and o.value_coded = 75;

insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from _encounter_results as o 
inner join encounter e on e.encounter_id = o.encounter_id 
inner join encounter_type et on et.encounter_type_id = e.encounter_type 
inner join obs p on p.obs_id = o.obs_group_id 
inner join orders o2 on o2.encounter_id = e.encounter_id 
inner join concept c on c.concept_id = o.concept_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join commonlabtest_attribute_type cat on cat.test_type_id = 5 and cat.name = 'TERIZIDONE RESISTANCE' 
where p.concept_id in (118, 138) and o.value_coded = 40;

insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from _encounter_results as o 
inner join encounter e on e.encounter_id = o.encounter_id 
inner join encounter_type et on et.encounter_type_id = e.encounter_type 
inner join obs p on p.obs_id = o.obs_group_id 
inner join orders o2 on o2.encounter_id = e.encounter_id 
inner join concept c on c.concept_id = o.concept_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join commonlabtest_attribute_type cat on cat.test_type_id = 5 and cat.name = 'THIOACETAZONE RESISTANCE' 
where p.concept_id in (118, 138) and o.value_coded = 99;

insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from _encounter_results as o 
inner join encounter e on e.encounter_id = o.encounter_id 
inner join encounter_type et on et.encounter_type_id = e.encounter_type 
inner join obs p on p.obs_id = o.obs_group_id 
inner join orders o2 on o2.encounter_id = e.encounter_id 
inner join concept c on c.concept_id = o.concept_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join commonlabtest_attribute_type cat on cat.test_type_id = 5 and cat.name = 'VIOMYCIN RESISTANCE' 
where p.concept_id in (118, 138) and o.value_coded = 116;
-- OTHER RESISTANCE = ?
-- DELAMANID RESISTANCE = ?
-- LINEZOLID RESISTANCE = ?

insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from _encounter_results as o 
inner join encounter e on e.encounter_id = o.encounter_id 
inner join encounter_type et on et.encounter_type_id = e.encounter_type 
inner join obs p on p.obs_id = o.obs_group_id 
inner join orders o2 on o2.encounter_id = e.encounter_id 
inner join concept c on c.concept_id = o.concept_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join commonlabtest_attribute_type cat on cat.test_type_id = 5 and cat.name = 'VIOMYCIN RESISTANCE' 
where p.concept_id in (118, 138) and o.value_coded = 116

select o.* from _encounter_results as o 
inner join encounter e on e.encounter_id = o.encounter_id 
inner join encounter_type et on et.encounter_type_id = e.encounter_type 
inner join obs p on p.obs_id = o.obs_group_id 
inner join orders o2 on o2.encounter_id = e.encounter_id 
inner join concept c on c.concept_id = o.concept_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
where o.encounter_id = 679497;


/* REPORT DATA */
-- Import location hierarchies
drop table if exists _address_hierarchy;
create table _address_hierarchy 
select ahe.address_hierarchy_entry_id as id, ahe.name, ahe.level_id, 0 as location_id, 
p.address_hierarchy_entry_id as parent_id, p.name as parent_name, p.level_id as parent_level, pl.location_id as parent_location_id, 
gp.parent_id as grandparent_id, gp.name as grandparent_name, gp.level_id as grandparent_level, gpl.location_id as grandparent_location_id from openmrspih.address_hierarchy_entry ahe 
left join openmrspih.address_hierarchy_entry p on p.address_hierarchy_entry_id = ahe.parent_id and p.level_id <> 1 
left join openmrspih.address_hierarchy_entry gp on gp.address_hierarchy_entry_id = p.parent_id and gp.level_id <> 1 
left join openmrspih.location gpl on gpl.name = gp.name and gpl.state_province = gp.name and gpl.retired = 0 
left join location pl on pl.name = p.name and pl.retired = 0 ;

-- Really messy method of finding out the right location IDs
update `_address_hierarchy` set grandparent_location_id = (
    case grandparent_name 
        when 'Кулябский регион' then 271 
        when 'Душанбе' then 272 
        else NULL
    end
) where grandparent_location_id is null;

update `_address_hierarchy` set parent_location_id = (
    case parent_name 
        when 'Другой' then 5 
        when 'Сугд' then 201 
        when 'Кургантюбинский регион' then 270
        when 'НТМ' then 273 
        when 'Хатлон' then 274 
        when 'ВМКБ' then 276 
        else NULL
    end
) where parent_location_id is null;

update `_address_hierarchy` set location_id = (
    case name 
        when 'Другой' then 5 
        when 'Сугд' then 201 
        when 'Кургантюбинский регион' then 270 
        when 'Кулябский регион' then 271 
        when 'Душанбе' then 272 
        when 'НТМ' then 273 
        when 'Хатлон' then 274 
        when 'ВМКБ' then 276 
        else NULL
    end
) where location_id is null;


-- Update the remaining ones by matching parents
update `_address_hierarchy` ah join location l on l.name = ah.name and l.parent_location = ah.parent_location_id 
set ah.location_id = l.location_id where ah.location_id = 0;
-- Update the last batch
update `_address_hierarchy` ah join location l on l.name = ah.name 
set ah.location_id = l.location_id where ah.location_id = 0;


truncate report_data ;
-- Import the facilities
-- insert ignore into report_data (report_id, location_id, report_name, description, year, quarter, `month`, report_type, report_status, date_created, creator, voided, uuid, table_data) 
select rd.report_id, fa.location_id, rd.report_name, '' as description, rd.year, (case rd.quarter when '' then null else rd.quarter end) as `quarter`, if(rd.month, '', null) as `month`, rd.report_type, (case rd.report_status when 1 then 'LOCKED' else 'UNLOCKED' end) report_status, rd.report_date, 1, 0, uuid(), rd.table_data from openmrspih.report_data as rd 
inner join _address_hierarchy fa on fa.id = rd.facility_id 
union
select rd.report_id, da.location_id, rd.report_name, '' as description, rd.year, (case rd.quarter when '' then null else rd.quarter end) as `quarter`, if(rd.month, '', null) as `month`, rd.report_type, (case rd.report_status when 1 then 'LOCKED' else 'UNLOCKED' end) report_status, rd.report_date, 1, 0, uuid(), rd.table_data from openmrspih.report_data as rd 
inner join _address_hierarchy da on da.id = rd.district_id 
where rd.facility_id is null
union
select rd.report_id, oa.location_id, rd.report_name, '' as description, rd.year, (case rd.quarter when '' then null else rd.quarter end) as `quarter`, if(rd.month, '', null) as `month`, rd.report_type, (case rd.report_status when 1 then 'LOCKED' else 'UNLOCKED' end) report_status, rd.report_date, 1, 0, uuid(), rd.table_data from openmrspih.report_data as rd 
inner join _address_hierarchy oa on oa.id = rd.oblast_id 
where rd.facility_id is null and rd.district_id is null;

set foreign_key_checks = 1;



select o.obs_id, o.person_id, o.encounter_id, o.location_id, o.obs_datetime, o2.concept_id, cn2.name as abba, o.concept_id, cn.name as question, o.obs_group_id, o.value_coded, o.value_numeric, o.value_text from obs as o 
inner join concept_name cn on cn.concept_id = o.concept_id and cn.locale = 'en' and cn.concept_name_type = 'FULLY_SPECIFIED' 
inner join obs o2 on o2.obs_id = o.obs_group_id and o2.concept_id in (138, 139, 153, 524, 535, 542, 549, 552, 571, 572, 573) -- (Concepts For TB DST) 
inner join concept_name cn2 on cn2.concept_id = o2.concept_id and cn2.locale = 'en' and cn2.concept_name_type = 'FULLY_SPECIFIED' 
where o.voided = 0 and cn.voided = 0; -- (The question concept should be SUSCEPTIBLE and value coded be AMIKACIN)


# Get all lab results against a patient ID
select o.order_id, o.encounter_id, o.date_created, ct.test_order_id, ct.test_type_id, ca.attribute_type_id, cat.name as question, ca.value_reference, cn.name as answer from commonlabtest_test as ct 
inner join orders as o on o.order_id = ct.test_order_id 
inner join commonlabtest_attribute ca on ca.test_order_id = ct.test_order_id 
inner join commonlabtest_attribute_type cat on cat.test_attribute_type_id = ca.attribute_type_id 
left join concept c on c.uuid = ca.value_reference 
left join concept_name cn on cn.concept_id = c.concept_id and cn.locale = 'en' and cn.concept_name_type = 'FULLY_SPECIFIED'
where o.patient_id = (select pi2.patient_id from patient_identifier pi2 where pi2.identifier = '01220001' limit 1);

select et.name, e.encounter_datetime, e.date_created, e.location_id, o.obs_id, o.concept_id, get_concept_name(o.concept_id) as concept, o.encounter_id, o.obs_datetime, o.obs_group_id, o.value_coded, get_concept_name(o.value_coded) as answer, o.value_text from obs o 
inner join encounter e on e.encounter_id = o.encounter_id 
inner join encounter_type et on et.encounter_type_id = e.encounter_type 
where o.person_id in (select pi2.patient_id from patient_identifier pi2 where pi2.identifier = '01220021');

select e.*, o.concept_id, get_concept_name(o.concept_id) as q, o.value_numeric, o.value_coded, get_concept_name(o.value_coded) as a from encounter e 
inner join obs o on o.encounter_id = e.encounter_id 
where e.encounter_id = 866886 and e.voided = 0;

select cat.name, ca.* from commonlabtest_attribute ca 
inner join commonlabtest_attribute_type cat on cat.test_attribute_type_id = ca.attribute_type_id 
where ca.test_order_id in 
(select o.order_id from orders o where o.encounter_id = 679497) and ca.voided = 0;

select cn.* from openmrs19.concept c 
inner join openmrs19.concept_name cn on cn.concept_id = c.concept_id and cn.locale = 'en' and cn.concept_name_type = 'FULLY_SPECIFIED' 
where name like '%RESISTANCE' 
order by concept_name_id;

select ca.test_attribute_id, ca.test_order_id, o.encounter_id, et.name as encounter_type, ca.attribute_type_id, cat.group_name, cat.name, ca.value_reference, get_concept_name(c.concept_id) as answer from commonlabtest_test ct 
inner join commonlabtest_sample cs on cs.test_order_id = ct.test_order_id and cs.status IN ('ACCEPTED', 'PROCESSED') 
inner join commonlabtest_attribute ca on ca.test_order_id = ct.test_order_id 
inner join commonlabtest_attribute_type cat on cat.test_type_id = ct.test_type_id and cat.test_attribute_type_id = ca.attribute_type_id 
inner join orders o on o.order_id = ct.test_order_id 
inner join person p on p.person_id = o.patient_id 
inner join encounter e on e.encounter_id = o.encounter_id 
inner join encounter_type et on et.encounter_type_id = e.encounter_type 
left join concept c on c.uuid = ca.value_reference 
where p.person_id = 350489 
;

select * from patient_identifier pi2 where identifier = '20240305';
select * from patient_program pp where pp.patient_id = 2277;
select * from patient_state ps where ps.patient_program_id = 94667;
select * from person where uuid = 'a6c53369-432d-419b-a624-1243bc985c41';
select * from person_name where person_id = 349487;
select * from person_address where person_id >= 406084;
select * from orders where patient_id = 425715;
select * from commonlabtest_type;
select * from commonlabtest_attribute_type where uuid = '41fd3be8-c38a-f56a-93e0-651efa6eb8a8';
select * from commonlabtest_test where test_order_id = 786424;
select * from commonlabtest_sample where test_order_id = 720887;

select * from obs where encounter_id in (select encounter_id from encounter where person_id = 406090);
select * from encounter where patient_id = 406090;

delete from patient_program pp where pp.patient_id = 406088;
delete from patient_state ps where ps.patient_program_id = 94666;
delete ignore from obs where encounter_id in (select encounter_id from encounter where person_id = 406088);
delete ignore from encounter where patient_id = 406088;

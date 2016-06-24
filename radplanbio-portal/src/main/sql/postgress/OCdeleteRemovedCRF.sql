-- Delete all rule set audit for eCRFs with status removed
delete from rule_set_audit where rule_set_id in (
 select rule_set_id from rule_set where crf_id in (
  select crf_id from crf_version where status_id in (
   select status_id from status where name='removed'
  )
 )
);

-- Delete all rule set rule audit for eCRFs with status removed
delete from rule_set_rule_audit where rule_set_rule_id in (
 select rule_set_rule_id from rule_set_rule where rule_set_id in (
  select rule_set_id from rule_set where crf_id in (
   select crf_id from crf_version where status_id in (
    select status_id from status where name='removed'
   )
  )
 )
);

-- Delete all rule action for eCRFs with status removed
delete from rule_action where rule_set_rule_id in (
 select rule_set_rule_id from rule_set_rule where rule_set_id in (
  select rule_set_id from rule_set where crf_id in (
   select crf_id from crf_version where status_id in (
    select status_id from status where name='removed'
   )
  )
 )
);

-- Delete all rule set rule for eCRFs with status removed
delete from rule_set_rule where rule_set_id in (
 select rule_set_id from rule_set where crf_id in (
  select crf_id from crf_version where status_id in (
   select status_id from status where name='removed'
  )
 )
);

-- Delete all rule set for eCRFs with status removed
delete from rule_set where crf_id in (
 select crf_id from crf_version where status_id in (
  select status_id from status where name='removed'
 )
);

-- Delete all itemDefinition data for eCRFs with status removed
delete from item_data where item_id in (
 select item_id from item_form_metadata where crf_version_id in (
  select crf_version_id from crf_version where status_id in (
   select status_id from status where name='removed'
  )
 )
);

-- Delete all itemDefinition group metadata for eCRFs with status removed
delete from item_group_metadata where item_group_id in ( 
 select item_group_id from item_group where crf_id in (
  select crf_id from crf_version where status_id in (
   select status_id from status where name='removed'
  )
 )
);

-- Delete all itemDefinition group for eCRFs with status removed
delete from item_group where crf_id in (
 select crf_id from crf_version where status_id in (
  select status_id from status where name='removed'
 )
);

-- Delete all dn event crf map for eCRFs with status removed
delete from dn_event_crf_map where event_crf_id in (
 select event_crf_id from event_crf where crf_version_id in (
  select crf_version_id from crf_version where status_id in (
   select status_id from status where name='removed'
  )
 )
);

/***********************************************************************/

delete from item_form_metadata where crf_version_id in (
 select crf_version_id from crf_version where status_id in (
  select status_id from status where name='removed'
 )
);

delete from item_data where item_id not in (
 select item_id from item_form_metadata
);

/***********************************************************************/

delete from event_crf where crf_version_id in (
 select crf_version_id from crf_version where status_id in (
  select status_id from status where name='removed'
 )
);

delete from dataset_filter_map where dataset_id in (
 select dataset_id from dataset_crf_version_map where event_definition_crf_id in (
  select event_definition_crf_id from event_definition_crf where crf_id in (
   select crf_id from crf_version where status_id in (
    select status_id from status where name='removed'
   )
  )
 )
);

delete from dataset_study_group_class_map where dataset_id in (
 select dataset_id from dataset_crf_version_map where event_definition_crf_id in (
  select event_definition_crf_id from event_definition_crf where crf_id in (
   select crf_id from crf_version where status_id in (
    select status_id from status where name='removed'
   )
  )
 )
);

delete from dataset where dataset_id in (
 select dataset_id from dataset_crf_version_map where event_definition_crf_id in (
  select event_definition_crf_id from event_definition_crf where crf_id in (
   select crf_id from crf_version where status_id in (
    select status_id from status where name='removed'
   )
  )
 )
);

delete from dataset_crf_version_map where event_definition_crf_id in (
 select event_definition_crf_id from event_definition_crf where crf_id in (
  select crf_id from crf_version where status_id in (
   select status_id from status where name='removed'
  )
 )
);

delete from event_definition_crf where crf_id in (
 select crf_id from crf_version where status_id in (
  select status_id from status where name='removed'
 )
);

delete from event_definition_crf where crf_id in (
 select crf_id from crf where status_id in (
  select status_id from status where name='removed'
 )
);

delete from section where crf_version_id in (
 select crf_version_id from crf_version where status_id in (
  select status_id from status where name='removed'
 )
);

delete from item_form_metadata where crf_version_id in (
 select crf_version_id from crf_version where crf_id in (
  select crf_id from crf where status_id in (
   select status_id from status where name='removed'
  )
 )
);

delete from section where crf_version_id in (
 select crf_version_id from crf_version where crf_id in (
  select crf_id from crf where status_id in (
   select status_id from status where name='removed'
  )
 )
);

delete from versioning_map where crf_version_id in (
 select crf_version_id from crf_version where status_id in (
  select status_id from status where name='removed'
 )
);

delete from versioning_map where crf_version_id in (
 select crf_version_id from crf_version where crf_id in (
  select crf_id from crf where status_id in (
   select status_id from status where name='removed'
  )
 )
);

-- Delete itemDefinition group metadata for eCRFs with status removed
delete from item_group_metadata where item_group_id in (
 select item_group_id from item_group where crf_id in (
  select crf_id from crf where status_id in (
   select status_id from status where name='removed'
  )
 )
);

-- Delete itemDefinition group for eCRFs with status removed
delete from item_group where crf_id in (
 select crf_id from crf where status_id in (
  select status_id from status where name='removed'
 )
);

delete from crf_version where crf_Id in (
 select crf_id from crf_version where status_id in (
  select status_id from status where name='removed'
 )
);

delete from crf_version where crf_Id in (
 select crf_id from crf where status_id in (
  select status_id from status where name='removed'
 )
);

-- Delete eCRF with status removed
delete from crf where status_id in (
 select status_id from status where name='removed'
);
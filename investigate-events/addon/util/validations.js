import { lookup } from 'ember-dependency-lookup';

import { COLUMN_THRESHOLD } from 'investigate-events/constants/columnGroups';

export const hasUniqueName = (name, currentItemId, list) => {

  // we want to exclude comparison with item's original name
  return !(list || []).some((item) => {
    const nameExists = item.name === name?.trim();
    const matchedItself = item.id === currentItemId;
    return nameExists && !matchedItself;
  });
};

export const isColumnGroupValid = (editedGroup, columnGroups) => {

  let invalidReason;
  let isValid = false;

  const isNameError = !hasUniqueName(editedGroup?.name, editedGroup?.id, columnGroups);

  // there should be atleast one column besides the baseColumns 'time' and 'medium'
  const hasMinColumns = editedGroup?.columns?.length >= 3;

  // column group can have limited number of columns
  const withinColumnThreshold = editedGroup.columns.length <= COLUMN_THRESHOLD + 2;

  const i18n = lookup('service:i18n');
  if (!editedGroup?.name) {
    invalidReason = i18n.t('investigate.events.columnGroups.noColumnGroupName');
  } else if (isNameError) {
    invalidReason = i18n.t('investigate.events.columnGroups.nameNotUnique');
  } else if (!hasMinColumns) {
    invalidReason = i18n.t('investigate.events.columnGroups.noColumnsAdded');
  } else if (!withinColumnThreshold) {
    invalidReason = i18n.t('investigate.events.columnGroups.notWithinColumnThreshold', { COLUMN_THRESHOLD });
  } else {
    isValid = true;
  }

  return { isValid, invalidReason };
};

export const isProfileValid = (newProfile, profiles) => {

  let invalidReason;
  const isNameError = !hasUniqueName(newProfile?.name, newProfile?.id, profiles);

  const i18n = lookup('service:i18n');
  if (!newProfile) {
    return { isValid: false };
  } else if (!newProfile.name) {
    invalidReason = i18n.t('investigate.profile.noProfileName');
  } else if (isNameError) {
    invalidReason = i18n.t('investigate.profile.profileNameNotUnique');
  }

  const isValid = !isNameError && // no errors allowed
    // must have required fields
    !!newProfile.contentType &&
    !!newProfile.name &&
    !!newProfile.columnGroup &&
    !!newProfile.metaGroup &&
    // empty pre-query is ok
    (!!newProfile.preQuery || newProfile.preQuery === '');

  return { isValid, invalidReason };
};

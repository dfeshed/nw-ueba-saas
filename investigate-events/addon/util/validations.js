export const hasUniqueName = (name, currentItemId, list) => {

  // we want to exclude comparison with item's original name
  return !(list || []).some((item) => {
    const nameExists = item.name === name?.trim();
    const matchedItself = item.id === currentItemId;
    return nameExists && !matchedItself;
  });
};

export const isColumnGroupValid = (editedGroup, columnGroups) => {
  const isNameError = !hasUniqueName(editedGroup?.name, editedGroup?.id, columnGroups);

  // there should be atleast one column besides the baseColumns 'time' and 'medium'
  const hasColumns = editedGroup?.columns?.length >= 3;

  return editedGroup?.name && !isNameError && hasColumns;
};

export const isProfileValid = (newProfile, profiles) => {
  const isNameError = !hasUniqueName(newProfile?.name, newProfile?.id, profiles);
  if (!newProfile) {
    return false;
  }

  return !isNameError && // no errors allowed
    // must have required fields
    !!newProfile.name &&
    !!newProfile.columnGroup &&
    !!newProfile.metaGroup &&
    // empty pre-query is ok
    (!!newProfile.preQuery || newProfile.preQuery === '');
};

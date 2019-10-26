export const hasUniqueName = (name, currentItemId, list) => {

  // we want to exclude comparison with item's original name
  return !(list || []).some((item) => {
    const nameExists = item.name === name?.trim();
    const matchedItself = item.id === currentItemId;
    return nameExists && !matchedItself;
  });
};

export const isColumnGroupValid = (editedGroup, columnGroups) => {
  if (!editedGroup?.name) {
    return false;
  }

  const isNameError = !hasUniqueName(editedGroup?.name, editedGroup?.id, columnGroups);
  if (isNameError) {
    return false;
  }

  // there should be atleast one column besides the baseColumns 'time' and 'medium'
  const hasColumns = editedGroup?.columns?.length >= 3;
  return hasColumns;
};

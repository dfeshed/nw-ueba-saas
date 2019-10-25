export const hasUniqueName = (name, currentItemId, list) => {

  // we want to exclude comparison with item's original name
  return !(list || []).some((item) => {
    const nameExists = item.name === name?.trim();
    const matchedItself = item.id === currentItemId;
    return nameExists && !matchedItself;
  });
};


export function isFilterHasValues(filter) {
  let hasValues = false;
  if (filter && filter.value && filter.value.length && filter.value.length > 0) {
    hasValues = true;
  }
  return hasValues;
}

export function parseFilters(filters) {
  const expressionList = filters.map((filter) => {
    if (isFilterHasValues(filter)) {
      const { name, operator, value } = filter;
      const propertyValues = value.map((val) => ({ value: val }));
      return {
        restrictionType: operator,
        propertyValues,
        propertyName: name
      };
    }
  }).compact();

  return expressionList;
}


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
      const propertyValues = _createValueMap(name, value);
      return {
        restrictionType: operator,
        propertyValues,
        propertyName: name
      };
    }
  }).compact();

  return expressionList;
}

const _createValueMap = (name, value) => {
  const valueWithType = value.map((val) => {
    let valType = 'STRING';
    switch (name) {
      case 'publishStatus' :
        valType = 'STRING';
        break;
    }
    return { type: valType, value: val };
  });
  return valueWithType;
};

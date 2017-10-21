/**
 * Build the filter query
 *
 * @method buildSearchCriteria
 * @private
 * @param {object} selectedFilter selected system filter
 * @param {array} schemas all searchable schemas
 */
export const buildSearchCriteria = (selectedFilter, schemas, isSave) => {
  let criteria;

  if (selectedFilter.id.indexOf('custom') >= 0 || selectedFilter.systemFilter === false) {
    const expressionList = [];
    if (schemas.length >= 0) {
      for (const schema of schemas) {
        let restrictionType;
        // Ignore Date/Time while Save
        if (schema.dataType === 'DATE' && isSave) {
          continue;
        }
        if (schema.selectedValues.length <= 0 && schema.selectedValue === null) {
          if ((schema.startValue === null) || (schema.endValue === null)) {
            continue;
          }
        }

        let property = [];
        switch (schema.dataType) {
          case 'STRING': {
            const { operator: { selected } } = schema;
            if (selected.multiOption) {
              property = schema.selectedValues.map((value) => ({ value }));
            } else {
              property.push({ 'value': schema.selectedValue });
            }
            break;
          }
          case 'BOOLEAN': {
            property.push({ 'value': Boolean(schema.selectedValue) });
            break;
          }
          case 'INT':
          case 'INTEGER':
          case 'LONG':
          case 'DOUBLE': {
            if (schema.displayFormat === 'HEX') {
              if (schema.operator.selected.type === 'BETWEEN') {
                property.push({ 'value': parseInt(schema.startValue.toString(), 16) });
                property.push({ 'value': parseInt(schema.endValue.toString(), 16) });
              } else {
                property.push({ 'value': parseInt(schema.selectedValue.toString(), 16) });
              }
            } else {
              if (schema.operator.selected.type === 'BETWEEN') {
                property.push({ 'value': parseInt(schema.startValue, 10) });
                property.push({ 'value': parseInt(schema.endValue, 10) });
              } else {
                property.push({ 'value': parseInt(schema.selectedValue, 10) });
              }
            }
            break;
          }
          case 'FLOAT': {
            if (schema.operator.selected.type === 'BETWEEN') {
              property.push({ 'value': parseFloat(schema.startValue, 2) });
              property.push({ 'value': parseFloat(schema.endValue, 2) });
            } else {
              property.push({ 'value': parseFloat(schema.selectedValue, 2) });
            }
            break;
          }
          case 'DATE': {
            if (schema.name === 'agentStatus.lastSeenTime') {
              property.push({ 'value': schema.selectedValue });
              restrictionType = 'LESS_THAN';
            } else {
              if (schema.hasCustomDate) {
                property.push({ 'value': new Date(schema.startValue).toISOString() });
                property.push({ 'value': new Date(schema.endValue).toISOString() });
                restrictionType = 'BETWEEN';
              } else {
                property.push({ 'value': new Date(schema.selectedValue).toISOString() });
              }
            }
            break;
          }
          default: {
            break;
          }
        }
        const { operator: { selected } } = schema;
        restrictionType = !restrictionType && selected ? selected.type : restrictionType;

        const expression = {
          'propertyName': schema.name,
          'restrictionType': restrictionType || 'EQUAL',
          'propertyValues': property
        };
        expressionList.push(expression);
      }
      if (expressionList.length !== 0) {
        criteria = { expressionList, 'predicateType': 'AND' };
      }
    }
  } else {
    criteria = selectedFilter.criteria;
  }
  return criteria;
};
/**
 * Adds the appropriate date value to the property value
 * @param {*} propertyValues
 * @public
 */

export const prepareExpressionProperty = (propertyValues) => {
  return propertyValues.map((property) => {
    let d = new Date();
    if (property.value === 'LAST_ONE_HOUR') {
      d.setHours(d.getHours() - 1);
    } else if (property.value === 'LAST_TWENTY_FOUR_HOURS') {
      d.setHours(d.getHours() - 24);
    } else if (property.value === 'LAST_FIVE_DAYS') {
      d.setDate(d.getDate() - 5);
    } else {
      d = new Date(property.value);
    }
    property.value = d.valueOf();
    // property.value = moment.utc(d).format('x');
    return property;
  });
};
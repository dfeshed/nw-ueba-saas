import { get } from '@ember/object';
import { isArray } from '@ember/array';

export const filterData = (data, expressionList = []) => {
  if (!expressionList.length) {
    return data;
  }
  let newData = data;
  expressionList.forEach(({ restrictionType, propertyValues, propertyName }) => {
    const values = propertyValues.mapBy('value');
    if (restrictionType === 'IN') {
      newData = newData.filter((data) => {
        const value = get(data, propertyName);
        if (isArray(value)) {
          for (let i = 0; i < value.length; i++) {
            if (values.includes(value[i])) {
              return true;
            }
          }
          return false;
        } else {
          return values.includes(value);
        }
      });
    }
    if (restrictionType === 'BETWEEN' && values.length === 2) {
      newData = newData.filter((data) => {
        const value = get(data, propertyName);
        return values[0] <= value && value <= values[1];
      });
    }
    if (restrictionType === 'LIKE') {
      newData = newData.filter((data) => {
        const value = get(data, propertyName);
        return value.includes(values[0]);
      });
    }
  });
  return newData;
};

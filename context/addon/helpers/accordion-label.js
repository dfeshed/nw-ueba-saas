import { helper } from 'ember-helper';

export function accordionLabel([data, columns]) {
  // return an array of values, one per column
  return columns.map(({ field1, field2 }) => {
    // try this column's field2 first; if empty, try field1
    return data[field2] || data[field1];
  });

}
export default helper(accordionLabel);


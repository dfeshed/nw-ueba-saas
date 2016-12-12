import Ember from 'ember';
const { Helper: { helper } } = Ember;
export function accordionLabel([data, columns]) {
  const value = [];
  columns.forEach((obj) => {
    value.push(data[obj.field]);
  });
  return value;
}
export default helper(accordionLabel);


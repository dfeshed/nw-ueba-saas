import Ember from 'ember';
const { Helper: { helper } } = Ember;
export function accordionLabel([list, data]) {
  const value = [];
  list.forEach((obj) => {
    value.push(data[obj.field]);
  });
  return value;
}
export default helper(accordionLabel);


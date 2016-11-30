import Ember from 'ember';
const { Helper: { helper } } = Ember;
export function accordianLabel(params) {
  const attribute = params.pop();
  const data = params.pop();
  const value = [];
  attribute.forEach(function(obj) {
    value.push(data[obj.field]);
  });
  return value;
}
export default helper(accordianLabel);


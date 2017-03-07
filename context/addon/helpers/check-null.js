import Ember from 'ember';
const {
  Helper: {
    helper
  },
  isEmpty
} = Ember;
export function checkNull([text, altText]) {
  return isEmpty(text) ? altText : text;
}
export default helper(checkNull);

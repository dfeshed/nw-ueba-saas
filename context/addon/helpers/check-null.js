import { helper } from 'ember-helper';
import { isEmpty } from 'ember-utils';

export function checkNull([text, altText]) {
  return isEmpty(text) ? altText : text;
}
export default helper(checkNull);

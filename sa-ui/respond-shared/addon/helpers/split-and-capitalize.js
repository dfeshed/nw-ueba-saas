import { helper } from '@ember/component/helper';
import { underscore, capitalize } from '@ember/string';

export function splitAndCapitalize(str) {
  return underscore(str).split('_')
    .map(capitalize)
    .join(' ');
}

export default helper(function([str]) {
  return splitAndCapitalize(str);
});

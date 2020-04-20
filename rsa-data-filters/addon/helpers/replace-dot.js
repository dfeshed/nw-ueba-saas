import { helper } from '@ember/component/helper';

export function replaceDot([name]) {
  // Replace . with -
  return name ? name.replace(/\./g, '-') : name;
}

export default helper(replaceDot);

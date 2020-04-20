import Helper from '@ember/component/helper';

export function toUpperCase([ str ]) {
  return String(str).toUpperCase();
}

export default Helper.helper(toUpperCase);

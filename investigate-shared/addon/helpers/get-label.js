import Helper from '@ember/component/helper';

export function getLabel([prefix, field]) {
  return `${prefix}.${field}`;
}

export default Helper.helper(getLabel);
import Ember from 'ember';
const { Helper } = Ember;

export function toUpperCase([ str ]) {
  return String(str).toUpperCase();
}

export default Helper.helper(toUpperCase);

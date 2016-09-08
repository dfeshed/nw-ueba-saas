import Ember from 'ember';

const {
  get,
  isArray,
  Helper
} = Ember;

export function metaKeyAlias([ metaName, language ]) {
  let key = isArray(language) && language.findBy('metaName', metaName);
  let displayName = key && get(key, 'displayName');
  return {
    metaName,
    displayName: displayName || metaName,
    bothNames: displayName ? `${displayName} [${metaName}]` : metaName
  };
}

export default Helper.helper(metaKeyAlias);

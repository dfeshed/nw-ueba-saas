import { get } from '@ember/object';
import { isArray } from '@ember/array';
import Helper from '@ember/component/helper';

export function metaKeyAlias([ metaName, language ]) {
  const key = isArray(language) && language.findBy('metaName', metaName);
  const displayName = key && get(key, 'displayName');
  return {
    metaName,
    displayName: displayName || metaName,
    bothNames: displayName ? `${displayName} [${metaName}]` : metaName
  };
}

export default Helper.helper(metaKeyAlias);

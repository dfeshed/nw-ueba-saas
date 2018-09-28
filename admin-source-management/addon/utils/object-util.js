import _ from 'lodash';

/**
 * Flattens nested object structure into one top level object.
 * Note that arrays & null are objects in JS, but this method ignores that and keeps them as they are.
 * @param {*} deepObject
 * @public
 */
export function flattenObject(deepObject, flatObject = {}) {
  for (const prop in deepObject) {
    // keep non objects (including Arrays & null) as they are
    if (typeof deepObject[prop] !== 'object' || Array.isArray(deepObject[prop]) || deepObject[prop] === null) {
      flatObject[prop] = _.cloneDeep(deepObject[prop]);
    } else {
      flattenObject(deepObject[prop], flatObject);
    }
  }
  return flatObject;
}

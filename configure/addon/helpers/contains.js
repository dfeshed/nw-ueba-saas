import Ember from 'ember';

const { assert, Helper, isEqual, isNone, isPresent, typeOf } = Ember;

/**
 * HTMLBars helper that takes a value and collection. The "collection" can be an array or an object with keys/values.
 * The helper will return true if the collection contains the supplied value (in the case of an object, when the object
 * has a key equal to the supplied value).
 * @param value
 * @param collection
 * @returns {boolean}
 * @public
 */
export function contains(value, collection) {
  const collectionType = typeOf(collection);
  let doesContain = false;

  assert(
    'You must provide both a value and a collection to the contains helper',
    !isNone(value) && !isNone(collection)
  );
  assert(
    'The second parameter to "contains" must be an object or an array',
    isEqual(collectionType, 'array') || isEqual(collectionType, 'object')
  );

  if (
    (isEqual(collectionType, 'array') && collection.indexOf(value) > -1) ||
    (isEqual(collectionType, 'object') && collection.hasOwnProperty(value) && isPresent(collection[value]))) {
    doesContain = true;
  }

  return doesContain;
}

export default Helper.helper(function([value, collection]) {
  return contains(value, collection);
});

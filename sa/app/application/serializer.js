/**
 * @file Application Serializer
 * Formats the data sent to and back from backend.
 * @public
 */
import Ember from 'ember';
import RESTSerializer from 'ember-data/serializers/rest';

const { isArray } = Ember;

// Converts a single POJO from a typical RSA record structure (e.g., `{id, foo, bar, ..}`) to a JSON API
// document's record structure (e.g., `{id, type, attributes: {foo, bar, ..}}`).
function normalizeHash(modelName, hash) {
  let { id } = hash;
  delete hash.id;
  return {
    id,
    attributes: hash,
    type: modelName
  };
}

export default RESTSerializer.extend({

  /**
   * Transforms a back-end response JSON to a standard JSON-API response format.
   * (A JSON API response structure can then be understood by the Ember Data Store and turned into instances of models.)
   * *
   * Ember Data's store expects the data to be in JSON-API format though we are using DS.RESTAdapter.
   * Our server responses aren't JSON API documents. They are typically structure liked `{code: number, data: object|array}`.
   * The `response.data` is either an array of POJOs if request is for multiple records (e.g., `query` or `findAll`); or
   * a single POJO otherwise.
   * *
   * When Ember Data receives a response JSON from the application adapter, it passes that response JSON to
   * `normalizeResponse` (as the `payload` argument) so that `normalizeResponse` will transform the JSON into a standard
   * JSON API format that Ember Data understand.  This means two things:
   * (1) each POJO in `response.data` needs to be converted from `{id:.., foo:.., bar:.., ..}` to
   * `{id:.., type:.., attributes: {foo:.., bar:.., ..}}`; and
   * (2) the root of the JSON should have a `data` attr with the POJO(s) in it -- but that's always true with our RSA
   * responses, so we're good there.
   *
   * @see http://emberjs.com/api/data/classes/DS.RESTSerializer.html#method_normalizeResponse
   * @public
   */
  normalizeResponse(store, primaryModelClass, payload/* , id, requestType */) {
    let { modelName } = primaryModelClass;
    let isArrayB = isArray(payload.data);
    let dataArray = isArrayB ? payload.data : [payload.data];

    let normalizedArray = dataArray.map((datum) => {
      return normalizeHash(modelName, datum);
    });

    payload.data = isArrayB ? normalizedArray : normalizedArray[0];
    return payload;
  },

  /**
   * Transforms a single POJO, representing a single record of a given type, into a JSON API document.
   *
   * Why would you need to transform a single record into an entire JSON API document?
   * Typically it is done to prepare a POJO before pushing it in to the Ember Data store.  Ember Data store wants
   * pushed data to be a JSON API document, not some unstructured POJO.
   *
   * Note that calling `store.push(myPojo)` will not work; `myPojo` must first be transformed into a JSON API document.
   * Alternatively, calling `store.pushPayload(myPojo)` does work, but only because it will automatically call this
   * `normalize()` method on `myPojo` before pushing to the store.
   *
   * The standard JSON API document structure looks like `{data: object}` where the `data` object is a JSON API record
   * structure, like `{id, type, attributes: {..}`. Once we have a JSON API document structure like that, we can
   * push to the Ember Data store.
   *
   * @param typeClass
   * @param hash
   * @returns {{data: ({id, attributes, type}|*)}}
   * @public
   */
  normalize(typeClass, hash) {
    return {
      data: normalizeHash(typeClass.modelName, hash)
    };
  }
});

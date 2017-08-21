import { isEmberArray } from 'ember-array/utils';

/**
 * Takes an array of values and splits them up into multiple arrays based on the chunk size
 * @method chunk
 * @param array
 * @param chunkSize
 * @returns {Array} where each item in the array is an array of chunked data
 * @public
 */
const chunk = (array, chunkSize) => {
  if (!isEmberArray(array)) {
    array = [array];
  }
  const chunked = [];
  for (let i = 0; i < array.length; i += chunkSize) {
    chunked.push(array.slice(i, i + chunkSize));
  }
  return chunked;
};

export default chunk;
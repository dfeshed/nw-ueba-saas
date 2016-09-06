import Ember from 'ember';
const { Helper } = Ember;

export function intToHex([int], hash) {
  let size = parseInt(hash && hash.size, 10);
  size = (isNaN(size) || !size) ? 2 : size;
  // For performance, assume size <= 8. Later, support any size, if needed.
  return (`00000000${int.toString(16)}`).slice(-1 * size);
}

export default Helper.helper(intToHex);

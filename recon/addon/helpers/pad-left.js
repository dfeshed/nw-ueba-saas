import Helper from 'ember-helper';

export function padLeft([value, ...rest], hash) {
  let size = parseInt(hash && hash.size, 10);
  size = (isNaN(size) || !size) ? 2 : size;
  return (`00000000${value}`).slice(-1 * size);
}

export default Helper.helper(padLeft);

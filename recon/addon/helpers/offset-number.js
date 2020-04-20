import Helper from '@ember/component/helper';
import { BYTES_PER_ROW } from 'recon/reducers/packets/util';
const DIGITS = 6;
const numberHash = [];

const offsetNumber = function(index) {
  index = parseInt(index, 10);
  if (numberHash[index]) {
    return numberHash[index];
  }

  // number not in cache, lets create it
  const value = index * BYTES_PER_ROW;
  const output = (`00000000${value}`).slice(-1 * DIGITS);

  // cache number for later
  numberHash[index] = output;
  return output;
};

export default Helper.helper(offsetNumber);

import * as FLAGS from './nw-flags';

const isFlag = (value, flag) => {
  // JS numbers are 64-bit floating point, but bitwise operators only operate on
  // 32 of those bits. So if we want to check a flag larger than 2^32, we have
  // to do some math first.
  if (flag > Math.pow(2, 32)) {
    return (Math.floor(value / Math.pow(2, 32)) & Math.floor(flag / Math.pow(2, 32))) > 0;
  } else {
    return (value & flag) > 0;
  }
};

export {
  isFlag,
  FLAGS
};

import { isPresent } from '@ember/utils';

const exceedsLength = (name, maxLength) => {
  if (isPresent(name) && isPresent(maxLength)) {
    return (name.length >= maxLength);
  } else {
    return false;
  }
};

export {
  exceedsLength
};

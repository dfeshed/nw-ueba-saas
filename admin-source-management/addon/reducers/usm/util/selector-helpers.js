import { isPresent } from '@ember/utils';

const exceedsLength = (name, maxLength) => {
  if (isPresent(name) && isPresent(maxLength)) {
    return (name.length >= maxLength);
  } else {
    return false;
  }
};

const isNameInList = (list, id, name) => {
  if (list && name) {
    const lowerCaseName = name.toLocaleLowerCase();
    for (let l = 0; l < list.length; l++) {
      const listItem = list[l];
      if ((!id || (listItem.id !== id)) && (listItem.name.toLocaleLowerCase() === lowerCaseName)) {
        return true;
      }
    }
  }
  return false;
};

export {
  exceedsLength,
  isNameInList
};

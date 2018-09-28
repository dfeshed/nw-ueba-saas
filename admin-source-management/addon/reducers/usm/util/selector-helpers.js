import { isPresent } from '@ember/utils';
import _ from 'lodash';

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

/**
 * Takes a complete description and returns an object containing two truncated versions of the description
 * {
 *   truncated : 'Truncated to 256 characters without an omission indicator',
 *   truncatedWithEllipsis 'Truncated to 256 characters with an ellipsis as the omission indicator...'
 * }
 * @param {*} description
 * @public
 */
const descriptionsForDisplay = (description) => {
  return {
    truncated: description ? _.truncate(description, { length: 256, omission: '' }) : null,
    truncatedWithEllipsis: description ? _.truncate(description, { length: 256, omission: '...' }) : null
  };
};

export {
  exceedsLength,
  isNameInList,
  descriptionsForDisplay
};

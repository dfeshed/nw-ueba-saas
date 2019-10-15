import _ from 'lodash';

/**
 * Sort all meta items based on meta names
 * @param metaItems array of meta items i.e. [['email', 'test@gmail.com'], ['ip.src','1.1.1.1']]
 * @returns sorted meta items i.e. [['ip.src','1.1.1.1'], ['email', 'test@gmail.com']]
 * @private
 */
const _sortedMetaItems = (metaItems) => {
  const modifiedMetaItems = _.cloneDeep(metaItems);
  return modifiedMetaItems.sort((a, b) => {
    if (a[0] < b[0]) {
      return -1;
    } else if (a[0] > b[0]) {
      return 1;
    }
    return 0;
  });
};

/**
 * Group all meta items based on alphabets
 * @param metaItems array of meta items i.e. [['email', 'test@gmail.com'], ['ip.src','1.1.1.1']]
 * @returns grouped meta items [{group: E, children: [['email', 'test@gmail.com']]}, {group: I, children: [['ip.src','1.1.1.1']]}]
 * @private
 */
const groupByAlphabets = (metaItems) => {

  const sortedMetaItmes = _sortedMetaItems(metaItems);

  const groupedMetaItems = sortedMetaItmes.reduce((r, e) => {
    // get first letter of name of current element
    const group = e[0].charAt(0).toUpperCase();

    // if there is no property in accumulator with this letter create it
    if (!r[group]) {
      r[group] = { group, children: [e] };
    } else {
      // if there is push current element to children array for that letter
      r[group].children.push(e);
    }
    return r;
  }, {});

  // since data at this point is an object, to get array of values
  // we use Object.values method
  return Object.values(groupedMetaItems);
};

export {
  _sortedMetaItems,
  groupByAlphabets
};

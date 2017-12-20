import _ from 'lodash';

export const getValues = (selectedTab, tabName, data, sortConfig) => {
  if (data) {
    let values = _.values(data);
    const tab = tabName.toLowerCase(); // AUTORUNS -> autoruns
    if (sortConfig && sortConfig[tab]) {
      const config = sortConfig[tab];
      values = values.sortBy(config.field);
      if (config.isDescending) {
        values.reverse();
      }
    }
    if (selectedTab && selectedTab.tabName === tabName) {
      return values.filter((val) => (selectedTab.checksum === val.checksumSha256));
    } else {
      return values;
    }
  }
  return [];
};

export const getProperties = (rowId, list, data) => {
  if (rowId) {
    return data[rowId];
  } else if (list) {
    return list[0];
  }
};
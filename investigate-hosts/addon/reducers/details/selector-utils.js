import _ from 'lodash';

export const getValues = (selectedTab, tabName, data) => {
  if (data) {
    const values = _.values(data);
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
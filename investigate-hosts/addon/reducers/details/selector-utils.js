import _ from 'lodash';

export const getValues = (selectedTab, tabName, data, sortConfig) => {
  if (data) {
    let values = _.values(data).sortBy('fileName'); // default sorting
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

/*
  gets the default or selected row related properties based on the data set passed.
*/
export const getProperties = (rowId, list, data) => {
  const isDataAnArray = Array.isArray(data);

  if (rowId) {
    if (isDataAnArray) {
      const filteredRow = data.filter((item) => item.id === rowId);
      return filteredRow[0];
    }
    return data[rowId];
  } else if (list) {
    if (isDataAnArray) {
      return data[0];
    }
    return list[0];
  }
};
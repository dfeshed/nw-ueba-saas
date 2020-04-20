export const removeTwinIdFromPreQueryPillsData = (pillsDataArray) => {
  pillsDataArray?.forEach((item) => {
    if (item?.hasOwnProperty('twinId')) {
      delete item.twinId;
    }
  });
  return pillsDataArray;
};

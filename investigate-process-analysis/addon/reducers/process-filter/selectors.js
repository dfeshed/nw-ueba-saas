import reselect from 'reselect';

const { createSelector } = reselect;

const _selectedFilterItems = (state) => state.processAnalysis.processFilter.filter;

/* Formats the selected filters from state into an array of queries to filter the events table.*/
/* Expected format
[ { value: "action = 'createProcess' || action = 'openProcess'" },
{ value: "category = 'network' || category = 'file'" } ] */

export const constructFilterQueryString = createSelector(
  [_selectedFilterItems],
  (selectedFilterItems) => {
    const queryStringsForMeta = [];
    const filterTypes = Object.keys(selectedFilterItems);

    for (let i = 0; i < filterTypes.length; i++) {
      const filter = filterTypes[i];

      if (selectedFilterItems[filter].length > 0) {

        let queryString = '';
        let orOperator = '';
        const selectedFilterClone = [...selectedFilterItems[filter]];

        for (let j = 0; j < selectedFilterClone.length; j++) {
          orOperator = (j === (selectedFilterClone.length - 1)) ? '' : '||';
          queryString = `${queryString}${filter}='${selectedFilterClone[j]}'${orOperator}`;
        }
        queryStringsForMeta.push({ value: `(${queryString})` });
      }
    }
    return queryStringsForMeta;
  }
);

export const selectedFilterItemsArray = createSelector(
  [_selectedFilterItems],
  (selectedFilterItems) => {
    let filterItemArray = [];
    const filterTypes = Object.keys(selectedFilterItems);
    for (let i = 0; i < filterTypes.length; i++) {
      filterItemArray = [...filterItemArray, ...selectedFilterItems[filterTypes[i]]];
    }
    return filterItemArray;
  }
);
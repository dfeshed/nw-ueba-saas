export default {
  // the known list of items
  items: [],

  // either 'wait', 'error' or 'completed'
  itemsStatus: null,

  // subset of `items` selected by the user
  itemsSelected: [],

  // true when user toggles "More Filters" to reveal filter panel
  isFilterPanelOpen: true,

  isSelectAll: false,

  // total number of matching known items
  itemsTotal: null,

  // map of filters applied to the list of items
  itemsFilters: null,

  // the item currently with focus (i.e., highlighted and/or shown in the inspector) in the item list
  focusedItem: null,

  // whether or not there is a transaction (e.g., update, fetch) underway
  isTransactionUnderway: false,

  hasCustomDateRestriction: false,

  sortField: 'created',

  isSortDescending: true,

  defaultDateFilterField: 'created',

  defaultDateRangeTypeName: 'ALL_TIME'
};

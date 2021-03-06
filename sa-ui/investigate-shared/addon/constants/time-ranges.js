const DEFAULT_TIME_RANGE_ID = 'LAST_24_HOURS';
const CUSTOM_TIME_RANGE_ID = 'CUSTOM';
const ALL_DATA = 'ALL_DATA';
const DATABASE_TIME = 'DB';
const RANGES = [
  { id: 'LAST_5_MINUTES', name: 'Last 5 Minutes', value: 5, unit: 'minutes' },
  { id: 'LAST_10_MINUTES', name: 'Last 10 Minutes', value: 10, unit: 'minutes' },
  { id: 'LAST_15_MINUTES', name: 'Last 15 Minutes', value: 15, unit: 'minutes' },
  { id: 'LAST_30_MINUTES', name: 'Last 30 Minutes', value: 30, unit: 'minutes' },
  { id: 'LAST_HOUR', name: 'Last 1 Hour', value: 1, unit: 'hours' },
  { id: 'LAST_3_HOURS', name: 'Last 3 Hours', value: 3, unit: 'hours' },
  { id: 'LAST_6_HOURS', name: 'Last 6 Hours', value: 6, unit: 'hours' },
  { id: 'LAST_12_HOURS', name: 'Last 12 Hours', value: 12, unit: 'hours' },
  { id: 'LAST_24_HOURS', name: 'Last 24 Hours', value: 1, unit: 'days' },
  { id: 'LAST_2_DAYS', name: 'Last 2 Days', value: 2, unit: 'days' },
  { id: 'LAST_5_DAYS', name: 'Last 5 Days', value: 5, unit: 'days' },
  { id: 'LAST_7_DAYS', name: 'Last 7 Days', value: 7, unit: 'days' },
  { id: 'LAST_14_DAYS', name: 'Last 14 Days', value: 14, unit: 'days' },
  { id: 'LAST_30_DAYS', name: 'Last 30 Days', value: 1, unit: 'months' },
  { id: 'ALL_DATA', name: 'All Data', value: 0, unit: 'all' },
  { id: 'CUSTOM', name: 'Custom', value: 0, unit: 'custom', hidden: true }
];

const getById = (id) => RANGES.find((d) => d.id === id);

const getNameById = (id) => {
  const range = getById(id);
  return range ? range.name : null;
};

export default {
  DATABASE_TIME,
  DEFAULT_TIME_RANGE_ID,
  CUSTOM_TIME_RANGE_ID,
  ALL_DATA,
  RANGES,
  getById,
  getNameById
};
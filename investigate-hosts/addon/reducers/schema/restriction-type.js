const NUMBER_OPTIONS = [
  { name: 'EQUALS', type: 'EQUAL', isDefault: true },
  { name: 'GT', type: 'GREATER_THAN' },
  { name: 'LT', type: 'LESS_THAN' },
  { name: 'GTE', type: 'GREATER_THAN_OR_EQUAL_TO' },
  { name: 'LTE', type: 'LESS_THAN_OR_EQUAL_TO' },
  { name: 'NOT_EQ', type: 'NOT_EQUAL' },
  { name: 'BETWEEN', type: 'BETWEEN' }
];

export const TYPES = [
  {
    dataType: 'STRING',
    options: [
      { name: 'EQUALS', type: 'IN', multiOption: true, isDefault: true },
      { name: 'CONTAINS', type: 'LIKE' }
    ]
  },
  {
    dataType: 'INT',
    options: NUMBER_OPTIONS
  },
  {
    dataType: 'INTEGER',
    options: NUMBER_OPTIONS
  },
  {
    dataType: 'FLOAT',
    options: NUMBER_OPTIONS
  },
  {
    dataType: 'LONG',
    options: NUMBER_OPTIONS
  },
  {
    dataType: 'DOUBLE',
    options: NUMBER_OPTIONS
  },
  {
    dataType: 'DATE',
    options: [
      { name: 'ALL_TIME', unit: 'none', subtract: 0, type: 'BETWEEN', isDefault: true },
      { name: 'LAST_HOUR', unit: 'minutes', subtract: 60, type: 'GREATER_THAN' },
      { name: 'LAST_TWENTY_FOUR_HOURS', unit: 'days', subtract: 1, type: 'GREATER_THAN' },
      { name: 'LAST_5_DAYS', unit: 'days', subtract: 5, type: 'GREATER_THAN' }
    ]
  },
  {
    dataType: 'DATE_TIME_AGO',
    options: [
      { name: 'ALL_TIME', unit: 'none', subtract: 0, type: 'LESS_THAN', isDefault: true },
      { name: 'LAST_HOUR', unit: 'minutes', subtract: 60, type: 'LESS_THAN' },
      { name: 'LAST_TWENTY_FOUR_HOURS', unit: 'days', subtract: 1, type: 'LESS_THAN' },
      { name: 'LAST_5_DAYS', unit: 'days', subtract: 5, type: 'LESS_THAN' }
    ]
  }
];

const RESTRICTION_TYPE = {};

TYPES.forEach((t) => {
  t.selected = t.options.find((t) => t.isDefault);
  RESTRICTION_TYPE[t.dataType] = t;
});

export {
  RESTRICTION_TYPE
};
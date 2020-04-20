const equals = '=';
const notEquals = '!=';
const begins = 'begins';
const ends = 'ends';
const contains = 'contains';
const regex = 'regex';
const isIn = 'in';
const isNotIn = 'nin';
const greaterThan = '>';
const greaterThanOrEquals = '>=';
const lessThan = '<';
const lessThanOrEquals = '<=';
const olderThan = '<';
const newerThan = '>';

export default {
  text: [equals, notEquals, begins, ends, contains, regex, isIn, isNotIn],
  number: [equals, notEquals, greaterThan, greaterThanOrEquals, lessThan, lessThanOrEquals],
  category: [equals, notEquals],
  date: [equals, notEquals, olderThan, newerThan]
};
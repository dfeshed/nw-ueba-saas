import { isEmpty } from '@ember/utils';

const eq = { displayName: '=', description: 'Equals', isExpensive: false, hasValue: true };
const notEq = { displayName: '!=', description: 'Does Not Equal', isExpensive: false, hasValue: true };
const lt = { displayName: '<', description: 'Less Than', isExpensive: false, hasValue: true };
const lte = { displayName: '<=', description: 'Less Than or Equal To', isExpensive: false, hasValue: true };
const gt = { displayName: '>', description: 'Greater Than', isExpensive: false, hasValue: true };
const gte = { displayName: '>=', description: 'Greater Than or Equal To', isExpensive: false, hasValue: true };
const exists = { displayName: 'exists', description: 'Exists', isExpensive: false, hasValue: false };
const notExists = { displayName: '!exists', description: 'Does Not Exist', isExpensive: false, hasValue: false };
const length = { displayName: 'length', description: 'Length', isExpensive: false, hasValue: true };
const begins = { displayName: 'begins', description: 'Begins', isExpensive: false, hasValue: true };
const contains = { displayName: 'contains', description: 'Contains', isExpensive: true, hasValue: true };
const ends = { displayName: 'ends', description: 'Ends', isExpensive: true, hasValue: true };
const regex = { displayName: 'regex', description: 'Regex', isExpensive: false, hasValue: true };

const makeOperatorExpensive = (obj) => ({ ...obj, isExpensive: true });

const equalities = [eq, notEq];
const comparators = [...equalities, lt, lte, gt, gte];
const expensiveEqualities = [
  makeOperatorExpensive(eq),
  makeOperatorExpensive(notEq)
];
const expensiveComparators = [
  ...expensiveEqualities,
  makeOperatorExpensive(lt),
  makeOperatorExpensive(lte),
  makeOperatorExpensive(gt),
  makeOperatorExpensive(gte)
];

const operatorsForMetaIndexedByKey = [exists, notExists, ...expensiveEqualities];
const operatorsForMetaIndexedByKeyWithTextFormat = [exists, notExists, ...expensiveEqualities, makeOperatorExpensive(begins), contains, ends];
const operatorsForMetaIndexedByKeyWithNumberFormat = [exists, notExists, ...expensiveComparators];
const operatorsForMetaIndexedByValue = [exists, notExists, eq, notEq ];
const operatorsForMetaIndexedByValueWithTextFormat = [exists, notExists, ...equalities, begins, contains, ends, makeOperatorExpensive(length), makeOperatorExpensive(regex)];
const operatorsForMetaIndexedByValueWithNumberFormat = [exists, notExists, ...comparators];
const operatorsForSessionId = [exists, notExists, eq, notEq];
const defaultOperators = [...comparators, exists, notExists, begins, contains, ends];

const NONE = 'none';
const KEY = 'key';
const VALUE = 'value';
const indices = [NONE, KEY, VALUE];

const relevantOperators = (meta) => {
  let options = [];
  if (!isEmpty(meta)) {
    const { format, flags = 1, metaName } = meta;
    const index = (flags & '0xF') - 1;
    const indexedBy = indices[index];
    if (indexedBy === KEY) {
      switch (format) {
        case 'Text':
          options = operatorsForMetaIndexedByKeyWithTextFormat;
          break;
        case 'UInt8':
        case 'UInt16':
        case 'UInt32':
        case 'UInt64':
        case 'UInt128':
        case 'Int8':
        case 'Int16':
        case 'Int32':
        case 'Int64':
        case 'Float32':
        case 'Float64':
          options = operatorsForMetaIndexedByKeyWithNumberFormat;
          break;
        default:
          options = operatorsForMetaIndexedByKey;
      }
    } else if (indexedBy === VALUE) {
      switch (format) {
        case 'Text':
          options = operatorsForMetaIndexedByValueWithTextFormat;
          break;
        case 'UInt8':
        case 'UInt16':
        case 'UInt32':
        case 'UInt64':
        case 'UInt128':
        case 'Int8':
        case 'Int16':
        case 'Int32':
        case 'Int64':
        case 'Float32':
        case 'Float64':
          options = operatorsForMetaIndexedByValueWithNumberFormat;
          break;
        default:
          options = operatorsForMetaIndexedByValue;
      }
    } else if (metaName === 'sessionid') {
      // sessionid is a special case in the sense that it is the only
      // non-indexed key that has these 4 options because it's a primary key.
      options = operatorsForSessionId;
    } else {
      options = defaultOperators;
    }
  }
  return options;
};

export {
  relevantOperators
};
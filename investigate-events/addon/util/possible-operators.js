import { isEmpty } from '@ember/utils';

const eq = { displayName: '=', description: 'Equals', isExpensive: false, hasValue: true };
const notEq = { displayName: '!=', description: 'Does Not Equal', isExpensive: false, hasValue: true };
const exists = { displayName: 'exists', description: 'Exists', isExpensive: false, hasValue: false };
const notExists = { displayName: '!exists', description: 'Does Not Exist', isExpensive: false, hasValue: false };
const begins = { displayName: 'begins', description: 'Begins', isExpensive: false, hasValue: true };
const contains = { displayName: 'contains', description: 'Contains', isExpensive: true, hasValue: true };
const ends = { displayName: 'ends', description: 'Ends', isExpensive: true, hasValue: true };

const makeOperatorExpensive = (obj) => ({ ...obj, isExpensive: true });

const operatorsForMetaIndexedByKey = [exists, notExists, makeOperatorExpensive(eq), makeOperatorExpensive(notEq)];
const operatorsForMetaIndexedByKeyWithTextFormat = [exists, notExists, makeOperatorExpensive(eq), makeOperatorExpensive(notEq), makeOperatorExpensive(begins), ends, contains];
const operatorsForMetaIndexedByValue = [exists, notExists, eq, notEq ];
const operatorsForMetaIndexedByValueWithTextFormat = [exists, notExists, eq, notEq, begins, ends, contains];
const operatorsForSessionId = [exists, notExists, eq, notEq];
const defaultOperators = [eq, notEq, exists, notExists, contains, begins, ends];

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
      options = (format === 'Text') ?
        operatorsForMetaIndexedByKeyWithTextFormat :
        operatorsForMetaIndexedByKey;
    } else if (indexedBy === VALUE) {
      options = (format === 'Text') ?
        operatorsForMetaIndexedByValueWithTextFormat :
        operatorsForMetaIndexedByValue;
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
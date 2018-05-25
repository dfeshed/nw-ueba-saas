const eq = { displayName: '=', isExpensive: false, hasValue: true };
const notEq = { displayName: '!=', isExpensive: false, hasValue: true };
const exists = { displayName: 'exists', isExpensive: false, hasValue: false };
const notExists = { displayName: '!exists', isExpensive: false, hasValue: false };
const begins = { displayName: 'begins', isExpensive: false, hasValue: true };
const contains = { displayName: 'contains', isExpensive: true, hasValue: true };
const ends = { displayName: 'ends', isExpensive: true, hasValue: true };

const all = [eq, notEq, exists, notExists, begins, contains, ends];

export {
  eq,
  notEq,
  exists,
  notExists,
  begins,
  contains,
  ends,
  all
};
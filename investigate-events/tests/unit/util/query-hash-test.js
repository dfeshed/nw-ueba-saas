import { module, test } from 'qunit';
import { createQueryHash } from 'investigate-events/util/query-hash';
import {
  CLOSE_PAREN,
  COMPLEX_FILTER,
  OPEN_PAREN,
  OPERATOR_AND,
  OPERATOR_OR,
  QUERY_FILTER,
  TEXT_FILTER
} from 'investigate-events/constants/pill';

module('Unit | Util | query-hash');

test('creates proper query hash for all types of filters', function(assert) {
  const pills = [
    { type: QUERY_FILTER, meta: 'a', operator: 'b', value: 'c' },
    { type: COMPLEX_FILTER, complexFilterText: '(d)' },
    { type: TEXT_FILTER, searchTerm: 'e' }
  ];
  const hash = createQueryHash('service', 'sTime', 'eTime', pills);
  assert.equal(
    hash,
    'service-sTime-eTime-abc-(d)-e',
    'hash is created properly'
  );
});

test('creates proper query hash for values which are undefined', function(assert) {
  const pills = [
    { type: QUERY_FILTER, meta: 'a', operator: 'b', value: undefined }
  ];
  const hash = createQueryHash('service', 'sTime', 'eTime', pills);
  assert.equal(
    hash,
    'service-sTime-eTime-ab',
    'hash is created properly'
  );
});

test('creates proper query hash for filters and params', function(assert) {
  const pills = [
    { type: OPEN_PAREN },
    { type: QUERY_FILTER, meta: 'a', operator: 'b', value: 'c' },
    { type: CLOSE_PAREN }
  ];
  const hash = createQueryHash('service', 'sTime', 'eTime', pills);
  assert.equal(
    hash,
    'service-sTime-eTime-(-abc-)',
    'hash is created properly'
  );
});

test('creates proper query hash for operators', function(assert) {
  const pills = [
    { type: OPERATOR_AND },
    { type: OPERATOR_OR }
  ];
  const hash = createQueryHash('service', 'sTime', 'eTime', pills);
  assert.equal(hash, 'service-sTime-eTime-&-|', 'hash is created properly');
});
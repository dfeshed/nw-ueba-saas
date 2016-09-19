import { eventQueryUri } from 'sa/helpers/event-query-uri';
import { module, test } from 'qunit';

module('Unit | Helper | event query uri');

const serviceId = 'id1';
const startTime = 1;
const endTime = 2;
const drillKey = 'foo';
const drillValue = 'bar';
const drillKey2 = 'ip.src';
const drillValue2 = '1.2.3.4';

const query = {
  serviceId,
  startTime,
  endTime,
  metaFilter: {
    uri: '',
    conditions: []
  }
};

const drillQuery = {
  serviceId,
  startTime,
  endTime,
  metaFilter: {
    conditions: [{ key: drillKey, value: drillValue }]
  }
};

test('it works', function(assert) {
  let result = eventQueryUri([ query ]);
  assert.equal(
    result,
    [ serviceId, startTime, endTime, '' ].join('/'),
    'Expected URI string with service id, start & end time, and no drill condition in meta filter.'
  );

  result = eventQueryUri([ query, drillKey, drillValue ]);
  assert.equal(
    result,
    [ serviceId, startTime, endTime, `${drillKey}=${drillValue}` ].join('/'),
    'Expected URI string with service id, start & end time, and 1 drill condition in meta filter.'
  );

  result = eventQueryUri([ drillQuery, drillKey2, drillValue2 ]);
  assert.equal(
    result,
    [ serviceId, startTime, endTime, `${drillKey}=${drillValue}`, `${drillKey2}=${drillValue2}` ].join('/'),
    'Expected URI string with service id, start & end time, and 2 drill conditions in meta filter.'
  );
});

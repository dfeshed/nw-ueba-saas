import { eventQueryUri } from 'investigate-events/helpers/event-query-uri';
import { module, test } from 'qunit';

module('Unit | Helper | event query uri');

const serviceId = 'id1';
const startTime = 1;
const endTime = 2;
const drillKey = 'foo';
const drillValue = 'bar';
const drillKey2 = 'ip.src';
const drillValue2 = '1.2.3.4';
const complexQueryString = 'foo > 2 && bar contains "exe"';

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
    conditions: [{
      queryString: `${drillKey}=${drillValue}`,
      isKeyValuePair: true,
      key: drillKey,
      value: drillValue
    }]
  }
};

const complexDrillQuery = {
  serviceId,
  startTime,
  endTime,
  metaFilter: {
    conditions: [{
      queryString: complexQueryString
    }]
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

  result = eventQueryUri([ complexDrillQuery, drillKey2, drillValue2 ]);
  assert.equal(
    result,
    [ serviceId, startTime, endTime, encodeURIComponent(complexQueryString), `${drillKey2}=${drillValue2}` ].join('/'),
    'Expected URI string with service id, start & end time, a complex condition & a simple key-value pair condition.'
  );

});

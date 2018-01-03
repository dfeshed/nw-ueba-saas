import { eventQueryUri } from 'investigate-events/helpers/event-query-uri';
import { module, test } from 'qunit';

module('Unit | Helper | event query uri');

const serviceId = 'id1';
const startTime = 1;
const endTime = 2;

const drillMetaKey = 'ip.dest';
const drillOperator = '=';
const encodedDrillOperator = '%3D';
const drillValue = '1.2.3.3';

const drillMetaKey2 = 'ip.src';
const drillOperator2 = '>';
const encodedDrillOperator2 = '%3E';
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
    conditions: [{
      meta: drillMetaKey,
      operator: drillOperator,
      value: drillValue
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

  result = eventQueryUri([ query, drillMetaKey, drillOperator, drillValue ]);
  assert.equal(
    result,
    [ serviceId, startTime, endTime, `${drillMetaKey}${encodedDrillOperator}${drillValue}` ].join('/'),
    'Expected URI string with service id, start & end time, and 1 drill condition in meta filter.'
  );

  result = eventQueryUri([ drillQuery, drillMetaKey2, drillOperator2, drillValue2 ]);
  assert.equal(
    result,
    [ serviceId, startTime, endTime, `${drillMetaKey}${encodedDrillOperator}${drillValue}`, `${drillMetaKey2}${encodedDrillOperator2}${drillValue2}` ].join('/'),
    'Expected URI string with service id, start & end time, and 2 drill conditions in meta filter.'
  );

});

import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { servicesWithURI } from 'investigate-events/reducers/services/selectors';

module('Unit | Selectors | services');

const endTime = new Date() / 1000 | 0;
const startTime = endTime - 3600;
const services = {
  data: [
    { id: 'id1', displayName: 'Service Name', name: 'SN' },
    { id: 'id2', displayName: 'Service Name2', name: 'SN2' }
  ]
};
const queryNode = {
  serviceId: 'sd1',
  startTime,
  endTime,
  metaFilter: {
    conditions: [
      {
        queryString: 'foo=foo-value',
        isKeyValuePair: true,
        key: 'foo',
        value: 'foo-value'
      }
    ]
  }
};
const expectedServiceId = services.data[0].id;
const expectedURI = `${services.data[0].id}/${startTime}/${endTime}/`;

test('servicesWithURI are computed correctly', function(assert) {
  const state = Immutable.from({ queryNode, services });
  const result = servicesWithURI(state);
  assert.equal(result.length, 2);

  const [ firstServiceWithURI ] = result;
  assert.equal(firstServiceWithURI.id, expectedServiceId, 'has matching IDs');

  assert.equal(firstServiceWithURI.queryURI, expectedURI, 'has matching URIs');
});

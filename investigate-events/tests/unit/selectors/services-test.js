import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { servicesWithURI, hasSummaryData } from 'investigate-events/reducers/investigate/services/selectors';

module('Unit | Selectors | services');

const endTime = new Date() / 1000 | 0;
const startTime = endTime - 3600;
const state = Immutable.from({
  investigate: {
    services: {
      serviceData: [
        { id: 'id1', displayName: 'Service Name', name: 'SN' },
        { id: 'id2', displayName: 'Service Name2', name: 'SN2' }
      ],
      summaryData: {
        startTime: '1234',
        endTime: '6789'
      }
    },
    queryNode: {
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
    }
  }
});

const expectedServiceId = state.investigate.services.serviceData[0].id;
const expectedURI = `${state.investigate.services.serviceData[0].id}/${startTime}/${endTime}/`;

test('servicesWithURI are computed correctly', function(assert) {
  // const state = Immutable.from(defaultState);
  const result = servicesWithURI(state);
  assert.equal(result.length, 2);

  const [ firstServiceWithURI ] = result;
  assert.equal(firstServiceWithURI.id, expectedServiceId, 'has matching IDs');

  assert.equal(firstServiceWithURI.queryURI, expectedURI, 'has matching URIs');
});

test('hasSummaryData are computed correctly', function(assert) {
  assert.equal(hasSummaryData(state), true, 'The returned value from hasSummaryData selector is as expected');
});

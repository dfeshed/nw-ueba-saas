import { moduleForComponent, test } from 'ember-qunit';
import Query from 'investigate-events/state/query-definition';
import engineResolverFor from '../../../helpers/engine-resolver';

moduleForComponent('bread-crumb', 'Unit | Component | bread-crumb', {
  needs: [
    'service:timezone',
    'service:dateFormat',
    'service:timeFormat',
    'service:redux'
  ],
  unit: true,
  resolver: engineResolverFor('investigate-events')
});

const i18n = {
  t() {
    return undefined;
  }
};
const services = {
  data: [
    { id: 'id1', displayName: 'Service Name', name: 'SN' },
    { id: 'id2', displayName: 'Service Name2', name: 'SN2' }
  ],
  isLoading: false,
  isError: false
};
const [ firstService ] = services.data;
const { id: expectedId } = firstService;
const nowSeconds = parseInt(+(new Date()) / 1000, 10);
const metaName = 'foo';
const metaValue = 'foo-value';
const query = Query.create({
  i18n,
  serviceId: expectedId,
  startTime: nowSeconds,
  endTime: nowSeconds,
  metaFilter: {
    conditions: [
      {
        queryString: `${metaName}=${metaValue}`,
        isKeyValuePair: true,
        key: metaName,
        value: metaValue
      }
    ]
  }
});

test('servicesWithURI are computed correctly', function(assert) {
  const breadcrumb = this.subject();

  const servicesWithURIs = breadcrumb._addQueryUriToServices(services.data, query);
  assert.equal(servicesWithURIs.length, 2);

  const [ firstServiceWithURI ] = servicesWithURIs;
  assert.equal(firstServiceWithURI.id, expectedId, 'has matching IDs');

  const expectedURI = `${expectedId}/${nowSeconds}/${nowSeconds}/`;
  assert.equal(firstServiceWithURI.queryURI, expectedURI, 'has matching URIs');
});

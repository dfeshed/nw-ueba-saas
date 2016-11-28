import { moduleForComponent, test } from 'ember-qunit';
import Query from 'sa/protected/investigate/state/query-definition';

moduleForComponent('rsa-investigate/breadcrumb', 'Unit | Component | rsa investigate/breadcrumb', {
  needs: [
    'component:rsa-content-help-trigger',
    'component:rsa-content-tooltip',
    'component:rsa-form-button',
    'component:rsa-icon',
    'component:rsa-investigate/time-range'
  ],
  unit: true
});

test('servicesWithURI are computed correctly', function(assert) {
  const i18n = {
    t() {
      return undefined;
    }
  };

  const id = 'id1';
  const name = 'Service Name';

  const metaName1 = 'foo';
  const displayName1 = 'fooDisplayName';
  const metaValue1 = 'foo-value';
  const metaValueAlias1 = `${metaName1}=${metaValue1}`;
  const condition1 = {
    queryString: `${metaName1}=${metaValue1}`,
    isKeyValuePair: true,
    key: metaName1,
    value: metaValue1
  };

  const nowSeconds = parseInt(+(new Date()) / 1000, 10);

  const language = [
    { metaName: metaName1, displayName: displayName1 }
  ];
  const aliases = {};
  aliases[metaName1] = {};
  aliases[metaName1][metaValue1] = metaValueAlias1;

  const query = Query.create({
    i18n,
    serviceId: id,
    startTime: nowSeconds,
    endTime: nowSeconds,
    metaFilter: {
      conditions: [ condition1 ]
    }
  });

  const services = [{
    id,
    name
  }];

  const component = this.subject();
  component.setProperties({
    services,
    query,
    language,
    aliases
  });

  const expected = `${id}/${nowSeconds}/${nowSeconds}/`;
  assert.equal(component.get('servicesWithURI.0.queryURI'), expected);
});

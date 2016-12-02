import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import Query from 'sa/protected/investigate/state/query-definition';

moduleForComponent('rsa-investigate/breadcrumb', 'Integration | Component | rsa investigate/breadcrumb', {
  integration: true
});

test('it renders', function(assert) {
  const i18n = {
    t() {
      return undefined;
    }
  };
  const id = 'id1';
  const displayName = 'Service Name';
  const services = [{
    id,
    displayName
  }];
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

  const metaName2 = 'bar';
  const metaValue2 = 'bar-value';
  const metaValueAlias2 = `${metaName2} = ${metaValue2}`;
  const condition2 = {
    queryString: `${metaName2} = ${metaValue2}`,
    isKeyValuePair: true,
    key: metaName2,
    value: metaValue2
  };

  const language = [
    { metaName: metaName1, displayName: displayName1 },
    { metaName: metaName2 }
  ];
  const aliases = {};
  aliases[metaName1] = {};
  aliases[metaName1][metaValue1] = metaValueAlias1;

  const nowSeconds = parseInt(+(new Date()) / 1000, 10);
  const query = Query.create({
    i18n,
    serviceId: id,
    startTime: nowSeconds,
    endTime: nowSeconds,
    metaFilter: {
      conditions: [ condition1 ]
    }
  });

  this.setProperties({
    services,
    query,
    language,
    aliases
  });

  this.render(hbs`{{rsa-investigate/breadcrumb services=services query=query language=language aliases=aliases}}`);

  let $el = this.$('.rsa-investigate-breadcrumb');
  assert.equal($el.length, 1, 'Expected root DOM element.');
  assert.equal($el.find('.js-test-service').text().trim(), displayName, 'Expected service displayName in DOM to match service data.');

  $el = this.$('.js-test-value');
  assert.equal($el.text().trim(), metaValueAlias1, 'Expected to find meta key/value in DOM.');
  assert.equal($el.attr('title').trim(), `${displayName1} [${metaName1}]: ${metaValueAlias1} [${metaValue1}]`, 'Expected tooltip to include friendly and raw strings.');

  const query2 = query.clone();
  query2.set('metaFilter.conditions', [ condition2 ]);
  this.set('query', query2);

  $el = this.$('.js-test-value');
  assert.equal($el.text().trim(), metaValueAlias2, 'Expected to find raw value in DOM.');
  assert.equal($el.attr('title').trim(), `${metaName2}: ${metaValue2}`, 'Expected tooltip to have raw string.');

  const query3 = query.clone();
  query3.set('metaFilter.conditions', [ condition1, condition2 ]);
  this.set('query', query3);

  $el = this.$('.js-test-value');
  assert.equal($el.length, 2, 'Expected to find 2 conditions in DOM.');
});

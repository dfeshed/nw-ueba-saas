import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import Query from 'investigate-events/state/query-definition';
import engineResolverFor from '../../../helpers/engine-resolver';

moduleForComponent('b-breadcrumb', 'Integration | Component | b-breadcrumb', {
  integration: true,
  resolver: engineResolverFor('investigate-events')
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
  const metaDisplayName1 = 'foo-display-name';
  const metaValue1 = 'foo-value';
  const metaValueAlias1 = 'foo-value-alias';
  const condition1 = {
    queryString: `${metaName1}=${metaValue1}`,
    isKeyValuePair: true,
    key: metaName1,
    value: metaValue1
  };

  const condition2 = {
    queryString: 'a=b || c=d',
    isKeyValuePair: false
  };

  const language = [
    { metaName: metaName1, displayName: metaDisplayName1 }
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

  this.render(hbs`{{b-breadcrumb services=services query=query language=language aliases=aliases}}`);

  let $el = this.$('.rsa-investigate-breadcrumb');
  assert.equal($el.length, 1, 'Expected root DOM element.');
  assert.equal($el.find('.js-test-service').text().trim(), displayName, 'Expected service displayName in DOM to match service data.');

  $el = this.$('.js-test-value');
  assert.equal($el.text().trim(), `${metaDisplayName1} = ${metaValueAlias1}`, 'Expected to find aliased meta key + value in DOM.');
  assert.equal($el.attr('title').trim(), `${metaDisplayName1} [${metaName1}]: ${metaValueAlias1} [${metaValue1}]`, 'Expected panel to include friendly and raw strings.');

  const query2 = query.clone();
  query2.set('metaFilter.conditions', [ condition2 ]);
  this.set('query', query2);

  $el = this.$('.js-test-value');
  assert.equal($el.text().trim(), condition2.queryString, 'Expected to find raw queryString in DOM.');
  assert.equal($el.attr('title').trim(), condition2.queryString, 'Expected panel to have raw queryString.');

  const query3 = query.clone();
  query3.set('metaFilter.conditions', [ condition1, condition2 ]);
  this.set('query', query3);

  $el = this.$('.js-test-value');
  assert.equal($el.length, 2, 'Expected to find 2 conditions in DOM.');
});

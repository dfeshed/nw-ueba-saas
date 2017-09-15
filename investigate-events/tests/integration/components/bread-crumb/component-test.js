import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import Query from 'investigate-events/state/query-definition';
import engineResolverFor from '../../../helpers/engine-resolver';
import DataHelper, { getConcentratorService, getConcentratorServiceId } from '../../../helpers/data-helper';

moduleForComponent('bread-crumb', 'Integration | Component | bread-crumb', {
  integration: true,
  resolver: engineResolverFor('investigate-events'),
  beforeEach() {
    this.inject.service('redux');
  }
});

const i18n = { t: () => undefined };
const nowSeconds = parseInt(+(new Date()) / 1000, 10);
const metaName1 = 'foo';
const metaValue1 = 'foo-value';
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
const query = Query.create({
  i18n,
  serviceId: getConcentratorServiceId(),
  startTime: nowSeconds,
  endTime: nowSeconds,
  metaFilter: {
    conditions: [ condition1 ]
  }
});
const metaDisplayName1 = 'foo-display-name';
const metaValueAlias1 = 'foo-value-alias';
const language = [
  { metaName: metaName1, displayName: metaDisplayName1 }
];
const aliases = {};
aliases[metaName1] = {};
aliases[metaName1][metaValue1] = metaValueAlias1;

test('it renders', function(assert) {
  this.render(hbs`{{bread-crumb}}`);
  const $el = this.$('.rsa-investigate-breadcrumb');
  assert.equal($el.length, 1, 'Expected root DOM element.');
  assert.equal($el.text().trim(), 'More Meta (optional)');
});

test('service name displayed', function(assert) {
  new DataHelper(this.get('redux'))
    .initializeData();

  this.render(hbs`{{bread-crumb}}`);
  const $el = this.$('.rsa-investigate-breadcrumb');
  const { displayName } = getConcentratorService();
  assert.equal($el.find('.js-test-service').text().trim(), displayName, `Expected service name in DOM to match "${displayName}".`);
});

test('alias meta keys displayed', function(assert) {
  new DataHelper(this.get('redux'))
    .initializeData();

  this.setProperties({ query, language, aliases });

  this.render(hbs`{{bread-crumb query=query language=language aliases=aliases}}`);

  const $el = this.$('.js-test-value');
  assert.equal($el.text().trim(), `${metaDisplayName1} = ${metaValueAlias1}`, 'Expected to find aliased meta key + value in DOM.');
  assert.equal($el.attr('title').trim(), `${metaDisplayName1} [${metaName1}]: ${metaValueAlias1} [${metaValue1}]`, 'Expected panel to include friendly and raw strings.');
});

test('raw data in query string', function(assert) {
  new DataHelper(this.get('redux'))
    .initializeData();

  const _query = query.clone();
  _query.set('metaFilter.conditions', [ condition2 ]);

  this.setProperties({ query: _query, language, aliases });

  this.render(hbs`{{bread-crumb query=query language=language aliases=aliases}}`);

  const $el = this.$('.js-test-value');
  assert.equal($el.text().trim(), condition2.queryString, 'Expected to find raw queryString in DOM.');
  assert.equal($el.attr('title').trim(), condition2.queryString, 'Expected panel to have raw queryString.');
});

test('can set multiple query values', function(assert) {
  new DataHelper(this.get('redux'))
    .initializeData();

  const _query = query.clone();
  _query.set('metaFilter.conditions', [ condition1, condition2 ]);

  this.setProperties({ query: _query, language, aliases });

  this.render(hbs`{{bread-crumb query=query language=language aliases=aliases}}`);

  const $el = this.$('.js-test-value');
  assert.equal($el.length, 2, 'Expected to find 2 conditions in DOM.');
});

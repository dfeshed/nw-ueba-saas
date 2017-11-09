import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import DataHelper, { getConcentratorServiceId } from '../../../helpers/data-helper';
import wait from 'ember-test-helpers/wait';
import { initialize, triggerKeyUp } from 'ember-keyboard';

moduleForComponent('query-filters', 'Integration | Component | query-filters', {
  integration: true,
  resolver: engineResolverFor('investigate-events'),
  beforeEach() {
    this.inject.service('redux');
    initialize();
  }
});

const nowSeconds = parseInt(+(new Date()) / 1000, 10);
const condition1 = {
  key: 'foo',
  operator: '=',
  value: 'bar'
};

const condition2 = {
  key: 'foo',
  operator: '=',
  value: 'bar'
};

const condition3 = {
  key: 'foo',
  operator: '=',
  value: 'bar'
};

const queryA = {
  serviceId: getConcentratorServiceId(),
  startTime: nowSeconds,
  endTime: nowSeconds,
  conditions: null
};

const queryB = {
  serviceId: getConcentratorServiceId(),
  startTime: nowSeconds,
  endTime: nowSeconds,
  metaFilter: {
    conditions: [ condition1, condition2, condition3 ]
  }
};

test('it renders with no fragments by defauls', function(assert) {
  new DataHelper(this.get('redux'))
    .initializeData()
    .setQueryParamsForTests(queryA);

  this.render(hbs`{{query-filters}}`);

  assert.equal(this.$('.rsa-query-meta').length, 1, 'Expected 1 .rsa-query-meta');
  assert.equal(this.$('.rsa-query-meta .rsa-query-fragment').length, 1, 'Expected 1 .rsa-query-fragment');
  assert.equal(this.$('.rsa-query-meta .rsa-query-fragment.edit-active').length, 1, 'Expected 1 .rsa-query-fragment.edit-active');
  assert.equal(this.$('.rsa-query-meta .rsa-query-fragment.edit-active input').prop('placeholder'), 'Enter a Meta Key and Value (optional)', 'Expected a placeholder');
  assert.equal(this.$('.rsa-query-meta input:focus').length, 0, 'Expected .rsa-query-meta to not have focus');
});

test('it renders with proper class when queryable', function(assert) {
  new DataHelper(this.get('redux'))
    .initializeData()
    .setQueryParamsForTests(queryA);

  this.render(hbs`{{query-filters queryable=true}}`);

  assert.equal(this.$('.rsa-query-meta.queryable').length, 1, 'Expected 1 .rsa-query-meta');
});

test('focusing expands dropdown immediately', function(assert) {
  new DataHelper(this.get('redux'))
    .initializeData()
    .setQueryParamsForTests(queryA);

  this.render(hbs`{{query-filters}}`);

  this.$('.rsa-query-meta input').focus();

  return wait().then(() => {
    assert.equal(this.$('.rsa-query-meta .ember-power-select-trigger[aria-expanded="true"]').length, 1, 'Expected .ember-power-select-trigger[aria-expanded=true]');
  });
});

test('it renders fragments', function(assert) {
  new DataHelper(this.get('redux'))
    .initializeData()
    .setQueryParamsForTests(queryB);

  this.render(hbs`{{query-filters}}`);

  return wait().then(() => {
    assert.equal(this.$('.rsa-query-meta .rsa-query-fragment').length, 4, 'Expected 4 .rsa-query-fragment');
    assert.equal(this.$('.rsa-query-meta .rsa-query-fragment.edit-active').length, 1, 'Expected 1 .rsa-query-fragment.edit-active');
    assert.equal(this.$('.rsa-query-meta .rsa-query-fragment.edit-active input').prop('placeholder'), '', 'Expected no placeholder');
  });
});

test('it allows fragment editing via keyboard', function(assert) {
  new DataHelper(this.get('redux'))
    .initializeData()
    .setQueryParamsForTests(queryB);

  this.render(hbs`{{query-filters}}`);

  const $queryBuilder = this.$('.rsa-query-meta');
  const $fragment = this.$('.rsa-query-fragment').first();

  $fragment.find('.meta').click();
  assert.ok($fragment.hasClass('selected'));

  triggerKeyUp('Enter', $queryBuilder[0]);
  assert.ok($fragment.hasClass('edit-active'));
});

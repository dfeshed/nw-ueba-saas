import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import DataHelper, { getConcentratorService } from '../../../helpers/data-helper';

moduleForComponent('bread-crumb', 'Integration | Component | bread-crumb', {
  integration: true,
  resolver: engineResolverFor('investigate-events'),
  beforeEach() {
    this.inject.service('redux');
  }
});

test('it renders', function(assert) {
  this.render(hbs`{{bread-crumb}}`);
  const $el = this.$('.rsa-investigate-breadcrumb');
  assert.equal($el.length, 1, 'Expected root DOM element.');
});

test('service name displayed', function(assert) {
  new DataHelper(this.get('redux'))
    .initializeData();

  this.render(hbs`{{bread-crumb}}`);
  const $el = this.$('.rsa-investigate-breadcrumb');
  const { displayName } = getConcentratorService();
  assert.equal($el.find('.js-test-service').text().trim(), displayName, `Expected service name in DOM to match "${displayName}".`);
});

test('query builder is found', function(assert) {
  this.render(hbs`{{bread-crumb}}`);

  assert.equal(this.$('.rsa-query-meta').length, 1, 'Expected to find .rsa-query-meta');
});

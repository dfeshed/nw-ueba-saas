import { moduleForComponent, test, skip } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import DataHelper, { getBrokerService } from '../../../helpers/data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

moduleForComponent('query-bar', 'Integration | Component | query bar', {
  integration: true,
  resolver: engineResolverFor('investigate-events'),
  beforeEach() {
    this.inject.service('redux');
    initialize(this);
  }
});

const broker = getBrokerService();
const timeRange = { id: 'LAST_24_HOURS', name: 'Last 24 Hours', value: 1, unit: 'days' };

test('it renders', function(assert) {
  this.render(hbs`{{events-container/query-bar}}`);
  assert.equal(this.$('.rsa-investigate-query-bar').length, 1, 'Expected root DOM element.');
});

test('it disables(CSS) the submit button if required inputs are not selected', function(assert) {
  assert.expect(1);
  new DataHelper(this.get('redux'))
    .initializeData();
  this.set('selectedService', null);
  this.render(hbs`{{events-container/query-bar selectedService=selectedService}}`);
  assert.ok(
    this.$('.rsa-investigate-query-bar__submit').hasClass('is-disabled'),
    'Expected is-disabled CSS class on the submit button.'
  );
});

test('it disables the timerange if there is no summaryData on the service', function(assert) {
  assert.expect(1);
  new DataHelper(this.get('redux')).initializeData().setServiceId();
  this.set('selectedService', broker);
  this.render(hbs`{{events-container/query-bar selectedService=selectedService}}`);
  assert.ok(
    this.$('.rsa-investigate-query-bar__time-range').children()[0].hasAttribute('aria-disabled'),
    'Expected disabled state on the timerange dropdown.'
  );
});

skip('it enables(CSS) the submit button if required inputs are selected', function(assert) {
  assert.expect(1);
  new DataHelper(this.get('redux'))
    .initializeData();
  this.set('selectedService', broker);
  this.set('selectedTimeRange', timeRange);
  this.render(hbs`{{events-container/query-bar selectedService=selectedService selectedTimeRange=selectedTimeRange}}`);
  assert.notOk(
    this.$('.rsa-investigate-query-bar__submit').hasClass('is-disabled'),
    'Did not expect is-disabled CSS class on the submit button.'
  );
});

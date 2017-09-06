import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';

moduleForComponent('query-bar', 'Integration | Component | query bar', {
  integration: true,
  resolver: engineResolverFor('investigate-events')
});

const broker = { id: 123, displayName: 'broker-1', name: 'BROKER' };
const timeRange = { id: 'LAST_24_HOURS', name: 'Last 24 Hours', seconds: 24 * 60 * 60 };

const submitSelector = '.js-test-investigate-query-bar__submit';

test('it renders', function(assert) {
  this.render(hbs`{{query-bar}}`);
  assert.equal(this.$('.rsa-investigate-query-bar').length, 1, 'Expected root DOM element.');
});

test('it invokes the onSubmit callback with required input', function(assert) {
  assert.expect(1);
  this.set('myCallback', function() {
    assert.ok(true, 'onSubmit was invoked');
  });
  this.set('selectedService', broker);
  this.set('selectedTimeRange', timeRange);

  this.render(hbs`{{query-bar selectedService=selectedService selectedTimeRange=selectedTimeRange onSubmit=myCallback}}`);
  this.$(submitSelector).trigger('click');
});

test('it does not invoke the onSubmit callback when missing required input', function(assert) {
  assert.expect(0);
  this.set('myCallback', function() {
    assert.ok(true, 'onSubmit was invoked');
  });
  this.set('selectedService', null);
  this.set('selectedTimeRange', null);

  this.render(hbs`{{query-bar selectedService=selectedService selectedTimeRange=selectedTimeRange onSubmit=myCallback}}`);
  this.$(submitSelector).trigger('click');

  this.set('selectedService', broker);
  this.set('selectedTimeRange', null);
  this.$(submitSelector).trigger('click');

  this.set('selectedService', null);
  this.set('selectedTimeRange', timeRange);
  this.$(submitSelector).trigger('click');
});

test('it disables(CSS) the submit button if required inputs are not selected', function(assert) {
  assert.expect(1);
  this.set('selectedService', null);
  this.set('selectedTimeRange', null);
  this.render(hbs`{{query-bar selectedService=selectedService selectedTimeRange=selectedTimeRange}}`);
  assert.ok(
    this.$('.rsa-investigate-query-bar__submit').hasClass('is-disabled'),
    'Expected is-disabled CSS class on the submit button.'
  );
});

test('it enables(CSS) the submit button if required inputs are selected', function(assert) {
  assert.expect(1);
  this.set('selectedService', broker);
  this.set('selectedTimeRange', timeRange);
  this.render(hbs`{{query-bar selectedService=selectedService selectedTimeRange=selectedTimeRange}}`);
  assert.notOk(
    this.$('.rsa-investigate-query-bar__submit').hasClass('is-disabled'),
    'Did not expect is-disabled CSS class on the submit button.'
  );
});

import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('rsa-investigate/query-bar', 'Integration | Component | rsa investigate/query bar', {
  integration: true
});

const broker = { id: 123, name: 'broker-1', type: 'BROKER' };
const concentrator = { id: 456, name: 'concentrator-1', type: 'CONCENTRATOR' };
const decoder = { id: 789, name: 'decoder-1', type: 'DECODER' };
const timeRange = { id: 'LAST_24_HOURS', name: 'Last 24 Hours', seconds: 24 * 60 * 60 };

const submitSelector = '.js-test-investigate-query-bar__submit';

test('it renders', function(assert) {
  this.render(hbs`{{rsa-investigate/query-bar}}`);
  assert.equal(this.$('.rsa-investigate-query-bar').length, 1, 'Expected root DOM element.');
});

test('it invokes the onSubmit callback', function(assert) {
  assert.expect(2);
  this.set('myCallback', function() {
    assert.ok(true, 'onSubmit was invoked');
  });

  this.set('services', [ decoder, concentrator, broker ]);
  this.render(hbs`{{rsa-investigate/query-bar services=services onSubmit=myCallback}}`);

  assert.equal(this.$(submitSelector).length, 1, 'Expected submit button DOM element.');
  this.$(submitSelector).trigger('click');
});

test('it disables the submit button if no service is selected', function(assert) {
  this.set('services', []);
  this.render(hbs`{{rsa-investigate/query-bar services=services onSubmit=myCallback}}`);
  assert.ok(
    this.$('.rsa-investigate-query-bar__submit').hasClass('is-disabled'),
    'Expected is-disabled CSS class on the submit button.'
  );
});

test('it enables the submit button if a service and timerange are selected', function(assert) {
  this.set('selectedService', broker);
  this.set('selectedTimeRange', timeRange);
  this.render(hbs`{{rsa-investigate/query-bar selectedService=selectedService selectedTimeRange=selectedTimeRange onSubmit=myCallback}}`);
  assert.notOk(
    this.$('.rsa-investigate-query-bar__submit').hasClass('is-disabled'),
    'Did not expect is-disabled CSS class on the submit button.'
  );
});

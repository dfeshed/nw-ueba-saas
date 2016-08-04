import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('rsa-investigate/query-bar', 'Integration | Component | rsa investigate/query bar', {
  integration: true
});

const broker = { id: 123, name: 'broker-1', type: 'BROKER' };
const concentrator = { id: 456, name: 'concentrator-1', type: 'CONCENTRATOR' };
const decoder = { id: 789, name: 'decoder-1', type: 'DECODER' };

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

test('it defaults to the first broker in the services list', function(assert) {
  assert.expect(1);
  this.set('myCallback', function(serviceId) {
    assert.equal(serviceId, broker.id, 'Expected broker id to be submitted to callback.');
  });
  this.set('services', [ decoder, concentrator, broker ]);
  this.render(hbs`{{rsa-investigate/query-bar services=services onSubmit=myCallback}}`);
  this.$(submitSelector).trigger('click');
});

test('it defaults to the first concentrator if no brokers are in the services list', function(assert) {
  assert.expect(1);
  this.set('myCallback', function(serviceId) {
    assert.equal(serviceId, concentrator.id, 'Expected concentrator id to be submitted to callback.');
  });
  this.set('services', [ decoder, concentrator ]);
  this.render(hbs`{{rsa-investigate/query-bar services=services onSubmit=myCallback}}`);
  this.$(submitSelector).trigger('click');
});

test('it defaults to the first service if no brokers nor concentrators are in the services list', function(assert) {
  assert.expect(1);
  this.set('myCallback', function(serviceId) {
    assert.equal(serviceId, decoder.id, 'Expected concentrator id to be submitted to callback.');
  });
  this.set('services', [ decoder ]);
  this.render(hbs`{{rsa-investigate/query-bar services=services onSubmit=myCallback}}`);
  this.$(submitSelector).trigger('click');
});

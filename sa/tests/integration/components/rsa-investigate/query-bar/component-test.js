import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('rsa-investigate/query-bar', 'Integration | Component | rsa investigate/query bar', {
  integration: true
});

test('it renders', function(assert) {
  this.render(hbs`{{rsa-investigate/query-bar}}`);
  assert.equal(this.$('.rsa-investigate-query-bar').length, 1, 'dom element found');
});

test('it invokes the onSubmit callback', function(assert) {
  assert.expect(2);
  this.set('myCallback', function() {
    assert.ok(true, 'onSubmit was invoked');
  });
  this.render(hbs`{{rsa-investigate/query-bar onSubmit=myCallback}}`);

  assert.equal(this.$('.js-test-investigate-query-bar__submit').length, 1, 'dom element found');
  this.$('.js-test-investigate-query-bar__submit').trigger('click');
});

import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('rsa-investigate/timeline', 'Integration | Component | rsa investigate/timeline', {
  integration: true
});

test('it renders', function(assert) {
  this.render(hbs`{{rsa-investigate/timeline}}`);

  assert.equal(this.$('.rsa-investigate-timeline').length, 1, 'Expected to find root DOM node');
  assert.equal(this.$('.rsa-chart').length, 1, 'Expected to find child chart\'s root DOM node');
});

test('it renders a wait indicator when status is \'wait\'', function(assert) {
  this.render(hbs`{{rsa-investigate/timeline status="wait"}}`);

  assert.equal(this.$('.rsa-investigate-timeline .js-test-wait').length, 1, 'Expected to find wait DOM node');
});

test('it renders an error indicator when status is \'rejected\'', function(assert) {
  assert.expect(3);

  this.set('retryAction', function() {
    assert.ok(true, 'retryAction was invoked when Retry DOM was clicked');
  });

  this.render(hbs`{{rsa-investigate/timeline status="rejected" retryAction=retryAction}}`);

  assert.equal(this.$('.rsa-investigate-timeline .js-test-rejected').length, 1, 'Expected to find error DOM node');

  const retryButton = this.$('.rsa-investigate-timeline .js-test-retry');
  assert.equal(retryButton.length, 1, 'Expected to find Retry DOM node');

  retryButton.trigger('click');
});

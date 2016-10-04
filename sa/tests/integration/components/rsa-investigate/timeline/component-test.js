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

test('it renders an empty message only when the status is \'resolved\' and no data is given', function(assert) {

  this.setProperties({
    status: 'wait',
    data: undefined
  });
  this.render(hbs`{{rsa-investigate/timeline data=data status=status}}`);

  this.set('data', [{ value: 1, count: 1 }]);
  assert.equal(this.$('.rsa-investigate-timeline .js-test-empty').length, 0, 'Expected empty message to be missing');

  this.set('status', 'rejected');
  assert.equal(this.$('.rsa-investigate-timeline .js-test-empty').length, 0, 'Expected empty message to be missing');

  this.set('status', 'resolved');
  assert.equal(this.$('.rsa-investigate-timeline .js-test-empty').length, 0, 'Expected empty message to be missing');

  this.setProperties({
    status: 'resolved',
    data: []
  });
  assert.equal(this.$('.rsa-investigate-timeline .js-test-empty').length, 1, 'Expected empty message in DOM');

  this.setProperties({
    status: 'resolved',
    data: undefined
  });
  assert.equal(this.$('.rsa-investigate-timeline .js-test-empty').length, 1, 'Expected empty message in DOM');
});

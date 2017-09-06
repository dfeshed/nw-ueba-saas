import Ember from 'ember';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';

const { get, set } = Ember;

moduleForComponent('meta-view/key-values', 'Integration | Component | meta-view/key values', {
  integration: true,
  resolver: engineResolverFor('investigate-events')
});

test('it renders', function(assert) {
  this.render(hbs`{{meta-view/key-values}}`);
  assert.equal(this.$('.rsa-investigate-meta-key-values').length, 1);
});

test('it can be toggled open/closed, and responds by toggling instruction DOM and firing toggleAction', function(assert) {
  assert.expect(4);

  const groupKey = { isOpen: false };
  const toggleAction = () => {
    const isOpen = get(groupKey, 'isOpen');
    set(groupKey, 'isOpen', !isOpen);
    assert.ok(true, 'Expected toggleAction to be invoked');
  };

  this.setProperties({
    groupKey,
    toggleAction
  });
  this.render(hbs`{{meta-view/key-values groupKey=groupKey toggleAction=toggleAction}}`);
  assert.equal(this.$('.is-open').length, 0, 'Expected hidden meta value body DOM to reflect closed state');

  const $el = this.$('.js-toggle-open');
  assert.equal($el.length, 1, 'Expected to find DOM that will trigger the toggle action');

  $el.click();
  assert.equal(this.$('.is-open').length, 1, 'Expected visible meta value body DOM to reflect open state');

});
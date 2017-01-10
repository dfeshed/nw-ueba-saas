import Ember from 'ember';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

const { get, set } = Ember;

moduleForComponent('rsa-investigate/meta/key-values', 'Integration | Component | rsa investigate/meta/key values', {
  integration: true
});

test('it renders', function(assert) {
  this.render(hbs`{{rsa-investigate/meta/key-values}}`);

  assert.equal(this.$('.rsa-investigate-meta-key-values').length, 1);
});

test('it can be toggled open/closed, and responds by toggling instruction DOM and firing toggleAction', function(assert) {
  assert.expect(4);

  const isOpenDefault = false;
  const groupKey = { isOpen: isOpenDefault };
  const toggleAction = () => {
    const isOpen = get(groupKey, 'isOpen');
    set(groupKey, 'isOpen', !isOpen);
    assert.ok(true, 'Expected toggleAction to be invoked');
  };

  this.setProperties({
    groupKey,
    toggleAction
  });
  this.render(hbs`{{rsa-investigate/meta/key-values groupKey=groupKey toggleAction=toggleAction}}`);
  assert.equal(this.$('.rsa-investigate-meta-key-values__body').css('display'), 'none', 'Expected hidden meta value body DOM to reflect closed state');

  const $el = this.$('.js-toggle-open');
  assert.equal($el.length, 1, 'Expected to find DOM that will trigger the toggle action');

  $el.click();
  assert.equal(this.$('.rsa-investigate-meta-key-values__body').css('display'), 'block', 'Expected visible meta value body DOM to reflect open state');

});

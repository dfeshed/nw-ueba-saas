import { module, skip } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { click, find, render } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { set, get } from '@ember/object';

module('Integration | Component | Key Values', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  skip('it renders', async function(assert) {
    // TODO - this test breaks with the error
    // (0 , _observer.observer) is not a function
    await render(hbs`{{meta-view/key-values}}`);
    assert.ok(find('.rsa-investigate-meta-key-values'));
  });

  skip('it can be toggled open/closed, and responds by toggling instruction DOM and firing toggleAction', async function(assert) {
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
    await render(hbs`{{meta-view/key-values groupKey=groupKey toggleAction=(action toggleAction)}}`);
    assert.notOk(find('.is-open'), 'Expected hidden meta value body DOM to reflect closed state');
    assert.ok(find('.js-toggle-open'), 'Expected to find DOM that will trigger the toggle action');
    await click('.js-toggle-open');
    assert.ok(find('.is-open'), 'Expected visible meta value body DOM to reflect open state');

  });
});
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

module('Integration | Component | user-container/footer', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-users')
  });

  test('it renders', async function(assert) {
    await render(hbs`{{user-container/footer}}`);
    assert.equal(this.element.textContent.trim(), '');
  });
});

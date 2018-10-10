import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

module('Integration | Component | alert-date', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-users')
  });

  test('it should render date from timestamp', async function(assert) {
    await render(hbs`{{alert-date timestamp=1538112457605}}`);
    assert.equal(this.element.textContent.replace(/\s/g, ''), '2018/09/28');
  });

});
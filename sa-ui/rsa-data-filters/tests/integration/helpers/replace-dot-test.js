import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Helper | replace-dot', function(hooks) {
  setupRenderingTest(hooks);

  test('. is replaced with -', async function(assert) {
    this.set('fieldName', 'machineIdentity.agent.driverErrorCode');
    await render(hbs`{{replace-dot fieldName}}`);
    assert.equal(this.element.textContent, 'machineIdentity-agent-driverErrorCode');
  });

  test('when name is undefined', async function(assert) {
    this.set('fieldName', undefined);
    await render(hbs`{{replace-dot fieldName}}`);
    assert.equal(this.element.textContent, '');
  });
});

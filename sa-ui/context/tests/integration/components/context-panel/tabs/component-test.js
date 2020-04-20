import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';


module('Integration | Component | context-panel/tabs', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('tab is renders', async function(assert) {
    await render(hbs `{{context-panel/tabs}}`);
    assert.equal(findAll('.rsa-nav-tab-group').length, 1, 'Expected to find tabs root element in DOM.');
  });
});
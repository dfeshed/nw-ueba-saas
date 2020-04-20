import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { findAll, render } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';

module('packager-container', 'Integration | Component | packager Container', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('it renders', async function(assert) {
    await render(hbs`{{packager-container}}`);
    const element = findAll('.packager-container');
    assert.equal(element.length, 1, 'Expected to find packager container root element in DOM.');
  });
});
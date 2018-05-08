import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | process-property-panel/property-value', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-process-analysis')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('Property-value size renders', async function(assert) {
    const field = {
      format: 'SIZE',
      value: 6.462693785416757,
      displayName: 'SIZE'
    };
    this.set('field', field);
    await render(hbs`{{process-property-panel/property-value property=field}}`);
    assert.equal(this.element.querySelector('.rsa-content-memsize .units').textContent.trim(), 'bytes', 'Should display size');
  });

  test('Property-value date renders', async function(assert) {
    const field = {
      format: 'DATE',
      value: 1343270377000,
      displayName: 'TIMESTAMP'
    };
    this.set('field', field);
    await render(hbs`{{process-property-panel/property-value property=field}}`);
    assert.equal(this.element.querySelectorAll('.rsa-content-datetime').length, 1, 'Should display timestamp');
  });
});
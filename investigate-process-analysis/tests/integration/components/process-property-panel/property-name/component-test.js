import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | process-property-panel/property-name', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-process-analysis')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('Property-name size renders', async function(assert) {
    const field = {
      format: 'SIZE',
      value: 6.462693785416757,
      displayName: 'SIZE'
    };
    this.set('field', field);
    await render(hbs`{{process-property-panel/property-name property=field}}`);
    assert.equal(this.element.querySelectorAll('.property-name').length, 1, 'Should display name');
  });
});
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import hbs from 'htmlbars-inline-precompile';
import { find, render } from '@ember/test-helpers';

module('Integration | Component | Events Preferences', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('it should show preferences panel trigger even if service is not selected', async function(assert) {
    await render(hbs`{{events-preferences}}`);
    assert.ok(find('.rsa-preferences-panel-trigger'), 'preference panel was rendered');
  });
});
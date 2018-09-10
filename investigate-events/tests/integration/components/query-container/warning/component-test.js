import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import hbs from 'htmlbars-inline-precompile';
import { find, findAll, render } from '@ember/test-helpers';

module('Integration | Component | Console Panel/Warning', function(hooks) {

  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
    initialize(this.owner);
  });

  test('renders the correct dom', async function(assert) {
    await render(hbs`
      {{query-container/console-panel/warning service='concentrator' message="warning"}}
    `);
    assert.equal(findAll('li.warning i.rsa-icon-report-problem-triangle-filled').length, 1);
    assert.equal(find('li.warning strong').textContent, 'concentrator:');
    assert.equal(find('li.warning .message').textContent, 'warning');
  });

});

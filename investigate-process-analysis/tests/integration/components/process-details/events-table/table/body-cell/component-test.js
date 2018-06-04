import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | process-details/events-table/table/body-cell', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-process-analysis')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('it renders', async function(assert) {
    // Set any properties with this.set('myProperty', 'value');
    // Handle any actions with this.set('myAction', function(val) { ... });

    const item = {
      sessionId: 45328
    };
    const column = {
      field: 'sessionId',
      title: 'sessionId'
    };
    this.set('item', item);
    this.set('column', column);

    await render(hbs`{{process-details/events-table/table/body-cell item=item column=column}}`);
    assert.equal(this.element.querySelector('.rsa-data-table-body-cell').textContent.trim(), '45328', 'Should display cell sessionid');

  });
});
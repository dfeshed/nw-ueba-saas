import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { find, findAll, render, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import Service from '@ember/service';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

const transitions = [];

module('Integration | Component | host-detail/process/process-details/process-details-action-bar', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });
  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'host',
      generateURL: () => {
        return;
      },
      transitionTo: (name, args, queryParams) => {
        transitions.push({ name, queryParams });
      }
    }));
  });

  test('process-details actionbar component should rendered', async function(assert) {

    await render(hbs`{{host-detail/process/process-details/process-details-action-bar}}`);

    assert.equal(find('.process-details-label').textContent.trim(), 'Process Details', 'process details label present');
    await click(findAll('.back-to-process')[0]);
    assert.deepEqual(transitions, [{
      name: 'hosts',
      queryParams: {
        pid: null,
        subTabName: null
      }
    }]);

    const redux = this.owner.lookup('service:redux');
    const { endpoint: { visuals: { isProcessDetailsView } } } = redux.getState();
    assert.equal(isProcessDetailsView, false, 'process property panel should close');
  });
});

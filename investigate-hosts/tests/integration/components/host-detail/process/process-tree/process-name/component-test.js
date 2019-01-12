import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, click, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import processData from '../../../../../../integration/components/state/process-data';
import { applyPatch, revertPatch } from '../../../../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Service from '@ember/service';

let setState;
const transitions = [];

module('Integration | Component | host-detail/process/process-tree/process-name', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    setState = (state) => {
      applyPatch(state);
    };
    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'hosts.details',
      generateURL: () => {
        return;
      },
      transitionTo: (name, args, queryParams) => {
        transitions.push({ name, queryParams });
      }
    }));
  });

  hooks.afterEach(function() {
    revertPatch();
  });
  test('should render process name', async function(assert) {
    assert.expect(1);
    this.set('item', { name: 'cmd.exe' });
    this.set('index', 0);
    await render(hbs`{{host-detail/process/process-tree/process-name item=item index=index}}`);
    assert.equal(find('.process-name-column').textContent.trim(), 'cmd.exe', 'process name is rendered');
  });
  test('on click of process name process details shold open', async function(assert) {
    new ReduxDataHelper(setState)
      .processList(processData.processList)
      .processTree(processData.processTree)
      .machineOSType('windows')
      .selectedTab(null)
      .build();
    this.set('item', { name: 'cmd.exe', pid: 1 });
    this.set('index', 0);
    await render(hbs`{{host-detail/process/process-tree/process-name item=item index=index}}`);
    await click(findAll('.process-name label')[0]);
    assert.deepEqual(transitions, [{
      name: 'hosts.details',
      queryParams: {
        pid: 1,
        subTabName: 'process-details',
        tabName: 'PROCESS'
      }
    }]);

  });
});

import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { patchReducer } from '../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import { revertPatch } from '../../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { render, findAll } from '@ember/test-helpers';

let setState;

const monitors = [{
  'id': 'EkMnyG0BbK6JFp72teyl',
  'severity': 1,
  'monitor': 'Reporting Engine Shared Task Critical Utilization',
  'trigger': 'Shared Task Critical Utilization',
  'enabled': true,
  'suppressionConfigured': false
},
{
  'id': 'IUIEyG0BbK6JFp72n_Ka',
  'severity': 1,
  'monitor': 'Reporting Engine Schedule Task Pool Critical Utilization',
  'trigger': 'Scheduled Task Pool Critical utilization',
  'enabled': true,
  'suppressionConfigured': false
}];

module('Integration | Component | hw-table', function(hooks) {

  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    this.owner.inject('component', 'i18n', 'service:i18n');
  });
  hooks.afterEach(function() {
    revertPatch();
  });

  test('hw-table component renders', async function(assert) {

    new ReduxDataHelper(setState).monitors(monitors);
    await render(hbs`{{hw-list/hw-table}}`);
    assert.equal(findAll('.hw-table').length, 1, 'health wellness table is rendered');

  });

});

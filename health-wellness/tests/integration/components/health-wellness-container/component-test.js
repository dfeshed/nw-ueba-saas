import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import Immutable from 'seamless-immutable';
import { patchReducer } from '../../../helpers/vnext-patch';

import { revertPatch } from '../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../helpers/redux-data-helper';


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

module('Integration | Component | health-wellness-container', function(hooks) {

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

  test('health-wellness-container component renders', async function(assert) {

    new ReduxDataHelper(setState).monitors(monitors);
    await render(hbs`{{health-wellness-container}}`);

    assert.equal(findAll('.hw-container').length, 1, 'health wellness container is rendered');
    assert.equal(findAll('.hw-list').length, 1, 'health wellness table is rendered');

  });
});

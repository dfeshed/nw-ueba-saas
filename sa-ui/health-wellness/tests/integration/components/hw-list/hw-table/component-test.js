import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { patchReducer } from '../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import { revertPatch } from '../../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { render, findAll, click, settled } from '@ember/test-helpers';

let setState;

const monitors = [{
  'id': 'EkMnyG0BbK6JFp72teyl',
  'severity': 1,
  'monitor': 'Deporting Engine',
  'trigger': 'Shared Task Critical Utilization',
  'enabled': true,
  'suppressionConfigured': false
},
{
  'id': 'IUIEyG0BbK6JFp72n_Ka',
  'severity': 1,
  'monitor': 'Reporting Engine',
  'trigger': 'Scheduled Task Pool Critical utilization',
  'enabled': true,
  'suppressionConfigured': true
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

    new ReduxDataHelper(setState).monitors(monitors).build();
    await render(hbs`{{hw-list/hw-table}}`);
    assert.equal(findAll('.hw-table').length, 1, 'health wellness table is rendered');
    assert.equal(findAll('.rsa-green-color').length, 1, 'green tick rendered under suppression column');
    assert.equal(findAll('.suppression-not-applied').length, 1, 'no suppression applied');

  });

  test('sort the data on click of column', async function(assert) {

    new ReduxDataHelper(setState).monitors(monitors).build();
    await render(hbs`{{hw-list/hw-table}}`);

    await click(document.querySelectorAll('.rsa-data-table-header-cell')[1].querySelector('.rsa-icon'));
    return settled().then(() => {
      assert.equal(findAll('.rsa-data-table-body-cell.monitor')[0].innerText, 'Reporting Engine', 'sorted data is rendered');
    });
  });

  test('Select all, on click of checkbox', async function(assert) {
    new ReduxDataHelper(setState).monitors(monitors).build();
    await render(hbs`{{hw-list/hw-table}}`);

    await click(findAll('.rsa-form-checkbox')[0]);
    assert.equal(findAll('.rsa-form-checkbox.checked').length, 3, 'All the rows with header is selected');

  });

});

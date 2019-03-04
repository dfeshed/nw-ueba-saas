import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { find, findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { revertPatch } from '../../../../../helpers/patch-reducer';
import Immutable from 'seamless-immutable';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';

let setState;
module('Integration | Component | context-panel/live-connect/feedback', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
  });

  hooks.afterEach(function() {
    revertPatch();
  });

  test('it renders', async function(assert) {

    const model = {
      lookupKey: '10.10.10.10',
      meta: 'IP',
      contextData: {
        liveConnectData: {
          'LiveConnect-Ip': 'testData',
          'allTags': [
            {
              'value': 'RECONNAISSANCE_SCANNING',
              'category': 'RECONNAISSANCE',
              'categoryText': 'RECONNAISSANCE',
              'name': 'SCANNING',
              'nameText': 'Scanning',
              'description': 'Some description'
            }
          ]
        }
      }
    };

    this.set('model', model);

    new ReduxDataHelper(setState)
      .setActiveTabName('LiveConnect-Ip')
      .build();

    await render(hbs`{{context-panel/live-connect/feedback model=model}}`);

    assert.equal(find('.rsa-content-section-header').textContent.trim(), 'Risk Assessment Feedback', 'Header name is found');

    assert.equal(findAll('.power-select.rsa-context-panel__liveconnect__field').length, 4, 'all dropdowns are displayed');
  });
});

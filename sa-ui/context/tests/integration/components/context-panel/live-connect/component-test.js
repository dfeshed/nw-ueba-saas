import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { revertPatch } from '../../../../helpers/patch-reducer';
import Immutable from 'seamless-immutable';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';

const dataSource = {
  field: 'LiveConnect-Ip',
  title: 'context.header.liveConnect',
  dataSourceType: 'LiveConnect-Ip',
  displayType: 'liveConnect',
  class: 'network-live',
  tabRequired: true,
  isConfigured: true
};

let setState;
module('Integration | Component | context-panel/live-connect', function(hooks) {
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
      contextData: {}
    };

    const lookupData = {
      'LiveConnect-Ip': 'testData'
    };

    new ReduxDataHelper(setState)
      .setActiveTabName('LiveConnect-Ip')
      .setDataSources([dataSource])
      .setLookupData([lookupData])
      .build();

    this.set('model', model);

    this.set('activeTabName', 'LiveConnect-Ip');

    await render(hbs`{{context-panel/live-connect activeTabName=activeTabName model=model}}`);

    assert.equal(find('.rsa-context-panel__error-text').textContent.trim(), 'No context data is available for this DataSource.');
  });

  test('Should display live connect error when failed to fetch data from live connect', async function(assert) {

    const model = {
      lookupKey: '10.10.10.10',
      meta: 'IP',
      contextData: {
        'LiveConnect-Ip_ERROR': 'test error'
      }
    };

    this.set('model', model);

    this.set('activeTabName', 'LiveConnect-Ip');

    await render(hbs`{{context-panel/live-connect activeTabName=activeTabName model=model}}`);

    assert.equal(find('.rsa-context-panel__liveconnect__field').textContent.trim(), 'Could not fetch data from Live Connect: test error');
  });
});

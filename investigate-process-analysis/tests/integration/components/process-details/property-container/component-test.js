import { module, test, skip } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';

module('Integration | Component | process-details/process-property-panel', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-process-analysis')
  });
  let setState;
  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    initialize(this.owner);
    this.owner.lookup('service:dateFormat').set('selected', 'MM/dd/yyyy');
    this.owner.lookup('service:timeFormat').set('selected', 'HR12');
    this.owner.lookup('service:timezone').set('selected', { zoneId: 'UTC' });
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  const processProperties = [
    {
      firstFileName: 'services.exe',
      entropy: 6.462693785416757,
      checksumSha256: 'xyz',
      pe: {
        timeStamp: 1561099851500
      },
      size: 10240000,
      signature: {
        thumbprint: '4a14668158d79df2ac08a5ee77588e5c6a6d2c8f',
        signer: 'ABC',
        features: ['signed', 'valid']
      }
    }
  ];
  const queryInput = {
    osType: 'windows'
  };

  test('it renders the property panel', async function(assert) {
    new ReduxDataHelper(setState)
      .processProperties(processProperties)
      .selectedProcess({
        processId: 1
      })
      .queryInput(queryInput)
      .build();
    await render(hbs`{{process-details/property-container}}`);
    assert.equal(findAll('.list_items').length, 5, 'Expected to render 5 sections');
  });

  // yet to handle the empty process execution details case in the property panel.

  skip('it renders the empty process execution details panel with message', async function(assert) {
    new ReduxDataHelper(setState)
      .processProperties(processProperties)
      .selectedProcess({
        processId: 1
      })
      .queryInput(queryInput)
      .build();
    await render(hbs`{{process-details/property-container}}`);
    assert.equal(find('.message').textContent.trim(), 'No matching results', 'No matching results message displayed');
  });

  test('process execution details panel should render details', async function(assert) {
    new ReduxDataHelper(setState)
      .processProperties(processProperties)
      .selectedProcess({
        processId: 1,
        checksumDst: 1
      })
      .queryInput(queryInput)
      .build();
    await render(hbs`{{process-details/property-container}}`);
    assert.equal(find('.title').textContent.trim(), 'Process Execution Details', 'Execution Details Title rendered.');
    assert.equal(findAll('.list_items li').length, 30, 'Expected details render');
  });

  test('execution details field as timestamp', async function(assert) {
    new ReduxDataHelper(setState)
      .processProperties(processProperties)
      .selectedProcess({
        processId: 1
      })
      .queryInput(queryInput)
      .build();
    await render(hbs`{{process-details/property-container}}`);
    assert.equal(findAll('.rsa-content-datetime .datetime')[0].textContent.trim(), '06/21/2019 06:50:51.500 am', 'timestamp displayed in desired format.');
  });

  test('size field converted into MB', async function(assert) {
    new ReduxDataHelper(setState)
      .processProperties(processProperties)
      .selectedProcess({
        processId: 1
      })
      .queryInput(queryInput)
      .build();
    await render(hbs`{{process-details/property-container}}`);
    assert.equal(findAll('.rsa-content-memsize .size')[0].textContent.trim(), '9.8', 'size converted');
    assert.equal(findAll('.rsa-content-memsize .units')[0].textContent.trim(), 'MB', 'size converted in MB');
  });

  test('signature field rendered', async function(assert) {
    new ReduxDataHelper(setState)
      .processProperties(processProperties)
      .selectedProcess({
        processId: 1
      })
      .queryInput(queryInput)
      .build();
    await render(hbs`{{process-details/property-container}}`);
    assert.equal(findAll('.file_properties li .tooltip-text')[19].textContent.trim(), 'signed,valid', 'signature displayed aptly.');

  });
});

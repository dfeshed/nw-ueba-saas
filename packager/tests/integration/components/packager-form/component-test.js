import { Promise } from 'rsvp';
import { module, test, skip } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { revertPatch } from '../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import Packager from 'packager/actions/fetch';
import sinon from 'sinon';
import { find, findAll, render, click } from '@ember/test-helpers';
import { patchReducer } from '../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Immutable from 'seamless-immutable';

let setState;
const setPackagerConfigMethodStub = sinon.stub(Packager, 'setPackagerConfig');
const newConfig = {
  'packageConfig': {
    'id': '59894c9984518a5cfb8fbec2',
    'server': '10.101.34.245',
    'port': 443,
    'certificatePassword': 'test',
    'serviceName': 'test',
    'displayName': 'Display Name Test'
  },
  'logCollectionConfig': {
    'configName': 'test',
    'primaryDestination': '10.10.10.10',
    'channels': [{ channel: 'Security', filter: 'Include', eventId: '1234' }, { channel: 'Security', filter: 'Include', eventId: '111' } ]
  }
};
const devices = [{
  'id': 'id1',
  'name': 'ld11',
  'displayName': 'ld1',
  'host': '10.10.10.10',
  'port': 1234,
  'useTls': false,
  'version': null,
  'family': null,
  'meta': { }
}];

module('Integration | Component | packager-form', function(hooks) {
  setupRenderingTest(hooks);
  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });
  hooks.afterEach(function() {
    revertPatch();
  });
  hooks.after(function() {
    setPackagerConfigMethodStub.restore();
  });

  test('it renders packager form', async function(assert) {
    assert.expect(1);
    await render(hbs`{{packager-form}}`);
    const packagerFormLength = findAll('.packager-form').length;
    assert.equal(packagerFormLength, 1, 'Expected to find packager form root element in DOM.');
  });

  test('it renders form with saved data', async function(assert) {
    assert.expect(2);
    new ReduxDataHelper(setState).defaultConfig().build();
    await render(hbs`{{packager-form}}`);
    // server
    const hostIPInput = find('.host-ip-js input');
    assert.equal(hostIPInput.value, '10.101.34.245', 'Expected to match the value "10.101.34.245" in DOM.');

    // service name
    const serviceName = find('.service-name-js input');
    assert.equal(serviceName.value, 'NWE Agent', 'Expected to match the value "NWE Agent" in DOM.');
  });

  test('full agent ui is rendered', async function(assert) {
    assert.expect(2);
    new ReduxDataHelper(setState)
      .setData('defaultPackagerConfig', newConfig)
      .setData('devices', devices)
      .build();

    await render(hbs`{{packager-form isFullAgentEnabled=false}}`);
    assert.equal(findAll('.x-toggle-container-checked').length, 0, 'Lite Agent is enabled');
    await click('.x-toggle-btn');
    assert.equal(findAll('.x-toggle-container-checked').length, 1, 'Full Agent is enabled');
  });

  test('Channel filter table has pre-populated values', async function(assert) {
    assert.expect(1);
    new ReduxDataHelper(setState)
      .setData('defaultPackagerConfig', newConfig)
      .setData('devices', devices)
      .build();

    await render(hbs`{{packager-form isLogCollectionEnabled=true}}`);
    assert.equal(findAll('.rsa-data-table-body-row').length, 2, 'There are pre-populated values in the table');
  });

  test('Add action creates another row in channel filter table', async function(assert) {
    assert.expect(1);
    new ReduxDataHelper(setState)
      .setData('defaultPackagerConfig', newConfig)
      .setData('devices', devices)
      .build();

    await render(hbs`{{packager-form isLogCollectionEnabled=true}}`);
    await click('.add-row .rsa-form-button');
    assert.equal(findAll('.rsa-data-table-body-row').length, 3, 'There are 3 rows in the table');
  });

  test('Channel filter null validation when generate agent button clicked', async function(assert) {
    assert.expect(2);
    const channelFiltersWithNullData = {
      'packageConfig': {
        'id': '59894c9984518a5cfb8fbec2',
        'server': '10.101.34.245',
        'port': 443,
        'certificatePassword': 'test'
      },
      'logCollectionConfig': {
        'configName': 'test',
        'primaryDestination': '10.10.10.10',
        'protocol': 'UDP',
        'channels': [{ channel: 'Security', filter: 'Include', eventId: '' }]
      }
    };
    new ReduxDataHelper(setState)
      .setData('defaultPackagerConfig', channelFiltersWithNullData)
      .setData('devices', devices)
      .build();

    this.set('selectedProtocol', 'UDP');
    await render(hbs`{{packager-form isLogCollectionEnabled=true selectedProtocol=selectedProtocol}}`);
    const beforeIsErrorEl = findAll('.packager-form .is-error');
    assert.equal(beforeIsErrorEl.length, 0, 'is-error class is not present');
    await click('.generate-button-js .rsa-form-button');
    const afterIsErrorEl = findAll('.packager-form .is-error');
    assert.equal(afterIsErrorEl.length, 1, 'is-error class is rendered');
  });

  test('Channel filter regex validation when generate agent button clicked', async function(assert) {
    assert.expect(2);
    const channelFiltersWithInvalidData = {
      'packageConfig': {
        'id': '59894c9984518a5cfb8fbec2',
        'server': '10.101.34.245',
        'port': 443,
        'certificatePassword': 'test'
      },
      'logCollectionConfig': {
        'configName': 'test',
        'protocol': 'UDP',
        'primaryDestination': '10.10.10.10',
        'channels': [{ channel: 'Security', filter: 'Include', eventId: 'abcd' }]
      }
    };
    new ReduxDataHelper(setState)
      .setData('defaultPackagerConfig', channelFiltersWithInvalidData)
      .setData('devices', devices)
      .build();
    this.set('selectedProtocol', 'UDP');
    await render(hbs`{{packager-form isLogCollectionEnabled=true selectedProtocol=selectedProtocol}}`);
    const beforeIsErrorEl = findAll('.packager-form .is-error');
    assert.equal(beforeIsErrorEl.length, 0, 'is-error class is not present');
    await click('.generate-button-js .rsa-form-button');
    const afterIsErrorEl = findAll('.packager-form .is-error');
    assert.equal(afterIsErrorEl.length, 1, 'is-error class is rendered');

  });
  // i am looking into it, some problem with stub usage in ember 3
  skip('Event Id server validation for out of range case when generate agent button clicked', async function(assert) {
    const channelFiltersWithInvalidData = {
      'packageConfig': {
        'id': '59894c9984518a5cfb8fbec2',
        'server': '10.101.34.245',
        'port': 443,
        'certificatePassword': 'test'
      },
      'logCollectionConfig': {
        'configName': 'test',
        'protocol': 'UDP',
        'primaryDestination': '10.10.10.10',
        'channels': [{ channel: 'Security', filter: 'Include', eventId: '111111111111' }]
      }
    };
    new ReduxDataHelper(setState)
      .defaultConfig(channelFiltersWithInvalidData)
      .setDevices(devices)
      .build();
    this.set('selectedProtocol', 'UDP');
    await render(hbs`{{packager-form isLogCollectionEnabled=true selectedProtocol=selectedProtocol}}`);
    const error = { meta: { reason: 'EVENT_ID_INVALID', identifier: 1 } };
    const beforeIsErrorEl = findAll('.packager-form .is-error');
    assert.equal(beforeIsErrorEl.length, 0, 'is-error class is not present');
    setPackagerConfigMethodStub.returns(Promise.reject(error));
    const afterIsErrorEl = findAll('.packager-form .is-error');
    assert.equal(afterIsErrorEl.length, 1, 'is-error class is rendered');
  });

  test('validates the packager config and sets the error field', async function(assert) {
    assert.expect(5);
    new ReduxDataHelper(setState).setData('defaultPackagerConfig', newConfig).build();

    await render(hbs`{{packager-form}}`);

    const $IP_FIELD = this.$('.server-input-js input');
    const $PORT_FIELD = this.$('.port-input-js input');
    const $SERVICE_NAME_FIELD = this.$('.service-name-input-js input');
    const $PASSWORD_FIELD = this.$('.password-input-js input');
    const $INPUT = this.$('.server-input-group input');
    const $DISPLAY_NAME_FIELD = this.$('.display-name-input-js input');

    // Invalid ip/hostname
    $IP_FIELD.val('-1.1.x.x');
    $INPUT.change();

    await click('.generate-button-js .rsa-form-button');
    assert.ok(find('.server-input-js').classList.contains('is-error'), 'Expected to have error class on server field');

    // Invalid port
    $IP_FIELD.val('1.1.1.1');
    $PORT_FIELD.val('10X');
    $INPUT.change();
    await click('.generate-button-js .rsa-form-button');
    assert.ok(find('.port-input-js').classList.contains('is-error'), 'Expected to have error class on port field');

    // Invalid Service name
    $IP_FIELD.val('1.1.1.1');
    $PORT_FIELD.val('123');
    $SERVICE_NAME_FIELD.val('End##Server');
    $INPUT.change();
    await click('.generate-button-js .rsa-form-button');
    assert.ok(find('.service-name-input-js').classList.contains('is-error'), 'Expected to have error class on service field');

    // Invalid Displpay name
    $IP_FIELD.val('1.1.1.1');
    $PORT_FIELD.val('123');
    $SERVICE_NAME_FIELD.val('EndServer');
    $DISPLAY_NAME_FIELD.val('Display&Name#Test');
    $INPUT.change();
    await click('.generate-button-js .rsa-form-button');
    assert.ok(find('.display-name-input-js').classList.contains('is-error'), 'Expected to have error class on display field');

    // Password is required
    $IP_FIELD.val('1.1.1.1');
    $PORT_FIELD.val('123');
    $SERVICE_NAME_FIELD.val('EndpointServer');
    $PASSWORD_FIELD.val('');
    $INPUT.change();
    await click('.generate-button-js .rsa-form-button');
    assert.ok(find('.password-input-js').classList.contains('is-error'), 'Expected to have error class on password field');
  });

  test('Protocol and TestLog resets to default when reset button is clicked', async function(assert) {
    assert.expect(2);
    new ReduxDataHelper(setState)
      .setData('defaultPackagerConfig', newConfig)
      .build();
    this.set('selectedProtocol', 'UDP');
    this.set('testLog', false);
    await render(hbs`{{packager-form isLogCollectionEnabled=true selectedProtocol=selectedProtocol testLog=testLog}}`);
    await click('.reset-button .rsa-form-button');
    const protocol = this.get('selectedProtocol');
    const testLog = this.get('testLog');
    assert.equal(protocol, 'TCP');
    assert.equal(testLog, true);
  });

  test('Test log is set false on uncheck of checkbox', async function(assert) {
    assert.expect(1);
    new ReduxDataHelper(setState)
      .setData('defaultPackagerConfig', newConfig)
      .build();
    this.set('testLog', 'true');
    await render(hbs`{{packager-form isLogCollectionEnabled=true testLog=testLog}}`);
    await click('.testLog .rsa-form-checkbox-label');
    const disableTestLog = this.get('testLog');
    assert.equal(disableTestLog, false);
  });

  test('required fields are rendered when full agent is enabled', async function(assert) {
    assert.expect(6);
    await render(hbs`{{packager-form isFullAgentEnabled=true}}`);
    const driverServiceEl = findAll('.driver-server-input-group');
    const driverServiceNameEl = findAll('.driver-server-input-group .service-name-js input');
    const driverDisplayNameEl = findAll('.driver-server-input-group .display-name-js input');
    const driverDescriptionEl = findAll('.driver-description-section input');
    const monitoringModeCheckboxEl = findAll('.monitoring-mode-section');
    const enableMonitoringChecked = findAll('.monitoring-mode-section label.checked');
    assert.equal(driverServiceEl.length, 1, 'driver section is rendered');
    assert.equal(driverServiceNameEl.length, 1, 'driver service name input is rendered');
    assert.equal(driverDisplayNameEl.length, 1, 'driver display name is rendered');
    assert.equal(driverDescriptionEl.length, 1, 'driver description is rendered');
    assert.equal(monitoringModeCheckboxEl.length, 1, 'monitoring mode is rendered');
    assert.equal(enableMonitoringChecked.length, 1, 'monitoring mode is checked by default');
  });
});
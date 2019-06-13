import { Promise } from 'rsvp';
import { module, test, skip } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { revertPatch } from '../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import Packager from 'packager/actions/fetch';
import sinon from 'sinon';
import { find, findAll, render, click, triggerKeyEvent, triggerEvent } from '@ember/test-helpers';
import { patchReducer } from '../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import { clickTrigger } from 'ember-power-select/test-support/helpers';

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
const selectedServerIP = {
  id: '2f3a0c01-a366-49a7-afb5-f04036fc7a97',
  name: 'endpoint-server',
  displayName: 'EPS1-Arya - Endpoint Server',
  host: '10.40.15.204',
  hostName: 'EPS1-Arya'
};

module('Integration | Component | packager-form', function(hooks) {
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
  hooks.after(function() {
    setPackagerConfigMethodStub.restore();
  });

  test('it renders packager form', async function(assert) {
    assert.expect(1);
    new ReduxDataHelper(setState).defaultConfig().setData('selectedServerIP', selectedServerIP).build();
    await render(hbs`{{packager-form}}`);
    const packagerFormLength = findAll('.packager-form').length;
    assert.equal(packagerFormLength, 1, 'Expected to find packager form root element in DOM.');
  });

  test('it renders form with saved data', async function(assert) {
    assert.expect(2);
    new ReduxDataHelper(setState).defaultConfig().setData('selectedServerIP', selectedServerIP).build();
    this.set('editedHost', 'EPS1-Arya');
    await render(hbs`{{packager-form editedHost=editedHost}}`);
    // server
    const hostIPInput = find('.host-ip-js input');
    assert.equal(hostIPInput.value, 'EPS1-Arya', 'Expected to match the value "EPS1-Arya" in DOM.');

    // service name
    const serviceName = find('.service-name-js input');
    assert.equal(serviceName.value, 'NWE Agent', 'Expected to match the value "NWE Agent" in DOM.');
  });

  test('Accordion is opened only when error is within the accordion section', async function(assert) {
    assert.expect(4);
    const channelFiltersWithNullData = {
      'packageConfig': {
        'id': '59894c9984518a5cfb8fbec2',
        'server': 'test',
        'port': 443,
        'certificatePassword': ''
      }
    };
    new ReduxDataHelper(setState)
      .setData('defaultPackagerConfig', channelFiltersWithNullData)
      .setData('selectedServerIP', selectedServerIP)
      .setData('devices', devices)
      .build();

    this.set('selectedProtocol', 'UDP');
    await render(hbs`{{packager-form selectedProtocol=selectedProtocol}}`);
    const beforeIsErrorEl = findAll('.packager-form .is-error');
    assert.equal(beforeIsErrorEl.length, 0, 'is-error class is not present');
    assert.equal(findAll('.agentConfiguration.is-collapsed').length, 1, 'Accordion is collapsed');
    await click('.generate-button-js .rsa-form-button');
    const afterIsErrorEl = findAll('.packager-form .is-error');
    assert.equal(afterIsErrorEl.length, 1, 'is-error class is rendered');
    assert.equal(findAll('.agentConfiguration.is-collapsed').length, 1, 'Accordion is collapsed, as error is not within the accordion');
  });

  test('Channel filter null validation when generate agent button clicked', async function(assert) {
    assert.expect(2);
    const channelFiltersWithNullData = {
      'packageConfig': {
        'id': '59894c9984518a5cfb8fbec2',
        'server': '10.101.34.245',
        'port': 443,
        'certificatePassword': 'test'
      }
    };
    new ReduxDataHelper(setState)
      .setData('defaultPackagerConfig', channelFiltersWithNullData)
      .setData('selectedServerIP', selectedServerIP)
      .setData('devices', devices)
      .build();

    this.set('selectedProtocol', 'UDP');
    await render(hbs`{{packager-form selectedProtocol=selectedProtocol}}`);
    const beforeIsErrorEl = findAll('.packager-form .is-error');
    assert.equal(beforeIsErrorEl.length, 0, 'is-error class is not present');
    await click('.generate-button-js .rsa-form-button');
    const afterIsErrorEl = findAll('.packager-form .is-error');
    assert.equal(afterIsErrorEl.length, 1, 'is-error class is rendered');
  });

  test('Channel filter regex validation when generate agent button clicked', async function(assert) {
    assert.expect(4);
    const channelFiltersWithInvalidData = {
      'packageConfig': {
        'id': '59894c9984518a5cfb8fbec2',
        'server': '10.101.34.245',
        'port': 443,
        'certificatePassword': 'test'
      }
    };
    new ReduxDataHelper(setState)
      .setData('defaultPackagerConfig', channelFiltersWithInvalidData)
      .setData('devices', devices)
      .setData('selectedServerIP', selectedServerIP)
      .build();
    this.set('selectedProtocol', 'UDP');
    await render(hbs`{{packager-form selectedProtocol=selectedProtocol}}`);
    const beforeIsErrorEl = findAll('.packager-form .is-error');
    assert.equal(beforeIsErrorEl.length, 0, 'is-error class is not present');
    assert.equal(findAll('.agentConfiguration.is-collapsed').length, 1, 'Accordion is collapsed');
    await click('.generate-button-js .rsa-form-button');
    const afterIsErrorEl = findAll('.packager-form .is-error');
    assert.equal(afterIsErrorEl.length, 1, 'is-error class is rendered');
    assert.equal(findAll('.agentConfiguration.is-collapsed').length, 0, 'Accordion is open, as error is within the accordion');

  });
  // i am looking into it, some problem with stub usage in ember 3
  skip('Event Id server validation for out of range case when generate agent button clicked', async function(assert) {
    const channelFiltersWithInvalidData = {
      'packageConfig': {
        'id': '59894c9984518a5cfb8fbec2',
        'server': '10.101.34.245',
        'port': 443,
        'certificatePassword': 'test'
      }
    };
    new ReduxDataHelper(setState)
      .defaultConfig(channelFiltersWithInvalidData)
      .setDevices(devices)
      .build();
    this.set('selectedProtocol', 'UDP');
    await render(hbs`{{packager-form selectedProtocol=selectedProtocol}}`);
    const error = { meta: { reason: 'EVENT_ID_INVALID', identifier: 1 } };
    const beforeIsErrorEl = findAll('.packager-form .is-error');
    assert.equal(beforeIsErrorEl.length, 0, 'is-error class is not present');
    setPackagerConfigMethodStub.returns(Promise.reject(error));
    const afterIsErrorEl = findAll('.packager-form .is-error');
    assert.equal(afterIsErrorEl.length, 1, 'is-error class is rendered');
  });

  test('validates the packager config and sets the error field', async function(assert) {
    assert.expect(5);
    new ReduxDataHelper(setState)
      .setData('defaultPackagerConfig', newConfig)
      .setData('selectedServerIP', selectedServerIP)
      .build();

    await render(hbs`{{packager-form}}`);
    const IP_FIELD = find('.server-input-js input');
    const PORT_FIELD = find('.port-input-js input');
    const SERVICE_NAME_FIELD = find('.service-name-input-js input');
    const PASSWORD_FIELD = find('.password-input-js input');
    const DISPLAY_NAME_FIELD = find('.display-name-input-js input');

    // Invalid ip/hostname
    IP_FIELD.value = '-1.1.x.x';
    await triggerEvent(IP_FIELD, 'change');

    await click('.generate-button-js .rsa-form-button');
    assert.ok(find('.server-input-js').classList.contains('is-error'), 'Expected to have error class on server field');

    // Invalid port
    IP_FIELD.value = '1.1.1.1';
    PORT_FIELD.value = '10X';

    await triggerEvent(IP_FIELD, 'change');
    await triggerEvent(PORT_FIELD, 'change');
    await click('.generate-button-js .rsa-form-button');
    assert.ok(find('.port-input-js').classList.contains('is-error'), 'Expected to have error class on port field');

    // Invalid Service name
    PORT_FIELD.value = '123';
    SERVICE_NAME_FIELD.value = 'End##Server';
    await triggerEvent(PORT_FIELD, 'change');
    await triggerEvent(SERVICE_NAME_FIELD, 'change');
    await click('.generate-button-js .rsa-form-button');
    assert.ok(find('.service-name-input-js').classList.contains('is-error'), 'Expected to have error class on service field');

    // Invalid Display name
    SERVICE_NAME_FIELD.value = 'EndServer';
    DISPLAY_NAME_FIELD.value = 'Display&Name#Test';
    await triggerEvent(SERVICE_NAME_FIELD, 'change');
    await triggerEvent(DISPLAY_NAME_FIELD, 'change');
    await click('.generate-button-js .rsa-form-button');
    assert.ok(find('.display-name-input-js').classList.contains('is-error'), 'Expected to have error class on display field');

    // Password is required
    SERVICE_NAME_FIELD.value = 'EndpointServer';
    PASSWORD_FIELD.value = '';
    await triggerEvent(SERVICE_NAME_FIELD, 'change');
    await triggerEvent(PASSWORD_FIELD, 'change');
    await click('.generate-button-js .rsa-form-button');
    assert.ok(find('.password-input-js').classList.contains('is-error'), 'Expected to have error class on password field');
  });

  test('Password field is not erroneous, on key up', async function(assert) {
    new ReduxDataHelper(setState)
      .setData('defaultPackagerConfig', newConfig)
      .setData('selectedServerIP', selectedServerIP)
      .build();
    await render(hbs`{{packager-form}}`);
    await triggerKeyEvent('.password-input-js  input', 'keyup', 13);
    assert.equal(find('.password-input-js').classList.contains('is-error'), false, 'Error class is not present, on password field.');
  });

  test('required fields are rendered in the AGENT CONFIGURATION section', async function(assert) {
    const testConfig = {
      'packageConfig': {
        'id': '59894c9984518a5cfb8fbec2',
        'server': '10.101.34.245',
        'port': 443,
        'certificatePassword': null,
        'serviceName': 'test',
        'displayName': 'Display Name Test'
      }
    };

    new ReduxDataHelper(setState)
      .setData('defaultPackagerConfig', testConfig)
      .setData('selectedServerIP', selectedServerIP)
      .build();
    await render(hbs`{{packager-form}}`);

    assert.equal(find('.agentConfigNote').textContent.trim(), 'For a subsequent installation/upgrade, use the same service names.', 'Note is present');
    const agentConfigurationDriverEl = findAll('.agentConfiguration .agentConfigurationDriver');
    const driverServiceEl = findAll('.agentConfigurationDriver .driver-server-input-group');
    const driverServiceNameEl = findAll('.agentConfigurationDriver .driver-server-input-group .service-name-js input');
    const driverDisplayNameEl = findAll('.agentConfigurationDriver .driver-server-input-group .display-name-js input');
    const driverDescriptionEl = findAll('.agentConfigurationDriver .driver-description-section input');
    assert.equal(agentConfigurationDriverEl.length, 1, 'Driver section present under agent configuration.');
    assert.equal(driverServiceEl.length, 1, 'driver section is rendered');
    assert.equal(driverServiceNameEl.length, 1, 'driver service name input is rendered');
    assert.equal(driverDisplayNameEl.length, 1, 'driver display name is rendered');
    assert.equal(driverDescriptionEl.length, 1, 'driver description is rendered');

    const agentConfigurationServiceEl = findAll('.agentConfiguration .agentConfigurationService');
    const serviceEl = findAll('.agentConfigurationService .server-input-group');
    const serviceNameEl = findAll('.agentConfigurationService .server-input-group .service-name-js input');
    const serviceDisplayNameEl = findAll('.agentConfigurationService .server-input-group .display-name-js input');
    const serviceDescriptionEl = findAll('.agentConfigurationService .service-description-section input');
    assert.equal(agentConfigurationServiceEl.length, 1, 'Service section present under agent configuration.');
    assert.equal(serviceEl.length, 1, 'Service section is rendered');
    assert.equal(serviceNameEl.length, 1, 'Service service name input is rendered');
    assert.equal(serviceDisplayNameEl.length, 1, 'Service display name is rendered');
    assert.equal(serviceDescriptionEl.length, 1, 'Service description is rendered');
  });

  test('Password field is not valid, on key up', async function(assert) {
    this.set('isPasswordError', false);
    const testConfig = {
      'packageConfig': {
        'id': '59894c9984518a5cfb8fbec2',
        'server': '10.101.34.245',
        'port': 443,
        'certificatePassword': null,
        'serviceName': 'test',
        'displayName': 'Display Name Test'
      }
    };

    new ReduxDataHelper(setState)
      .setData('defaultPackagerConfig', testConfig)
      .setData('selectedServerIP', selectedServerIP)
      .build();
    await render(hbs`{{packager-form isPasswordError=isPasswordError}}`);
    await triggerKeyEvent('.password-input-js  input', 'keyup', 13);
    assert.equal(this.get('isPasswordError'), true);
    assert.equal(find('.password-input-js').classList.contains('is-error'), true, 'Error class is present on password field.');
  });

  test('Force Overwrite', async function(assert) {
    const testConfig = {
      'packageConfig': {
        'id': '59894c9984518a5cfb8fbec2',
        'server': '10.101.34.245',
        'port': 443,
        'certificatePassword': null,
        'serviceName': 'test',
        'displayName': 'Display Name Test',
        'forceOverwrite': false
      }
    };

    new ReduxDataHelper(setState)
      .setData('defaultPackagerConfig', testConfig)
      .setData('selectedServerIP', selectedServerIP)
      .build();
    await render(hbs`{{packager-form}}`);
    assert.equal(findAll('.field.force-overwrite .rsa-form-checkbox.checked').length, 0, 'Force overwrite is checked');
    await click(findAll('.field.force-overwrite .rsa-form-checkbox')[0]);
    assert.equal(findAll('.field.force-overwrite .rsa-form-checkbox.checked').length, 1, 'Force overwrite is not checked');
  });

  test('Endpoint list', async function(assert) {
    const testConfig = {
      'packageConfig': {
        'id': '59894c9984518a5cfb8fbec2',
        'server': '10.101.34.245',
        'port': 443,
        'certificatePassword': null,
        'serviceName': 'test',
        'displayName': 'Display Name Test',
        'forceOverwrite': false,
        'serviceId': '2f3a0c01-a366-49a7-afb5-f04036fc7a97'
      }
    };
    const endpointServerList = [
      {
        id: '2f3a0c01-a366-49a7-afb5-f04036fc7a97',
        name: 'endpoint-server',
        displayName: 'EPS1-Arya - Endpoint Server',
        host: '10.40.15.204',
        port: 7050,
        useTls: true,
        version: '11.3.1.0',
        family: 'launch',
        meta: {},
        hostName: 'EPS1-Arya'
      },
      {
        id: 'ec8e6e1e-efa6-45d6-b577-6b39de544e00',
        name: 'endpoint-server',
        displayName: 'EPS2-Arya - Endpoint Server',
        host: '10.40.12.5',
        port: 7050,
        useTls: true,
        version: '11.3.0.0',
        family: 'launch',
        meta: {},
        hostName: 'EPS2-Arya'
      }
    ];
    new ReduxDataHelper(setState)
      .setData('defaultPackagerConfig', testConfig)
      .setData('selectedServerIP', selectedServerIP)
      .setData('endpointServerList', endpointServerList)
      .build();
    await render(hbs`{{packager-form}}`);
    await clickTrigger('.server-list-dropdown');
    assert.equal(findAll('.ember-power-select-option').length, 2, '2 endpoint servers rendered');
  });

  test('Editable server and port value rendering', async function(assert) {
    const testConfig = {
      'packageConfig': {
        'id': '59894c9984518a5cfb8fbec2',
        'server': '10.101.34.245',
        'port': 443,
        'certificatePassword': null,
        'serviceName': 'test',
        'displayName': 'Display Name Test',
        'forceOverwrite': false,
        'serviceId': '2f3a0c01-a366-49a7-afb5-f04036fc7a97'
      }
    };

    new ReduxDataHelper(setState)
      .setData('defaultPackagerConfig', testConfig)
      .setData('selectedServerIP', selectedServerIP)
      .build();
    this.set('editedHost', '10.101.34.245');
    await render(hbs`{{packager-form editedHost=editedHost}}`);
    assert.equal(find('.host-ip-js input').value, '10.101.34.245', 'Editable endpoint servers rendered');
    assert.equal(find('.host-port-js input').value, '443', 'Editable endpoint servers port rendered');
  });

  test('Editable server and port value rendering', async function(assert) {
    const testConfig = {
      'packageConfig': {
        'id': '59894c9984518a5cfb8fbec2',
        'server': '10.101.34.245',
        'port': 4434,
        'certificatePassword': null,
        'serviceName': 'test',
        'displayName': 'Display Name Test',
        'forceOverwrite': false,
        'serviceId': 'a366-49a7-afb5-f04036fc7a97-2f3a0c01'
      }
    };

    new ReduxDataHelper(setState)
      .setData('defaultPackagerConfig', testConfig)
      .setData('selectedServerIP', selectedServerIP)
      .build();
    this.set('editedHost', 'EPS1-Arya');
    await render(hbs`{{packager-form editedHost=editedHost}}`);
    assert.equal(find('.host-ip-js input').value, 'EPS1-Arya', 'Editable endpoint server rendered');
    assert.equal(find('.host-ip-js input').placeholder, '10.40.15.204', 'Endpoint server rendered as placeholder');
    assert.equal(find('.host-port-js input').value, '443', 'Editable endpoint servers port rendered');
  });

  test('check overwrite info message is present', async function(assert) {
    const testConfig = {
      'packageConfig': {
        'id': '59894c9984518a5cfb8fbec2',
        'server': '10.101.34.245',
        'port': 4434,
        'certificatePassword': null,
        'serviceName': 'test',
        'displayName': 'Display Name Test',
        'forceOverwrite': false,
        'serviceId': 'a366-49a7-afb5-f04036fc7a97-2f3a0c01'
      }
    };

    new ReduxDataHelper(setState)
      .setData('defaultPackagerConfig', testConfig)
      .setData('selectedServerIP', selectedServerIP)
      .build();
    this.set('editedHost', 'EPS1-Arya');
    await render(hbs`{{packager-form editedHost=editedHost}}`);
    assert.equal(find('.overwrite-info').textContent.trim(), 'Overwrites the installed Windows agent regardless of the version.', 'Force Overwrite info message is displayed');
  });
});

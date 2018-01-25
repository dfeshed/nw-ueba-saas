import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import waitFor from '../../../helpers/wait-for';
import { clickTrigger } from '../../../helpers/ember-power-select';
import $ from 'jquery';
import { applyPatch, revertPatch } from '../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import Packager from 'packager/actions/fetch';
import Ember from 'ember';
import sinon from 'sinon';
import wait from 'ember-test-helpers/wait';

let setState;

const { RSVP: { Promise } } = Ember;
const setPackagerConfigMethodStub = sinon.stub(Packager, 'setPackagerConfig');

moduleForComponent('packager-form', 'Integration | Component | packager form', {
  integration: true,
  beforeEach() {
    setState = (state) => {
      applyPatch(state);
      this.inject.service('redux');
      return this;
    };
    this.registry.injection('component', 'i18n', 'service:i18n');
  },
  afterEach() {
    revertPatch();
  },
  after() {
    setPackagerConfigMethodStub.restore();
  }
});

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

test('it renders packager form', function(assert) {
  this.render(hbs`{{packager-form}}`);
  const $el = this.$('.packager-form');
  assert.equal($el.length, 1, 'Expected to find packager form root element in DOM.');
});

test('it renders form with saved data', function(assert) {
  new ReduxDataHelper(setState).defaultConfig().build();
  this.render(hbs`{{packager-form}}`);
  // server
  const $el = this.$('.host-ip-js input');
  assert.equal($el.val(), '10.101.34.245', 'Expected to match the value "10.101.34.245" in DOM.');

  // service name
  const $serviceName = this.$('.service-name-js input');
  assert.equal($serviceName.val(), 'NWE Agent', 'Expected to match the value "NWE Agent" in DOM.');
});

test('Primary decoder have values', function(assert) {
  new ReduxDataHelper(setState)
    .setData('defaultPackagerConfig', newConfig)
    .setData('devices', devices)
    .build();

  this.render(hbs`{{packager-form isLogCollectionEnabled=true}}`);
  clickTrigger('.power-select:nth-child(1)');
  assert.equal($('li.ember-power-select-option').length, 1, 'There is 1 option available for LD/VLC');
});

test('Channel filter table has pre-populated values', function(assert) {
  new ReduxDataHelper(setState)
    .setData('defaultPackagerConfig', newConfig)
    .setData('devices', devices)
    .build();

  this.render(hbs`{{packager-form isLogCollectionEnabled=true}}`);
  assert.equal(this.$('.rsa-data-table-body-row').length, 1, 'There are pre-populated values in the table');
});

test('Add action creates another row in channel filter table', function(assert) {
  new ReduxDataHelper(setState)
    .setData('defaultPackagerConfig', newConfig)
    .setData('devices', devices)
    .build();

  this.render(hbs`{{packager-form isLogCollectionEnabled=true}}`);
  const $button = this.$('.add-row .rsa-form-button');
  return waitFor(() => $button.trigger('click'))().then(() => {
    assert.equal(this.$('.rsa-data-table-body-row').length, 3, 'There are 3 rows in the table');
  });
});

test('Channel filter null validation when generate agent button clicked', function(assert) {
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
  this.render(hbs`{{packager-form isLogCollectionEnabled=true selectedProtocol=selectedProtocol}}`);
  const $button = this.$('.generate-button-js .rsa-form-button');
  return waitFor(() => $button.trigger('click'))().then(() => {
    const $eventId = this.$($('.event-id')).parent().attr('class');
    assert.ok($eventId.includes('is-error'));
  });
});

test('Channel filter regex validation when generate agent button clicked', function(assert) {
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
  this.render(hbs`{{packager-form isLogCollectionEnabled=true selectedProtocol=selectedProtocol}}`);
  const $button = this.$('.generate-button-js .rsa-form-button');
  return waitFor(() => $button.trigger('click'))().then(() => {
    const $eventId = this.$($('.event-id')).parent().attr('class');
    assert.ok($eventId.includes('is-error'));
  });

});

test('Event Id server validation for out of range case when generate agent button clicked', function(assert) {
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
  this.render(hbs`{{packager-form isLogCollectionEnabled=true selectedProtocol=selectedProtocol}}`);
  const error = { meta: { reason: 'EVENT_ID_INVALID', identifier: 1 } };
  setPackagerConfigMethodStub.returns(Promise.reject(error));
  const $button = this.$('.generate-button-js .rsa-form-button');
  $button.trigger('click');
  return wait().then(() => {
    const $eventId = this.$($('.event-id')).parent().attr('class');
    assert.ok($eventId.includes('is-error'));
  });
});

test('Event Id server validation for success case to test callback action (reset form) when generate Log Configuration button clicked', function(assert) {
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
      'channels': [{ channel: 'Security', filter: 'Include', eventId: '12' }]
    }
  };
  new ReduxDataHelper(setState)
    .defaultConfig(channelFiltersWithInvalidData)
    .setDevices(devices)
    .build();
  this.set('selectedProtocol', 'UDP');
  this.render(hbs`{{packager-form isLogCollectionEnabled=true selectedProtocol=selectedProtocol}}`);
  const responseData = { data: { id: 'd3710823-c89c-470e-a91e-4c2dded20d54' } };
  setPackagerConfigMethodStub.returns(Promise.resolve(responseData));
  const $button = this.$('.generate-button-js .rsa-form-button');
  $button.trigger('click');
  return wait().then(() => {
    const $eventId = this.$('.event-id').text();
    assert.ok($eventId === '');
  });
});

test('validates the packager config and sets the error field', function(assert) {
  assert.expect(5);
  new ReduxDataHelper(setState).setData('defaultPackagerConfig', newConfig).build();

  this.render(hbs`{{packager-form}}`);

  const $IP_FIELD = this.$('.server-input-js input');
  const $PORT_FIELD = this.$('.port-input-js input');
  const $SERVICE_NAME_FIELD = this.$('.service-name-input-js input');
  const $PASSWORD_FIELD = this.$('.password-input-js input');
  const $INPUT = this.$('.server-input-group input');
  const $DISPLAY_NAME_FIELD = this.$('.display-name-input-js input');

  // Invalid ip
  $IP_FIELD.val('1.1.x.x');
  $INPUT.change();

  this.$('.generate-button-js .rsa-form-button').trigger('click');
  assert.ok(this.$('.server-input-js').hasClass('is-error'), 'Expected to have error class on server field');

  // Invalid port
  $IP_FIELD.val('1.1.1.1');
  $PORT_FIELD.val('10X');
  $INPUT.change();
  this.$('.generate-button-js .rsa-form-button').trigger('click');
  assert.ok(this.$('.port-input-js').hasClass('is-error'), 'Expected to have error class on port field');

  // Invalid Service name
  $IP_FIELD.val('1.1.1.1');
  $PORT_FIELD.val('123');
  $SERVICE_NAME_FIELD.val('End##Server');
  $INPUT.change();
  this.$('.generate-button-js .rsa-form-button').trigger('click');
  assert.ok(this.$('.service-name-input-js').hasClass('is-error'), 'Expected to have error class on service field');

  // Invalid Displpay name
  $IP_FIELD.val('1.1.1.1');
  $PORT_FIELD.val('123');
  $SERVICE_NAME_FIELD.val('EndServer');
  $DISPLAY_NAME_FIELD.val('Display&Name#Test');
  $INPUT.change();
  this.$('.generate-button-js .rsa-form-button').trigger('click');
  assert.ok(this.$('.display-name-input-js').hasClass('is-error'), 'Expected to have error class on display field');

  // Password is required
  $IP_FIELD.val('1.1.1.1');
  $PORT_FIELD.val('123');
  $SERVICE_NAME_FIELD.val('EndpointServer');
  $PASSWORD_FIELD.val('');
  $INPUT.change();
  this.$('.generate-button-js .rsa-form-button').trigger('click');
  assert.ok(this.$('.password-input-js').hasClass('is-error'), 'Expected to have error class on password field');
});

test('Protocol resets to default when reset button is clicked', function(assert) {
  new ReduxDataHelper(setState)
    .setData('defaultPackagerConfig', newConfig)
    .build();
  this.set('selectedProtocol', 'UDP');
  this.render(hbs`{{packager-form isLogCollectionEnabled=true selectedProtocol=selectedProtocol}}`);
  const $button = this.$('.reset-button .rsa-form-button');
  return waitFor(() => $button.trigger('click'))().then(() => {
    const protocol = this.get('selectedProtocol');
    assert.equal(protocol, 'TCP');
  });
});

test('Test log is set false on uncheck of checkbox', function(assert) {
  new ReduxDataHelper(setState)
    .setData('defaultPackagerConfig', newConfig)
    .build();
  this.set('testLog', 'true');
  this.render(hbs`{{packager-form isLogCollectionEnabled=true testLog=testLog}}`);
  const $button = this.$('.testLog .rsa-form-checkbox-label');
  return waitFor(() => $button.trigger('click'))().then(() => {
    const disableTestLog = this.get('testLog');
    assert.equal(disableTestLog, false);
  });
});

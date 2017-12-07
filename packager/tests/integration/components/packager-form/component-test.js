import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import waitFor from '../../../helpers/wait-for';
import { clickTrigger } from '../../../helpers/ember-power-select';
import $ from 'jquery';
import { applyPatch, revertPatch } from '../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../helpers/redux-data-helper';

let setState;

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
  }
});

const newConfig = {
  'packageConfig': {
    'id': '59894c9984518a5cfb8fbec2',
    'server': '10.101.34.245',
    'port': 443,
    'password': 'test',
    'serviceName': 'test',
    'displayName': 'test'
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

test('Protocol is empty on click of generate agent button', function(assert) {
  new ReduxDataHelper(setState)
    .setData('defaultPackagerConfig', newConfig)
    .setData('devices', devices)
    .build();
  this.render(hbs`{{packager-form isLogCollectionEnabled=true}}`);
  const $button = this.$('.generate-button-js .rsa-form-button');
  return waitFor(() => $button.trigger('click'))().then(() => {
    const $protocol = this.$('.protocol-js label');
    assert.ok($protocol.hasClass('is-error'));
  });
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
    assert.equal(this.$('.rsa-data-table-body-row').length, 2, 'There are 2 rows in the table');
  });
});

test('Channel filter null validation when generate agent button clicked', function(assert) {
  const channelFiltersWithNullData = {
    'packageConfig': {
      'id': '59894c9984518a5cfb8fbec2',
      'server': '10.101.34.245',
      'port': 443,
      'password': 'test'
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
      'password': 'test'
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

test('validates the packager config and sets the error field', function(assert) {
  assert.expect(4);
  new ReduxDataHelper(setState).setData('defaultPackagerConfig', newConfig).build();

  this.render(hbs`{{packager-form}}`);

  const $IP_FIELD = this.$('.server-input-js input');
  const $PORT_FIELD = this.$('.port-input-js input');
  const $SERVICE_NAME_FIELD = this.$('.service-name-input-js input');
  const $PASSWORD_FIELD = this.$('.password-input-js input');
  const $INPUT = this.$('.server-input-group input');

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

  // Password is required
  $IP_FIELD.val('1.1.1.1');
  $PORT_FIELD.val('123');
  $SERVICE_NAME_FIELD.val('EndpointServer');
  $PASSWORD_FIELD.val('');
  $INPUT.change();
  this.$('.generate-button-js .rsa-form-button').trigger('click');
  assert.ok(this.$('.password-input-js').hasClass('is-error'), 'Expected to have error class on password field');
});

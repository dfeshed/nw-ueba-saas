import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import DataHelper from '../../../helpers/data-helper';
import waitFor from '../../../helpers/wait-for';
import { clickTrigger } from '../../../helpers/ember-power-select';
import $ from 'jquery';


moduleForComponent('packager-form', 'Integration | Component | packager form', {
  integration: true,
  beforeEach() {
    this.inject.service('redux');
    this.registry.injection('component', 'i18n', 'service:i18n');
  }
});

const testData = {
  'packageConfig': {
    'id': '59894c9984518a5cfb8fbec2',
    'server': '10.101.34.245',
    'port': 443
  },
  'logCollectionConfig': {
    'configName': 'test'
  }
};
const testDevices = [{
  'id': 'id1',
  'name': 'ld11',
  'displayName': 'ld1',
  'host': '10.10.10.10',
  'port': 1234,
  'useTls': false,
  'version': null,
  'family': null,
  'meta': { }
}
];

test('it renders packager form', function(assert) {
  this.render(hbs`{{packager-form}}`);
  const $el = this.$('.packager-form');
  assert.equal($el.length, 1, 'Expected to find packager form root element in DOM.');
});

test('it renders form with saved data', function(assert) {
  new DataHelper(this.get('redux')).getConfig();
  this.render(hbs`{{packager-form}}`);
  // server
  const $el = this.$('.host-ip-js input');
  assert.equal($el.val(), '10.101.34.245', 'Expected to match the value "10.101.34.245" in DOM.');

  // service name
  const $serviceName = this.$('.service-name-js input');
  assert.equal($serviceName.val(), 'NWE Agent', 'Expected to match the value "NWE Agent" in DOM.');
});

test('Protocol is empty on click of generate agent button', function(assert) {
  const dataHelper = new DataHelper(this.get('redux'));
  dataHelper.getConfig(testData);
  dataHelper.getDevices(testDevices);
  this.render(hbs`{{packager-form isLogCollectionEnabled=true}}`);
  const $button = this.$('.generateButton-js .rsa-form-button');
  return waitFor(() => $button.trigger('click'))().then(() => {
    const $protocol = this.$('.protocol-js label');
    assert.ok($protocol.hasClass('is-error'));
  });
});

test('Primary decoder have values', function(assert) {
  const dataHelper = new DataHelper(this.get('redux'));
  dataHelper.getConfig(testData);
  dataHelper.getDevices(testDevices);
  this.render(hbs`{{packager-form isLogCollectionEnabled=true}}`);

  clickTrigger('.power-select:nth-child(1)');
  assert.equal($('li.ember-power-select-option').length, 1, 'There is 1 option available for LD/VLC');
}
);

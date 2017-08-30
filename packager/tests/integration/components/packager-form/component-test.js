import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import DataHelper from '../../../helpers/data-helper';

moduleForComponent('packager-form', 'Integration | Component | packager form', {
  integration: true,
  beforeEach() {
    this.inject.service('redux');
    this.registry.injection('component', 'i18n', 'service:i18n');
  }
});

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
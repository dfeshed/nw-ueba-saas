import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';
import engineResolverFor from '../../../../../../helpers/engine-resolver';

const item = {
  field: 'fileName',
  label: 'investigateHosts.process.fileName',
  value: 'ntoskrnl.exe',
  format: 'format1'
};


moduleForComponent('host-detail/process/summary-items/property', 'Integration | Component | endpoint host-detail/process/summary-items/property', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
  }
});

test('this is to test if property value is populated', function(assert) {
  this.set('item', item);
  this.render(hbs`{{host-detail/process/summary-items/property item=item}}`);
  return wait().then(() => {
    assert.equal(this.$('.header-item .value').length, 1, 'property value populated for corresponding summary item passed');
    assert.equal(this.$('.header-item').hasClass('col-xs-4 col-md-3'), true, 'has default column classes');
  });
});

test('test for hasBlock', function(assert) {
  this.set('item', item);
  this.render(hbs`
    {{#host-detail/process/summary-items/property item=item as |label value|}}
      <div class="value">{{label}}</div>
    {{/host-detail/process/summary-items/property}}
  `);
  return wait().then(() => {
    assert.equal(this.$('.header-item .value').text().trim(), 'investigateHosts.process.fileName', 'hasBlock property values populated for corresponding summary items passed');
  });
});

test('this is to test the custom class passed with item', function(assert) {
  const newItem = {
    field: 'fileName',
    label: 'investigateHosts.process.fileName',
    value: 'ntoskrnl.exe',
    format: 'format1',
    cssClass: 'custom-class'
  };
  this.set('item', newItem);
  this.render(hbs`{{host-detail/process/summary-items/property item=item}}`);
  return wait().then(() => {
    assert.equal(this.$('.header-item').hasClass('custom-class'), true, 'has custom class passed with item');
  });
});
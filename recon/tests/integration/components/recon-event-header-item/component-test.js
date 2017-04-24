import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('recon-event-header-item', 'Integration | Component | recon event header item', {
  integration: true
});

test('header item name and value render', function(assert) {
  this.set('name', 'nwService');
  this.set('value', 'concentrator');

  this.render(hbs`{{recon-event-header-item name=name value=value}}`);
  assert.equal(this.$('.name').first().text().trim(), 'NW Service');
  assert.equal(this.$('.value').first().text().trim(), 'concentrator');
});

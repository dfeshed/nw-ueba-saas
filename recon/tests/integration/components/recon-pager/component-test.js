import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('recon-pager', 'Integration | Component | recon pager', {
  integration: true
});

test('it renders for Packet view with the appropriate css class names', function(assert) {
  this.setProperties({
    eventIndex: 1,
    eventTotal: 1000,
    isPacket: true
  });
  this.render(hbs`{{recon-pager eventIndex=eventIndex eventTotal=eventTotal isPacket=isPacket}}`);

  assert.equal(this.$('.recon-pager').length, 1, 'Expected DOM with base CSS class');
  assert.equal(this.$().text().length, 191, 'Expected no extra text');
});

test('it renders for non Packet views with the appropriate css class names', function(assert) {
  this.setProperties({
    eventIndex: 1,
    eventTotal: 1000
  });
  this.render(hbs`{{recon-pager eventIndex=eventIndex eventTotal=eventTotal}}`);

  assert.equal(this.$('.recon-pager').length, 1, 'Expected DOM with base CSS class');
  assert.equal(this.$().text().length, 34, 'Expected extra text');
});

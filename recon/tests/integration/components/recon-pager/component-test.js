import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('recon-pager', 'Integration | Component | recon pager', {
  integration: true
});

test('it renders for Packet view with the appropriate css class names', function(assert) {
  this.setProperties({
    eventIndex: 1,
    eventTotal: 1000,
    packetCount: 100,
    packetTotal: 500,
    isPacket: true
  });
  this.render(hbs`{{recon-pager eventIndex=eventIndex eventTotal=eventTotal packetCount=packetCount packetTotal=packetTotal isPacket=isPacket}}`);

  assert.equal(this.$('.recon-pager').length, 1, 'Expected DOM with base CSS class');
  assert.equal(this.$().text().length, 96, 'Expected extra text');

  this.setProperties({
    packetCount: 500,
    packetTotal: 500
  });

  assert.equal(this.$().text().length, 74, 'Expected no extra text');

});

test('it renders for non Packet views with the appropriate css class names', function(assert) {
  this.setProperties({
    eventIndex: 1,
    eventTotal: 1000,
    packetCount: 100,
    packetTotal: 500
  });
  this.render(hbs`{{recon-pager eventIndex=eventIndex eventTotal=eventTotal packetCount=packetCount packetTotal=packetTotal}}`);

  assert.equal(this.$('.recon-pager').length, 1, 'Expected DOM with base CSS class');
  assert.equal(this.$().text().length, 23, 'Expected extra text');
});

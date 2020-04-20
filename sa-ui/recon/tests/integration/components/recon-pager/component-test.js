import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('recon-pager', 'Integration | Component | recon pager', {
  integration: true
});

test('it renders for Packet view with the appropriate css class names', function(assert) {
  assert.expect(2);
  this.setProperties({
    eventIndex: 1,
    eventTotal: 1000,
    isPacket: true
  });
  this.render(hbs`{{recon-pager eventIndex=eventIndex eventTotal=eventTotal isPacket=isPacket}}`);

  assert.equal(this.$('.recon-pager').length, 1, 'Expected DOM with base CSS class');
  assert.equal(this.$('.packet-pagination').length, 1, 'Packet Pagination controls expected');
});

test('it renders text view with the appropriate css class names', function(assert) {
  this.setProperties({
    eventIndex: 1,
    eventTotal: 1000,
    isText: true
  });
  this.render(hbs`{{recon-pager eventIndex=eventIndex eventTotal=eventTotal isText=isText}}`);
  assert.equal(this.$('.recon-pager').length, 1, 'Expected DOM with base CSS class');
  assert.equal(this.$('.text-pagination').length, 1, 'Text Pagination controls expected');
});

test('it renders formatted (comma delimited) Event Index and Event Count', function(assert) {
  this.setProperties({
    eventIndex: 1000,
    eventTotal: 10000,
    isText: true
  });
  this.render(hbs`{{recon-pager eventIndex=eventIndex eventTotal=eventTotal isText=isText}}`);
  assert.equal(this.$('.event-info').length, 1, 'Expected DOM with base CSS class');
  assert.equal(this.$('.event-info')[0].innerText.trim(), '1,000 of 10,000 events', 'Comma delimited counts');
});
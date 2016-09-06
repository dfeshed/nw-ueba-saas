import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('recon-meta-content-item', 'Integration | Component | recon meta content item', {
  integration: true
});

test('meta item name and value rendered', function(assert) {
  this.set('item', ['test-name', 'test-value']);


  this.render(hbs`{{recon-meta-content-item item=item}}`);

  assert.equal(this.$('.meta-name').text().trim(), 'test-name');
  assert.equal(this.$('.meta-value').text().trim(), 'test-value');
});

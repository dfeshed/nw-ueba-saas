import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('recon-pager', 'Integration | Component | recon pager', {
  integration: true
});

test('it renders with the appropriate css class names', function(assert) {
  this.setProperties({
    total: 10,
    pageSize: 100
  });
  this.render(hbs`{{recon-pager total=total pageSize=pageSize}}`);

  assert.equal(this.$('.recon-pager').length, 1, 'Expected DOM with base CSS class');
  assert.equal(this.$('.recon-pager.is-hidden').length, 1, 'Expected hidden class for small enough list');

  this.setProperties({
    total: 100,
    pageSize: 100
  });

  assert.equal(this.$('.recon-pager.is-hidden').length, 0, 'Expected no hidden class for a list that matches page size');

  this.setProperties({
    total: 101,
    pageSize: 100
  });

  assert.equal(this.$('.recon-pager.is-hidden').length, 0, 'Expected no hidden class for a list that exceeds page size');
  assert.ok(this.$().text().indexOf('1 - 100') > -1, 'Expected appropriate text in DOM');
});

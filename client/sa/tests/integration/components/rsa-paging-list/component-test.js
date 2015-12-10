import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('rsa-paging-list', 'Integration | Component | rsa paging list', {
  integration: true
});

test('it renders', function(assert) {
  assert.expect(6);

  this.set('data', [
      { id: 'item1' },
      { id: 'item2' },
      { id: 'item3' },
      { id: 'item4' },
      { id: 'item5' }
  ]);
  this.set('pageSize', 2);
  this.render(hbs`
  {{#rsa-paging-list pageSize=pageSize includeFooter=true records=data as |item|}}
    <li class='{{item.id}}'>Item</li>
  {{/rsa-paging-list}}
  `);

  assert.equal(this.$('.rsa-paging-list').length, 1, 'Unable to find component root DOM element.');
  assert.equal(this.$('.rsa-paging-list li').length, 2, 'Unexpected number of list items in first page.');

  let lastBtn = this.$('.rsa-paging-list .js-test-paging-list-last-btn');
  assert.equal(lastBtn.length, 1, 'Could not find last page button.');

  lastBtn.trigger('click');
  assert.equal(this.$('.rsa-paging-list li').length, 1, 'Unexpected number of list items in last page.');

  this.set('pageSize', 10);

  let firstBtn = this.$('.rsa-paging-list .js-test-paging-list-first-btn');
  assert.equal(firstBtn.length, 1, 'Could not find last page button.');

  firstBtn.trigger('click');
  assert.equal(this.$('.rsa-paging-list li').length, 5, 'Unexpected number of list items in an all-encompassing page.');

});

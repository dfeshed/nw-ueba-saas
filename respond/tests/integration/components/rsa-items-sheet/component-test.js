import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import wait from 'ember-test-helpers/wait';

moduleForComponent('rsa-items-sheet', 'Integration | Component | Items Sheet', {
  integration: true,
  resolver: engineResolverFor('respond')
});

const item1 = {
  id: 'id1',
  foo: 'foo1',
  bar: 'bar1'
};

const item2 = {
  id: 'id2',
  foo: 'foo2',
  bar: 'bar2'
};

test('it renders the table view initially when given a list of more than 1 item', function(assert) {
  this.setProperties({
    items: [ item1, item2 ],
    totalCount: 2
  });
  this.render(hbs`{{rsa-items-sheet items=items totalCount=totalCount}}`);
  return wait()
    .then(() => {
      const $el = this.$('.rsa-items-sheet');
      assert.equal($el.length, 1, 'Expected to find root DOM node');

      const $table = $el.find('.rsa-items-sheet__table-view');
      assert.equal($table.length, 1, 'Expected to find data table DOM node');

      const $details = $el.find('.rsa-items-sheet__details-view');
      assert.notOk($details.length, 'Expected to NOT find detais view DOM node');
    });
});

test('it renders the details view initially when given a list of only 1 item', function(assert) {
  this.setProperties({
    items: [ item1 ],
    totalCount: 1
  });
  this.render(hbs`{{rsa-items-sheet items=items totalCount=totalCount}}`);
  return wait()
    .then(() => {
      const $el = this.$('.rsa-items-sheet');
      assert.equal($el.length, 1, 'Expected to find root DOM node');

      const $table = $el.find('.rsa-items-sheet__table-view');
      assert.notOk($table.length, 'Expected to NOT find data table DOM node');

      const $details = $el.find('.rsa-items-sheet__details-view');
      assert.equal($details.length, 1, 'Expected to find detais view DOM node');
    });
});

test('it renders the table view initially when told there will be multiple items', function(assert) {
  this.setProperties({
    items: [ item1 ],
    totalCount: 2
  });
  this.render(hbs`{{rsa-items-sheet items=items totalCount=totalCount}}`);
  return wait()
    .then(() => {
      const $table = this.$('.rsa-items-sheet__table-view');
      assert.equal($table.length, 1, 'Expected to find data table DOM node');

      const $details = this.$('.rsa-items-sheet__details-view');
      assert.notOk($details.length, 'Expected to NOT find detais view DOM node');
    });
});

test('it preserves the selected item when the items list is reset to items that does include the selected item', function(assert) {
  const item3 = { id: 3 };

  this.setProperties({
    items: [ item1, item2 ],
    totalCount: 2,
    selectedItem: item2
  });
  this.render(hbs`{{rsa-items-sheet items=items totalCount=totalCount selectedItem=selectedItem}}`);
  return wait()
    .then(() => {
      const $table = this.$('.rsa-items-sheet__table-view');
      assert.notOk($table.length, 'Expected to not find data table DOM node');

      const $details = this.$('.rsa-items-sheet__details-view');
      assert.ok($details.length, 'Expected to find detais view DOM node');

      this.setProperties({
        items: [ item2, item3 ],
        totalCount: 2
      });
      return wait();
    })
    .then(() => {
      assert.equal(this.get('selectedItem'), item2, 'Expected selectedItem to be preserved');
    });
});
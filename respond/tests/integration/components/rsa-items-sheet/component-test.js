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
  this.set('items', [ item1, item2 ]);
  this.render(hbs`{{rsa-items-sheet items=items}}`);
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
  this.set('items', [ item1 ]);
  this.render(hbs`{{rsa-items-sheet items=items}}`);
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

test('it renders the table view initially if itemStatus is still awaiting data', function(assert) {
  this.setProperties({
    items: [ item1 ],
    itemsStatus: 'streaming'
  });
  this.render(hbs`{{rsa-items-sheet items=items itemsStatus=itemsStatus}}`);
  return wait()
    .then(() => {
      const $table = this.$('.rsa-items-sheet__table-view');
      assert.equal($table.length, 1, 'Expected to find data table DOM node while awaiting data');

      const $details = this.$('.rsa-items-sheet__details-view');
      assert.notOk($details.length, 'Expected to NOT find detais view DOM node while awaiting data');

      // Simulate the end of the data stream.
      this.set('itemsStatus', 'complete');
      return wait();
    })
    .then(() => {
      const $table = this.$('.rsa-items-sheet__table-view');
      assert.notOk($table.length, 'Expected to NOT find data table DOM node after data is done arriving');

      const $details = this.$('.rsa-items-sheet__details-view');
      assert.equal($details.length, 1, 'Expected to find detais view DOM node after data is done arriving');
    });
});
import EmberObject from '@ember/object';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import wait from 'ember-test-helpers/wait';
import ComputesRowViewport from 'respond/mixins/group-table/computes-row-viewport';
import ComputesColumnExtents from 'respond/mixins/group-table/computes-column-extents';
import HasSelections from 'respond/mixins/group-table/has-selections';

moduleForComponent('rsa-group-table-group', 'Integration | Component | rsa group table group', {
  integration: true,
  resolver: engineResolverFor('respond')
});

const group = {
  id: 'id1',
  value: 'foo',
  items: [ 'a', 'b', 'c' ]
};

const index = 1;

const top = 100;

const MockTableClass = EmberObject.extend(ComputesRowViewport, ComputesColumnExtents, HasSelections);

const table = MockTableClass.create();

const initialState = {
  group,
  index,
  top,
  table
};

test('it renders and applies the correct top to its DOM node', function(assert) {

  this.setProperties(initialState);

  this.render(hbs`{{rsa-group-table/group
    group=group
    index=index
    top=top
    table=table
  }}`);

  return wait()
    .then(() => {
      const cell = this.$('.rsa-group-table-group');
      assert.ok(cell.length, 'Expected to find root DOM node');
      assert.equal(parseInt(cell.css('top'), 10), top, 'Expected initial top to be applied to DOM');

      this.set('top', top * 2);
      return wait();
    })
    .then(() => {
      const cell = this.$('.rsa-group-table-group');
      assert.equal(parseInt(cell.css('top'), 10), top * 2, 'Expected top to be updated in DOM');
    });
});

test('it applies the correct CSS class name when selected', function(assert) {

  table.set('selections', { areGroups: true, ids: [] });
  this.setProperties(initialState);

  this.render(hbs`{{rsa-group-table/group
    group=group
    index=index
    top=top
    table=table
  }}`);

  return wait()
    .then(() => {
      const row = this.$('.rsa-group-table-group');
      assert.notOk(row.hasClass('is-selected'));
      table.get('selections.ids').pushObject(group.id);

      return wait();
    }).then(() => {

      const row = this.$('.rsa-group-table-group');
      assert.ok(row.hasClass('is-selected'));
    });
});

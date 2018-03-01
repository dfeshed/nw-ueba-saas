import EmberObject from '@ember/object';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import wait from 'ember-test-helpers/wait';

moduleForComponent('rsa-group-table', 'Integration | Component | rsa group table', {
  integration: true,
  resolver: engineResolverFor('respond')
});

const columnsConfig = [{
  field: 'foo',
  width: 75
}, {
  field: 'bar',
  width: 50
}];

// Generate mock groups with items.
const groupsCount = 2;
const groupItemsCount = 10;
const groups = [];

(function() {
  let i;
  for (i = 0; i < groupsCount; i++) {
    const items = [];
    let j;
    for (j = 0; j < groupItemsCount; j++) {
      items.push({
        id: `${i}.${j}`,
        foo: `foo${i}.${j}`,
        bar: `bar${i}.${j}`
      });
    }
    groups.push(EmberObject.create({
      value: i,
      items
    }));
  }
})();

test('it renders itself and its contextual components', function(assert) {

  this.setProperties({
    groups,
    columnsConfig
  });

  this.render(hbs`
    {{#rsa-group-table
      lazy=false
      groups=groups
      columnsConfig=columnsConfig
      as |table|
    }}
      {{table.header}}
      {{table.body}}
    {{/rsa-group-table}}
  `);

  return wait().then(() => {
    assert.equal(this.$('.rsa-group-table').length, 1, 'Expected to find root DOM node.');
    assert.equal(this.$('.rsa-group-table-column-headers').length, 1, 'Expected to find header root DOM node.');
    assert.equal(this.$('.rsa-group-table-body').length, 1, 'Expected to find body root DOM node.');
  });

});

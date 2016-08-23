import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('rsa-investigate/meta/groups-panel', 'Integration | Component | rsa investigate/meta/groups panel', {
  integration: true
});

test('it renders', function(assert) {
  const groups = [{
    id: 1,
    name: 'Group 1',
    type: 'group'
  }, {
    id: 2,
    name: 'Group 2',
    type: 'group'
  }];
  this.set('groups', groups);
  this.render(hbs`{{rsa-investigate/meta/groups-panel groups=groups}}`);

  assert.equal(this.$('.rsa-investigate-meta-groups-panel').length, 1);
  assert.equal(this.$('li').length, groups.length, 'Expected one list item for each group.');
});

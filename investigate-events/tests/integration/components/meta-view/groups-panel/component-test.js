import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';

moduleForComponent('meta-view/groups-panel', 'Integration | Component | meta-view/groups panel', {
  integration: true,
  resolver: engineResolverFor('investigate-events')
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
  this.render(hbs`{{meta-view/groups-panel groups=groups}}`);

  assert.equal(this.$('.rsa-investigate-meta-groups-panel').length, 1);
  assert.equal(this.$('li').length, groups.length, 'Expected one list item for each group.');
});

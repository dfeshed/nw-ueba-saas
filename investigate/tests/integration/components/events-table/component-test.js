import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';

moduleForComponent('events-table', 'Integration | Component | events table', {
  integration: true,
  resolver: engineResolverFor('investigate')
});

test('it renders', function(assert) {
  this.render(hbs`{{events-table}}`);

  assert.equal(this.$('.rsa-investigate-events-table').length, 1);
});

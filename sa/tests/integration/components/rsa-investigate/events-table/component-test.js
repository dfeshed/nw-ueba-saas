import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('rsa-investigate/events-table', 'Integration | Component | rsa investigate/events table', {
  integration: true
});

test('it renders', function(assert) {
  this.render(hbs`{{rsa-investigate/events-table}}`);

  assert.equal(this.$('.rsa-investigate-events-table').length, 1);
});

import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('events-container', 'Integration | Component | events container', {
  integration: true
});

test('it renders', function(assert) {
  this.render(hbs`{{events-container}}`);
  assert.equal(this.$().text().trim(), '');
});

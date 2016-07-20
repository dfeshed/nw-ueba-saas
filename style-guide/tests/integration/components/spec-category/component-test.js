import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('spec-category', 'Integration | Component | spec body category', {
  integration: true
});

test('it renders', function(assert) {

  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });" + EOL + EOL +

  this.render(hbs`{{spec-category}}`);

  assert.ok(this.$('.spec-category').length, 'Could not find component\'s root DOM element.');
});

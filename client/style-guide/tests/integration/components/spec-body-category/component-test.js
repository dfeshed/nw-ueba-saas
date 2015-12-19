import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('spec-body-category', 'Integration | Component | spec body category', {
  integration: true
});

test('it renders', function(assert) {

  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });" + EOL + EOL +

  this.render(hbs`{{spec-body-category}}`);

  assert.ok(this.$('.spec-body-category').length, 'Could not find component\'s root DOM element.');
});

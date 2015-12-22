import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('hbs-example', 'Integration | Component | hbs example', {
  integration: true
});

test('it renders', function(assert) {

  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });" + EOL + EOL +

  this.render(hbs`{{hbs-example}}`);

  assert.ok(this.$('.hbs-example').length, 'Could not find component\'s root DOM element.');
});

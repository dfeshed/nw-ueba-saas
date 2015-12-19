import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('hbs-preview', 'Integration | Component | hbs preview', {
  integration: true
});

test('it renders', function(assert) {

  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });" + EOL + EOL +

  this.render(hbs`{{hbs-preview code="<h1>Hello world</h1>"}}`);

  assert.ok(this.$('.hbs-preview').length, 'Could not find component\'s root DOM element.');
});

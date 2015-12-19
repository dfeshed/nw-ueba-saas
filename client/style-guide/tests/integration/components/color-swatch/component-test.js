import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('color-swatch', 'Integration | Component | color swatch', {
  integration: true
});

test('it renders', function(assert) {

  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });" + EOL + EOL +

  this.render(hbs`{{color-swatch}}`);

  assert.ok(this.$('.color-swatch').length, 'Could not find component\'s root DOM element.');
});

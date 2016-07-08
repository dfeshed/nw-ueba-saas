import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('spec-doc', 'Integration | Component | spec doc', {
  integration: true
});

test('it renders', function(assert) {

  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });" + EOL + EOL +

  this.render(hbs`{{spec-doc}}`);

  assert.ok(this.$('.spec-doc').length, 'Could not find component\'s root DOM element.');
});

import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('spec-masthead', 'Integration | Component | spec masthead', {
  integration: true
});

test('it renders', function(assert) {

  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });" + EOL + EOL +

  this.render(hbs`{{spec-masthead}}`);

  assert.ok(this.$('.spec-masthead').length, 'Could not find component\'s root DOM element.');
});

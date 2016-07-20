import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('spec-member', 'Integration | Component | spec member', {
  integration: true
});

test('it renders', function(assert) {

  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });" + EOL + EOL +

  this.render(hbs`{{spec-member}}`);

  assert.ok(this.$('.spec-member').length, 'Could not find component\'s root DOM element.');
});

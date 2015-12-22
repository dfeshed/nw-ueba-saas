import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('spec-body-member', 'Integration | Component | spec body member', {
  integration: true
});

test('it renders', function(assert) {

  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });" + EOL + EOL +

  this.render(hbs`{{spec-body-member}}`);

  assert.ok(this.$('.spec-body-member').length, 'Could not find component\'s root DOM element.');
});

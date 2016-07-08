import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('svg-clipboard', 'Integration | Component | svg clipboard', {
  integration: true
});

test('it renders', function(assert) {

  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });" + EOL + EOL +

  this.render(hbs`{{svg-clipboard}}`);

  assert.ok(this.$('.svg-clipboard').length, 'Could not find component\'s root DOM element.');
});

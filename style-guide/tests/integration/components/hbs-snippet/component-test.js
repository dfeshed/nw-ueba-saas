import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('hbs-snippet', 'Integration | Component | hbs snippet', {
  integration: true
});

test('it renders', function(assert) {

  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });" + EOL + EOL +

  this.set('myCode', '<h1>Hello world</h1>');
  this.render(hbs`{{hbs-snippet code=myCode}}`);

  assert.ok(this.$('.hbs-snippet').length, 'Could not find component\'s root DOM element.');
  assert.ok(this.$().text().indexOf('Hello world') > -1, 'Could not find component\'s snippet DOM element.');
});

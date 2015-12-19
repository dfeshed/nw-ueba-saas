import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('spec-nav', 'Integration | Component | spec nav', {
  integration: true
});

test('it renders', function(assert) {

  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });" + EOL + EOL +
  this.set('myModel', { categories: [{ id: 1, title: 'Title 1' }] });
  this.render(hbs`{{spec-nav model=myModel}}`);

  assert.ok(this.$('.spec-nav').length, 'Could not find component\'s root DOM element.');
});

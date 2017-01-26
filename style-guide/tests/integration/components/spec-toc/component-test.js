import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('spec-toc', 'Integration | Component | spec toc', {
  integration: true
});

test('it renders', function(assert) {

  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });

  this.render(hbs`{{spec-toc}}`);

  assert.ok(this.$('.spec-toc').length, 'Could not find component\'s root DOM element.');
});

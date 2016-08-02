import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('recon-event-titlebar', 'Integration | Component | recon event titlebar', {
  integration: true
});

test('it renders', function(assert) {
  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });

  this.render(hbs`{{recon-event-titlebar}}`);

  assert.equal(this.$().text().trim(), 'Event Reconstruction');
});

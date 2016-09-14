import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('recon-event-detail-files', 'Integration | Component | recon event detail files', {
  integration: true
});

test('it renders', function(assert) {
  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });

  this.render(hbs`{{recon-event-detail/files}}`);
  assert.equal(this.$().text().trim(), 'FILE RECON GOES HERE');
});

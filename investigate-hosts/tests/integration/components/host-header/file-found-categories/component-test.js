import { moduleForComponent, skip } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('host-detail/explore/file-found-categories', 'Integration | Component | endpoint host detail/file found categories', {
  integration: true
});

skip('Testing file-found-categories component', function(assert) {

  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });

  this.render(hbs`{{host-detail/explore/file-found-categories file=file}}`);

  assert.equal(this.$().text().trim(), '');

  assert.equal(this.$().text().trim(), 'template block text');
});

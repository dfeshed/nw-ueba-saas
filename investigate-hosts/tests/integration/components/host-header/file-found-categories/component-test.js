import { module, skip } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | endpoint host detail/file found categories', function(hooks) {
  setupRenderingTest(hooks);

  skip('Testing file-found-categories component', function(assert) {

    // Set any properties with this.set('myProperty', 'value');
    // Handle any actions with this.on('myAction', function(val) { ... });

    this.render(hbs`{{host-detail/explore/file-found-categories file=file}}`);

    assert.equal(find('*').textContent.trim(), '');

    assert.equal(find('*').textContent.trim(), 'template block text');
  });
});

import { module, skip } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | endpoint host header', function(hooks) {
  setupRenderingTest(hooks);

  skip('it renders', function(assert) {

    // Set any properties with this.set('myProperty', 'value');
    // Handle any actions with this.on('myAction', function(val) { ... });

    this.render(hbs`{{host-header}}`);

    assert.equal(find('*').textContent.trim(), '');

    // Template block usage:
    this.render(hbs`
      {{#host-header}}
        template block text
      {{/host-header}}
    `);

    assert.equal(find('*').textContent.trim(), 'template block text');
  });
});

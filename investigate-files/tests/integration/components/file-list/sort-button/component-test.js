import { module, skip } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | file list/sort button', function(hooks) {
  setupRenderingTest(hooks);

  skip('it renders', function(assert) {

    // Set any properties with this.set('myProperty', 'value');
    // Handle any actions with this.on('myAction', function(val) { ... });

    this.render(hbs`{{file-list/sort-button}}`);

    assert.equal(find('*').textContent.trim(), '');

    // Template block usage:
    this.render(hbs`
      {{#file-list/sort-button}}
        template block text
      {{/file-list/sort-button}}
    `);

    assert.equal(find('*').textContent.trim(), 'template block text');
  });
});

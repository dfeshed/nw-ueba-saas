import { moduleForComponent, skip } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('file-list/sort-button', 'Integration | Component | file list/sort button', {
  integration: true
});

skip('it renders', function(assert) {

  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });

  this.render(hbs`{{file-list/sort-button}}`);

  assert.equal(this.$().text().trim(), '');

  // Template block usage:
  this.render(hbs`
    {{#file-list/sort-button}}
      template block text
    {{/file-list/sort-button}}
  `);

  assert.equal(this.$().text().trim(), 'template block text');
});

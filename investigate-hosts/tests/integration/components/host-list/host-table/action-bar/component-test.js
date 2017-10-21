import { moduleForComponent, skip } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('host-list/host-table/action-bar', 'Integration | Component | endpoint host list/host table/action bar', {
  integration: true
});

skip('it renders', function(assert) {

  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });

  this.render(hbs`{{host-list/host-table/action-bar}}`);

  assert.equal(this.$().text().trim(), '');

  // Template block usage:
  this.render(hbs`
    {{#host-list/host-table/action-bar}}
      template block text
    {{/host-list/host-table/action-bar}}
  `);

  assert.equal(this.$().text().trim(), 'template block text');
});

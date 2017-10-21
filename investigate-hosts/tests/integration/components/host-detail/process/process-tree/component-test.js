import { moduleForComponent, skip } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('host-detail/process/process-tree', 'Integration | Component | endpoint host detail/process/process tree', {
  integration: true
});

skip('it renders', function(assert) {

  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });

  this.render(hbs`{{host-detail/process/process-tree}}`);

  assert.equal(this.$().text().trim(), '');

  // Template block usage:
  this.render(hbs`
    {{#host-detail/process/process-tree}}
      template block text
    {{/host-detail/process/process-tree}}
  `);

  assert.equal(this.$().text().trim(), 'template block text');
});

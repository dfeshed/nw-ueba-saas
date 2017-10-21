import { moduleForComponent, skip } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('host-detail/base-property-panel/property-name', 'Integration | Component | endpoint property panel/property name', {
  integration: true
});

skip('it renders', function(assert) {

  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });

  this.render(hbs`{{host-detail/base-property-panel/property-name}}`);

  assert.equal(this.$().text().trim(), '');

  // Template block usage:
  this.render(hbs`
    {{#host-detail/base-property-panel/property-name}}
      template block text
    {{/host-detail/base-property-panel/property-name}}
  `);

  assert.equal(this.$().text().trim(), 'template block text');
});

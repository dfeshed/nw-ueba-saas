import { moduleForComponent, skip } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('property-panel', 'Integration | Component | endpoint property panel', {
  integration: true
});

skip('it renders', function(assert) {

  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });

  this.render(hbs`{{property-panel}}`);

  assert.equal(this.$().text().trim(), '');

  // Template block usage:
  this.render(hbs`
    {{#property-panel}}
      template block text
    {{/property-panel}}
  `);

  assert.equal(this.$().text().trim(), 'template block text');
});

import { moduleForComponent, skip } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('host-detail/overview', 'Integration | Component | endpoint host detail/overview', {
  integration: true
});

skip('it renders', function(assert) {

  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });

  this.render(hbs`{{host-detail/overview}}`);

  assert.equal(this.$().text().trim(), '');

  // Template block usage:
  this.render(hbs`
    {{#host-detail/overview}}
      template block text
    {{/host-detail/overview}}
  `);

  assert.equal(this.$().text().trim(), 'template block text');
});

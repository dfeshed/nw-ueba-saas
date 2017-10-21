import { moduleForComponent, skip } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('host-detail/header/titlebar', 'Integration | Component | endpoint host titlebar', {
  integration: true
});

skip('it renders', function(assert) {

  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });

  this.render(hbs`{{host-detail/header/titlebar}}`);

  assert.equal(this.$().text().trim(), '');

  // Template block usage:
  this.render(hbs`
    {{#host-detail/header/titlebar}}
      template block text
    {{/host-detail/header/titlebar}}
  `);

  assert.equal(this.$().text().trim(), 'template block text');
});

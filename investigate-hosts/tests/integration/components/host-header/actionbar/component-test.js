import { moduleForComponent, skip } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('host-detail/header/actionbar', 'Integration | Component | endpoint host actionbar', {
  integration: true
});

skip('it renders', function(assert) {

  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });

  this.render(hbs`{{host-detail/header/actionbar}}`);

  assert.equal(this.$().text().trim(), '');

  // Template block usage:
  this.render(hbs`
    {{#host-detail/header/actionbar}}
      template block text
    {{/host-detail/header/actionbar}}
  `);

  assert.equal(this.$().text().trim(), 'template block text');
});

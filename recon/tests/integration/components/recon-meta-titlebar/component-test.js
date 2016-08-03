import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('recon-meta-titlebar', 'Integration | Component | recon meta titlebar', {
  integration: true
});

test('it renders', function(assert) {
  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });

  this.render(hbs`{{recon-meta-titlebar}}`);

  assert.equal(this.$().text().trim(), '');

  // Template block usage:
  this.render(hbs`
    {{#recon-meta-titlebar}}
      template block text
    {{/recon-meta-titlebar}}
  `);

  assert.equal(this.$().text().trim(), 'template block text');
});

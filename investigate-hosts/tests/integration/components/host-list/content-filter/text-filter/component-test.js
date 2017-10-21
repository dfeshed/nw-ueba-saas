import { moduleForComponent, skip } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('host-list/content-filter/text-filter', 'Integration | Component | host list/content filter/text filter', {
  integration: true
});

skip('it renders', function(assert) {
  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });

  this.render(hbs`{{host-list/content-filter/text-filter}}`);

  assert.equal(this.$().text().trim(), '');

  // Template block usage:
  this.render(hbs`
    {{#host-list/content-filter/text-filter}}
      template block text
    {{/host-list/content-filter/text-filter}}
  `);

  assert.equal(this.$().text().trim(), 'template block text');
});

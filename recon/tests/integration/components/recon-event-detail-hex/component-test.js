import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('recon-event-detail-hex', 'Integration | Component | recon event detail hex', {
  integration: true
});

test('it renders', function(assert) {
  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });

  this.render(hbs`{{recon-event-detail-hex}}`);

  assert.equal(this.$().text().trim(), '');

  // Template block usage:
  this.render(hbs`
    {{#recon-event-detail-hex}}
      template block text
    {{/recon-event-detail-hex}}
  `);

  assert.equal(this.$().text().trim(), 'template block text');
});

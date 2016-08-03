import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('recon-event-header-group', 'Integration | Component | recon event header group', {
  integration: true
});

test('it renders', function(assert) {
  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });

  this.render(hbs`{{recon-event-header-group}}`);

  assert.equal(this.$().text().trim(), '');

  // Template block usage:
  this.render(hbs`
    {{#recon-event-header-group}}
      template block text
    {{/recon-event-header-group}}
  `);

  assert.equal(this.$().text().trim(), 'template block text');
});

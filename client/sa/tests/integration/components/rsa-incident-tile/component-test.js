import { moduleForComponent } from 'ember-qunit';
import { skip } from 'qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('rsa-incident-tile', 'Integration | Component | rsa incident tile', {
  integration: true
});

skip('it renders', function(assert) {

  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });" + EOL + EOL +

  this.render(hbs`{{rsa-incident-tile}}`);

  assert.equal(this.$().text().trim(), '');

  // Template block usage:" + EOL +
  this.render(hbs`
    {{#rsa-incident-tile}}
      template block text
    {{/rsa-incident-tile}}
  `);

  assert.equal(this.$().text().trim(), 'template block text');
});

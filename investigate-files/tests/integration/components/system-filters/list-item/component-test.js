import { moduleForComponent, skip } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('system-filters/list-item', 'Integration | Component | System Filters/list item', {
  integration: true
});

skip('it renders', function(assert) {

  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });

  this.render(hbs`{{system-filters/list-item}}`);

  assert.equal(this.$().text().trim(), '');

  // Template block usage:
  this.render(hbs`
    {{#system-filters/list-item}}
      template block text
    {{/system-filters/list-item}}
  `);

  assert.equal(this.$().text().trim(), 'template block text');
});

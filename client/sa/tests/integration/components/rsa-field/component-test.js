import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('rsa-field', 'Integration | Component | rsa field', {
  integration: true
});

test('it renders', function(assert) {
  assert.expect(2);

  this.render(hbs`{{rsa-field}}`);
  assert.ok(this.$('.rsa-field').length);

  // Template block usage:
  this.render(hbs`
  {{#rsa-field class='rsa-field-test2'}}
    template block text
  {{/rsa-field}}
  `);

  assert.equal(this.$('.rsa-field-test2 span').text().trim(), 'template block text');
});

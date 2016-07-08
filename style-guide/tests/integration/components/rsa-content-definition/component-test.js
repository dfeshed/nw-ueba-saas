import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('/rsa-content-definition', 'Integration | Component | rsa-content-definition', {
  integration: true
});

test('it includes the proper classes', function(assert) {
  this.render(hbs `{{rsa-content-definition}}`);
  let contentCount = this.$().find('.rsa-content-definition').length;
  assert.equal(contentCount, 1);
});

test('it includes the proper inner element', function(assert) {
  this.render(hbs `{{#rsa-content-definition}}
  <p class='inner-element-class'>something</p>
  {{/rsa-content-definition}}`);
  let contentCount = this.$().find('.inner-element-class').length;
  assert.equal(contentCount, 1, 'Checking inner content is displayed');
});
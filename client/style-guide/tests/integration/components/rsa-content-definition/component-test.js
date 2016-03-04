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

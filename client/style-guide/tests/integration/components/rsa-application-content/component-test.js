import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('/rsa-application-content', 'Integration | Component | rsa-application-content', {
  integration: true
});

test('it includes the proper classes', function(assert) {
  this.render(hbs `{{rsa-application-content}}`);
  let content = this.$().find('.rsa-application-content').length;
  assert.equal(content, 1);
});

import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('/rsa-content-section-header', 'Integration | Component | rsa-content-section-header', {
  integration: true
});

test('it includes the proper classes', function(assert) {
  this.render(hbs `{{rsa-content-section-header label='foo'}}`);
  let header = this.$().find('.rsa-content-section-header').length;
  assert.equal(header, 1);
});

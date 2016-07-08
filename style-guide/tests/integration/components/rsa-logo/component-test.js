import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('/rsa-logo', 'Integration | Component | rsa-logo', {
  integration: true
});

test('it renders', function(assert) {
  assert.expect(1);

  this.render(hbs`{{rsa-logo}}`);

  assert.equal(this.$('.rsa-logo').length, 1, 'Could not find the component root DOM element.');
});

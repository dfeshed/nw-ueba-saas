import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('rsa-live/rsa-wizard/progress', 'Integration | Component | rsa wizard/progress', {
  integration: true
});

test('it renders', function(assert) {
  this.render(hbs`{{rsa-live/rsa-wizard/progress}}`);

  assert.equal(this.$('.rsa-wizard-progress').length, 1);
});

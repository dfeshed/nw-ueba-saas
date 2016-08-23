import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('rsa-investigate/meta/values-panel', 'Integration | Component | rsa investigate/meta/values panel', {
  integration: true
});

test('it renders', function(assert) {

  this.render(hbs`{{rsa-investigate/meta/values-panel}}`);

  assert.equal(this.$('.rsa-investigate-meta-values-panel').length, 1);
});

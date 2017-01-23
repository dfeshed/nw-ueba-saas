import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';

moduleForComponent('m-meta/values-panel', 'Integration | Component | m-meta/values panel', {
  integration: true,
  resolver: engineResolverFor('investigate')
});

test('it renders', function(assert) {

  this.render(hbs`{{m-meta/values-panel}}`);

  assert.equal(this.$('.rsa-investigate-meta-values-panel').length, 1);
});

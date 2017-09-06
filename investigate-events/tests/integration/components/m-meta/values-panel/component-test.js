import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';

moduleForComponent('meta-view/values-panel', 'Integration | Component | meta-view/values panel', {
  integration: true,
  resolver: engineResolverFor('investigate-events')
});

test('it renders', function(assert) {

  this.render(hbs`{{meta-view/values-panel}}`);

  assert.equal(this.$('.rsa-investigate-meta-values-panel').length, 1);
});

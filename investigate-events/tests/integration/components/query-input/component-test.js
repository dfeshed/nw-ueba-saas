import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';

moduleForComponent('query-input', 'Integration | Component | query-input', {
  integration: true,
  resolver: engineResolverFor('investigate-events')
});

test('it renders', function(assert) {
  this.render(hbs`{{query-input}}`);
  assert.equal(this.$().text().trim(), '');

  // Just test that it can render for now.
  // There will be lots to test once the auto-complete features are implememted.
});

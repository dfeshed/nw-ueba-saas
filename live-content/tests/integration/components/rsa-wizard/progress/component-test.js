import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';

moduleForComponent('rsa-wizard/progress', 'Integration | Component | rsa wizard/progress', {
  integration: true,
  resolver: engineResolverFor('live-content')
});

test('it renders', function(assert) {
  this.render(hbs`{{rsa-wizard/progress}}`);

  assert.equal(this.$('.rsa-wizard-progress').length, 1);
});

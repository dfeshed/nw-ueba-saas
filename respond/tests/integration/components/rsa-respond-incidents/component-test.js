import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';

moduleForComponent('rsa-respond-incidents', 'Integration | Component | Respond Incidents', {
  integration: true,
  resolver: engineResolverFor('respond')
});

test('The Incidents component renders to the DOM', function(assert) {
  assert.expect(1);
  this.render(hbs`{{rsa-respond-incidents}}`);
  assert.equal(this.$('.rsa-respond-incidents').length, 1, 'The Incidents component should be found in the DOM');
});
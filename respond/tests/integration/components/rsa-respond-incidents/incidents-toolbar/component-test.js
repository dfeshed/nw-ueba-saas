import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';

moduleForComponent('rsa-respond-incidents/incidents-toolbar', 'Integration | Component | Respond Incidents Toolbar', {
  integration: true,
  resolver: engineResolverFor('respond')
});

test('The Incidents toolbar renders to the DOM', function(assert) {
  assert.expect(1);
  this.render(hbs`{{rsa-respond-incidents/incidents-toolbar}}`);
  assert.equal(this.$('.rsa-respond-incidents-toolbar').length, 1, 'The Incidents toolbar should be found in the DOM');
});
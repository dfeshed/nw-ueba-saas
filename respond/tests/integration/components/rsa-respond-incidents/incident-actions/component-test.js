import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';

moduleForComponent('rsa-respond-incidents/incidents-toolbar', 'Integration | Component | Respond Incidents Actions Drawer', {
  integration: true,
  resolver: engineResolverFor('respond')
});

test('The Incidents action drawer renders to the DOM with three action buttons', function(assert) {
  this.render(hbs`{{rsa-respond-incidents/incident-actions}}`);
  assert.equal(this.$('.incident-actions').length, 1, 'The Incidents action drawer should be found in the DOM');
  assert.equal(this.$('.incident-action-button').length, 3, 'The drawer has three incident action buttons');
});
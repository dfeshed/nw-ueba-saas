import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';

moduleForComponent('rsa-respond-incidents', 'Integration | Component | Respond Incidents Filters', {
  integration: true,
  resolver: engineResolverFor('respond'),
  beforeEach() {
    // TODO figure out what to specifically inject into, rather than all components
    this.registry.injection('component', 'i18n', 'service:i18n');
  }
});

test('The Incidents Filters component renders to the DOM', function(assert) {
  assert.expect(1);
  this.render(hbs`{{rsa-respond-incidents/incidents-filters}}`);
  assert.equal(this.$('.incidents-filters').length, 1, 'The Incidents Filters component should be found in the DOM');
});
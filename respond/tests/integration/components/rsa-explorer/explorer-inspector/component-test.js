import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';

moduleForComponent('rsa-alerts', 'Integration | Component | Explorer Inspector', {
  integration: true,
  resolver: engineResolverFor('respond'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
  }
});

test('The explorer/explorer-inspector component renders to the DOM', function(assert) {
  this.render(hbs`{{rsa-explorer/explorer-inspector}}`);
  assert.equal(this.$('.rsa-explorer-inspector').length, 1, 'The explorer-inspector component should be found in the DOM');
  assert.equal(this.$('.back a').length, 0, 'There is NO back link');
});

test('it creates a back-to-route link', function(assert) {
  this.render(hbs`{{rsa-explorer/explorer-inspector backToRouteText='Back to before' backToRouteName='before' }}`);
  assert.equal(this.$('.rsa-explorer-inspector').length, 1, 'The explorer-inspector component should be found in the DOM');
  assert.equal(this.$('.back a').length, 1, 'There is a back link');
  assert.equal(this.$('.back a i[title="Back to before"]').length, 1, 'The back link has an icon with the backToRoute text as the title');
});

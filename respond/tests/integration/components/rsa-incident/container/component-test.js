import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

moduleForComponent('rsa-incident-container', 'Integration | Component | Incident Container', {
  integration: true,
  resolver: engineResolverFor('respond'),
  setup() {
    this.inject.service('redux');
  }
});

test('it renders', function(assert) {
  this.render(hbs`{{rsa-incident/container}}`);
  const $el = this.$('.rsa-incident-container');
  assert.equal($el.length, 1, 'Expected to find overview root element in DOM.');
});
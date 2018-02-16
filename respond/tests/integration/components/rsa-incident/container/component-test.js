import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';

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
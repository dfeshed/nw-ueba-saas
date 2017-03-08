import { moduleForComponent, skip } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';

moduleForComponent('rsa-incident-container', 'Integration | Component | Incident Container', {
  integration: true,
  resolver: engineResolverFor('respond'),
  setup() {
    this.inject.service('redux');
  }
});

// @workaround We skip this integration test for now because there is an issue in ember-engines with integration
// tests for components that use a `{{link-to}}`.  The fix is in ember-engines 0.5.0-beta2 so we should be able to
// stop skipping this test once we upgrade.
// @see https://github.com/ember-engines/ember-engines/issues/294 regarding the issue and
// https://github.com/ember-engines/ember-engines/pull/295 for the PR that fixes it
skip('it renders - to test, please upgrade to ember-engines 0.5.0-beta2+', function(assert) {
  this.render(hbs`{{rsa-incident/container}}`);
  const $el = this.$('.rsa-incident-container');
  assert.equal($el.length, 1, 'Expected to find overview root element in DOM.');
});
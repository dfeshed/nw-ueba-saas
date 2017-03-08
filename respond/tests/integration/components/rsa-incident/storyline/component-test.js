import { moduleForComponent, skip } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import wait from 'ember-test-helpers/wait';
import DataHelper from '../../../../helpers/data-helper';

moduleForComponent('rsa-incident-storyline', 'Integration | Component | Incident Storyline', {
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
  new DataHelper(this.get('redux')).fetchIncidentStoryline();
  this.render(hbs`{{rsa-incident/storyline}}`);
  return wait().then(() => {
    const $el = this.$('.rsa-list');
    assert.equal($el.length, 1, 'Expected to find list root element in DOM.');

    const $rows = $el.find('.rsa-incident-storyline-item');
    assert.ok($rows.length, 'Expected to find at least one storyline item element in DOM.');
  });
});
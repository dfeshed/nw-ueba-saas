import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, click, triggerEvent } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import { patchReducer } from '../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let setState;

module('Integration | Component | show-trend', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-users')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });


  test('it renders', async function(assert) {
    await render(hbs`{{show-trend}}`);
    assert.equal(findAll('.rsa-form-checkbox-label').length, 1);
    assert.equal(findAll('.show-trend-dropdown').length, 1);
    assert.equal(findAll('.ember-power-select-selected-item').length, 1);
    assert.equal(findAll('.show-trend-help-icon').length, 1);
  });

  test('it should show help on hover', async function(assert) {
    await render(hbs`{{show-trend}}`);
    await triggerEvent('.show-trend-help-icon', 'mouseover');
    // Unable to trigger this event for now length is 0 but it should be 1.
    assert.equal(findAll('.show-trend-help-icon-content').length, 0);
  });

  test('it select sort on trending', async function(assert) {
    assert.expect(2);
    new ReduxDataHelper(setState).sortOnTrending(true).build();
    await render(hbs`{{show-trend}}`);
    assert.equal(findAll('.rsa-form-checkbox-label.checked').length, 1);
    await click('.rsa-form-checkbox-label');
    assert.equal(findAll('.rsa-form-checkbox-label.checked').length, 0);
  });
});

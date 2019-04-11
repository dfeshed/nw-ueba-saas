import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, click, findAll, waitUntil } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

let setState;
const timeout = 10000;

module('Integration | Component | entity-details-container/body/indicator-navigator', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('entity-details')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    this.owner.register('helper:mount', function() {});
  });

  test('it should render indicator navigator', async function(assert) {
    new ReduxDataHelper(setState).build();

    await render(hbs`{{entity-details-container/body/indicator-navigator}}`);

    assert.equal(findAll('.rsa-icon-arrow-left-12-filled').length, 1);
  });

  test('it should go to first overview if previous overview click', async function(assert) {
    new ReduxDataHelper(setState).build();

    await render(hbs`{{entity-details-container/body/indicator-navigator}}`);
    assert.equal(findAll('.rsa-icon-arrow-left-12-filled').length, 1);
    await click('.entity-details-container-body-indicator-navigator_button');
    return waitUntil(() => findAll('.rsa-form-button-wrapper.is-disabled').length === 1, { timeout }).then(() => {
      assert.equal(findAll('.rsa-form-button-wrapper.is-disabled').length, 1);
    });
  });

  test('it should go to next indicator right button clicked', async function(assert) {
    new ReduxDataHelper(setState).build();

    await render(hbs`{{entity-details-container/body/indicator-navigator}}`);
    assert.equal(this.element.textContent.trim(), 'Indicator 1 of 9');
    await click('.rsa-icon-arrow-right-12-filled');
    assert.equal(this.element.textContent.trim(), 'Indicator 2 of 9');
  });
});

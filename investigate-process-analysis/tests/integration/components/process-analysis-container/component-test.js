import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import { patchReducer } from '../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';

let setState;

module('Integration | Component | process-analysis-container', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-process-analysis')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('process-analysis/container renders', async function(assert) {
    await render(hbs`{{process-analysis-container}}`);
    assert.equal(findAll('.process-list-box').length, 2, '2 columns present');
  });

  test('Collapse and expand classes on load', async function(assert) {
    new ReduxDataHelper(setState).detailsTabSelected('Properties').build();
    await render(hbs`{{process-analysis-container}}`);
    assert.equal(findAll('.processTreeBox.expand').length, 1, 'Expand class added to process tree');
    assert.equal(findAll('.processDetailsBox.collapse').length, 1, 'Collapse class added to process details');
  });

  test('Collapse and expand classes toggle when Events is clicked on ', async function(assert) {
    new ReduxDataHelper(setState).detailsTabSelected('Events').build();
    await render(hbs`{{process-analysis-container}}`);
    assert.equal(findAll('.processTreeBox.collapse').length, 1, 'Collapse class added to process tree');
    assert.equal(findAll('.processDetailsBox.expand').length, 1, 'Expand class added to process details');
  });
});

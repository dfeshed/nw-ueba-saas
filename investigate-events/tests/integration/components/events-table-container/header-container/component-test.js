import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { find, findAll, render } from '@ember/test-helpers';
import { clickTrigger } from 'ember-power-select/test-support/helpers';
import EventColumnGroups from '../../../../data/subscriptions/investigate-columns/data';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';

let setState;

const columnSelector = '.rsa-investigate-events-table__header__columnGroup .ember-power-select-selected-item';

const renderDefaultHeaderContainer = async(assert) => {
  new ReduxDataHelper(setState).columnGroup('SUMMARY').reconSize('max').eventsPreferencesConfig().columnGroups(EventColumnGroups).eventCount(55).build();
  await render(hbs`{{events-table-container/header-container}}`);
  // TODO bring download back. power-select for download is not available as of now
  assert.equal(findAll('.ember-power-select-trigger').length, 1, 'only 1 power-select available. There is no option to select default column group.');
  assert.equal(find('.rsa-investigate-event-counter').textContent.trim(), '55');
};


module('Integration | Component | events table header container', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('hasInvestigateContentExportAccess', true);
  });

  test('render the events header with required fields ', async function(assert) {
    await renderDefaultHeaderContainer(assert);
    assert.ok(find('.rsa-investigate-events-table__header__container'), 'render event header container');
    assert.equal(find('.rsa-investigate-events-table__header__container').childElementCount, 2, 'rendered with two elements');
    // TODO bring download back.
    assert.equal(find('.rsa-investigate-events-table__header__content').childElementCount, 2, 'rendered with three elements');
    assert.equal(find('.rsa-investigate-events-table__header__eventLabel').textContent.trim().replace(/\s+/g, ''), 'Events55', 'rendered event header title');
    assert.equal(find('.rsa-investigate-events-table__header__columnGroup span').textContent.trim(), 'Summary List', 'rendered event header title');
    // TODO bring download back.
    // assert.equal(find('.rsa-investigate-events-table__header__downloadEvents span').textContent.trim(), 'Download', 'rendered event header title');
  });

  test('it provides option to select column groups', async function(assert) {
    await renderDefaultHeaderContainer(assert);
    assert.equal(find(columnSelector).textContent.trim(), 'Summary List', 'Default Column group is Summary List.');
    await clickTrigger();
    const options = findAll('.ember-power-select-option').map((d) => d.textContent.trim());
    assert.equal(options.join('').replace(/\s+/g, ''), 'Custom1Custom2SummaryListEmailAnalysisMalwareAnalysisThreatAnalysisWebAnalysisEndpointAnalysis');
    assert.equal(findAll('.ember-power-select-group').length, 2, 'render two column groups');
    assert.equal(findAll('.ember-power-select-group-name')[0].textContent.trim(), 'Custom Column Groups', 'render custom column group');
    assert.equal(findAll('.ember-power-select-group-name')[1].textContent.trim(), 'Default Column Groups', 'render default column group');
    assert.equal(find('.ember-power-select-group-name').getAttribute('title'), 'Manage Custom Column Groups in Events View');
  });


  test('it provides option for search filter', async function(assert) {
    await renderDefaultHeaderContainer(assert);
    await clickTrigger();
    assert.ok(find('.ember-power-select-search'), 'Show search filter option in drop down');
  });

  test('persisted column group is preselected in the drop down', async function(assert) {
    new ReduxDataHelper(setState).columnGroup('MALWARE').eventsPreferencesConfig().columnGroups(EventColumnGroups).build();
    await render(hbs`{{events-table-container/header-container}}`);
    // return waitFor(columnSelector).then(() => {
    assert.equal(find(columnSelector).textContent.trim(), 'Malware Analysis', 'Expected Malware Analysis to be selected');
    // });
  });

});

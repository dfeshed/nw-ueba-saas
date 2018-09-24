import { moduleForComponent, skip, test } from 'ember-qunit';
import $ from 'jquery';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { applyPatch, revertPatch } from '../../../../helpers/patch-reducer';
import { waitFor } from 'ember-wait-for-test-helper/wait-for';
import { clickTrigger } from 'ember-power-select/test-support/helpers';
import EventColumnGroups from '../../../../data/subscriptions/investigate-columns/data';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { later } from '@ember/runloop';
import { patchPowerSelect, restorePowerSelect } from '../../../../helpers/patch-power-select';

let setState;

const columnSelector = '.rsa-investigate-events-table__header__columnGroup .ember-power-select-selected-item';

const renderDefaultHeaderContainer = (assert, _this) => {
  new ReduxDataHelper(setState).columnGroup('SUMMARY').reconSize('max').eventsPreferencesConfig().columnGroups(EventColumnGroups).eventCount(55).build();
  _this.render(hbs`{{events-table-container/header-container}}`);
  assert.equal(_this.$('.ember-power-select-trigger').length, 2, 'there is no option to select default column group.');
  assert.equal(_this.$('.rsa-investigate-event-counter').text().trim(), '55');
};


moduleForComponent('events-table-container/header-container', 'Integration | Component | events table header container', {
  integration: true,
  resolver: engineResolverFor('investigate-events'),
  beforeEach() {
    initialize({ '__container__': this.container });
    this.registry.injection('component', 'i18n', 'service:i18n');

    this.inject.service('accessControl');
    this.set('accessControl.hasInvestigateContentExportAccess', true);

    setState = (state) => {
      applyPatch(state);
      this.inject.service('redux');
    };

  },
  afterEach() {
    revertPatch();
    restorePowerSelect();
  }
});

test('render the events header with required fields ', function(assert) {
  renderDefaultHeaderContainer(assert, this);
  assert.equal(this.$('.rsa-investigate-events-table__header__container').length, 1, 'render event header container');
  assert.equal(this.$('.rsa-investigate-events-table__header__container')[0].childElementCount, 2, 'rendered with two elements');
  assert.equal(this.$('.rsa-investigate-events-table__header__content')[0].childElementCount, 3, 'rendered with three elements');
  assert.equal(this.$('.rsa-investigate-events-table__header__eventLabel')[0].textContent.trim().replace(/\s+/g, ''), 'Events55', 'rendered event header title');
  assert.equal(this.$('.rsa-investigate-events-table__header__columnGroup span')[0].textContent.trim(), 'Summary List', 'rendered event header title');
  assert.equal(this.$('.rsa-investigate-events-table__header__downloadEvents span')[0].textContent.trim(), 'Download', 'rendered event header title');
});

// Skipping this because the `later()` part is garbage. Needs to be refactored
skip('it provides option to select column groups', function(assert) {
  patchPowerSelect();
  renderDefaultHeaderContainer(assert, this);
  assert.equal(this.$(columnSelector).text().trim(), 'Summary List', 'Default Column group is Summary List.');
  clickTrigger();
  assert.equal($('.ember-power-select-option').text().trim().replace(/\s+/g, ''), 'Custom1Custom2SummaryListEmailAnalysisMalwareAnalysisThreatAnalysisWebAnalysisEndpointAnalysis');
  assert.equal($('.ember-power-select-group').length, 2, 'render two column groups');
  assert.equal($('.ember-power-select-group-name')[0].textContent, 'Custom Column Groups', 'render custom column group');
  assert.equal($('.ember-power-select-group-name')[1].textContent, 'Default Column Groups', 'render default column group');
  const done = assert.async();
  later(() => {
    assert.equal($('.ember-power-select-group-name').first().attr('title'), 'Manage Custom Column Groups in Events View');
    done();
  }, 300);
});


test('it provides option for search filter', function(assert) {
  patchPowerSelect();
  renderDefaultHeaderContainer(assert, this);
  clickTrigger();
  assert.equal($('.ember-power-select-search').length, 1, 'Show search filter option in drop down');
});

test('persisted column group is preselected in the drop down', function(assert) {
  new ReduxDataHelper(setState).columnGroup('MALWARE').columnGroups(EventColumnGroups).build();
  this.render(hbs`{{events-table-container/header-container}}`);
  return waitFor(columnSelector).then(() => {
    assert.equal(this.$(columnSelector).text().trim(), 'Malware Analysis', 'Expected Malware Analysis to be selected');
  });
});

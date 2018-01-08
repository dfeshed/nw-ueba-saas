import { moduleForComponent, test } from 'ember-qunit';
import $ from 'jquery';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { applyPatch, revertPatch } from '../../../helpers/patch-reducer';
import { waitFor } from 'ember-wait-for-test-helper/wait-for';
import { clickTrigger, selectChoose } from '../../../helpers/ember-power-select';
import EventColumnGroups from '../../../data/subscriptions/investigate-columns/data';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import Helper from 'ember-helper';
import { patchSocket } from '../../../helpers/patch-socket';
import RSVP from 'rsvp';

let setState;
const prefToSave = { eventAnalysisPreferences: { isReconExpanded: false } };

const assertForInvestigateColumnAndColumnSelector = (waitFor, assert, headerCount, count, selectedOption, isNotEmptyRow) => {
  clickTrigger();
  selectChoose('.ember-power-select-trigger', selectedOption);
  return waitFor('.ember-power-select-selected-item').then(() => {
    assert.equal($('.rsa-data-table-header-cell').length, headerCount * (isNotEmptyRow ? 1 : 2), `Should show columns for ${selectedOption}.`);
    assert.equal($('.ember-power-select-selected-item').text().trim(), selectedOption, `Selected column group should be ${selectedOption}.`);
    $('.rsa-icon-cog-filled').trigger('mouseover');
    return waitFor('.rsa-form-checkbox-label', { count }).then(() => {
      assert.equal($('li .rsa-form-checkbox-label').length, count, `Should show all columns for column selector for ${selectedOption}.`);
    });
  });
};
const renderDefaultEventTable = (assert, _this) => {
  new ReduxDataHelper(setState).columnGroup('SUMMARY').reconSize('max').eventsPreferencesConfig().columnGroups(EventColumnGroups).build();
  _this.render(hbs`{{events-table}}`);
  assert.equal(_this.$('.rsa-investigate-events-table').length, 1);
  assert.equal(_this.$('.ember-power-select-trigger').length, 1, 'there is no option to select default column group.');
  assert.equal(_this.$('.rsa-icon-cog-filled').length, 1, 'There should be column selector icon.');
};

moduleForComponent('events-table', 'Integration | Component | events table', {
  integration: true,
  resolver: engineResolverFor('investigate-events'),
  beforeEach() {
    initialize({ '__container__': this.container });
    // Mock the route action 'toggleReconSize' on the click of expand/shrink toggle button on events page
    this.container.registry.registrations['helper:route-action'] = Helper.helper((arg) => {
      return this.routeActions[arg];
    });
    this.routeActions = {
      toggleReconSize(arg) {
        return RSVP.resolve({ arg });
      }
    };
    setState = (state) => {
      applyPatch(state);
      this.inject.service('redux');
    };

  },
  afterEach() {
    revertPatch();
  }
});

test('it provides option to select column groups', function(assert) {
  renderDefaultEventTable(assert, this);
  assert.equal(this.$('.ember-power-select-selected-item').text().trim(), 'Summary List', 'Default Column group is Summary List.');
  clickTrigger();
  return waitFor('.ember-power-select-options').then(() => {
    const $options = $('.ember-power-select-option');
    assert.equal($options.text().trim().replace(/\s+/g, ''), 'Custom1Custom2SummaryListEmailAnalysisMalwareAnalysisThreatAnalysisWebAnalysisEndpointAnalysis');
  });
});

test('it should show columns for Event Analysis', function(assert) {
  renderDefaultEventTable(assert, this);
  return assertForInvestigateColumnAndColumnSelector(waitFor, assert, 15, 41, 'Email Analysis');
});

test('it should show columns for Malware Analysis', function(assert) {
  renderDefaultEventTable(assert, this);
  return assertForInvestigateColumnAndColumnSelector(waitFor, assert, 15, 27, 'Malware Analysis');
});

test('it should show columns for Threat Analysis', function(assert) {
  renderDefaultEventTable(assert, this);
  return assertForInvestigateColumnAndColumnSelector(waitFor, assert, 15, 57, 'Threat Analysis');
});

test('it should show columns for Web Analysis', function(assert) {
  renderDefaultEventTable(assert, this);
  return assertForInvestigateColumnAndColumnSelector(waitFor, assert, 15, 53, 'Web Analysis');
});

test('it should show columns for Endpoint Analysis', function(assert) {
  renderDefaultEventTable(assert, this);
  return assertForInvestigateColumnAndColumnSelector(waitFor, assert, 15, 32, 'Endpoint Analysis');
});

test('Click on isExpanded toggle button on event page, persist the recon size', function(assert) {
  renderDefaultEventTable(assert, this);
  assert.equal(this.$('.rsa-icon-expand-diagonal-4-filled').length, 1);
  this.$('.rsa-icon-expand-diagonal-4-filled').click();
  patchSocket((method, modelName, query) => {
    assert.equal(query.data.eventAnalysisPreferences.isReconExpanded, true);
    assert.equal(method, 'setPreferences');
    assert.equal(modelName, 'investigate-events-preferences');
    assert.deepEqual(query, {
      data: prefToSave
    });
    assert.equal(query.data.eventAnalysisPreferences.isReconExpanded, false);
  });
});

test('persisted column group is preselected in the drop down', function(assert) {
  new ReduxDataHelper(setState).columnGroup('MALWARE').columnGroups(EventColumnGroups).build();
  this.render(hbs`{{events-table}}`);
  return waitFor('.ember-power-select-selected-item').then(() => {
    assert.equal(this.$('.ember-power-select-selected-item').text().trim(), 'Malware Analysis', 'Expected Malware Analysis to be selected');
  });
});

test('Check for OOTB column groups style', function(assert) {
  const custom = {
    id: 'SUMMARY1',
    name: 'Summary List1',
    ootb: false,
    columns: [
      { field: 'custom.theme', title: 'Theme' }
    ]
  };
  new ReduxDataHelper(setState).columnGroup('MALWARE').columnGroups(EventColumnGroups.concat(custom)).build();
  this.render(hbs`{{events-table}}`);
  clickTrigger();
  return waitFor('.ember-power-select-options').then(() => {
    // To check fixed width for column group dropdown.
    assert.equal($('.ember-power-select-trigger').get(0).clientWidth, 216);
    // To check tooltip for column group options.
    assert.equal($('.rsa-investigate-events-table__header__columns').first().attr('title'), 'Custom 1');
    assert.equal($('.rsa-investigate-events-table__header__columns').length, 3);
    assert.equal($('.rsa-investigate-events-table__header__ootb-columns').length, 7);
  });
});

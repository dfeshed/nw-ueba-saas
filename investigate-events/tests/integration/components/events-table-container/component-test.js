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
import { later } from 'ember-runloop';
import RSVP from 'rsvp';

let setState;

const assertForInvestigateColumnAndColumnSelector = (waitFor, assert, headerCount, count, selectedOption, isNotEmptyRow) => {
  clickTrigger();
  selectChoose('.ember-power-select-trigger', selectedOption);
  return waitFor('.ember-power-select-selected-item').then(() => {
    assert.equal($('.rsa-data-table-header-cell').length, headerCount * (isNotEmptyRow ? 1 : 2), `Should show columns for ${selectedOption}.`);
    assert.equal($('.ember-power-select-selected-item').text().trim(), selectedOption, `Selected column group should be ${selectedOption}.`);
    $('.rsa-icon-cog-filled').trigger('click');
    return waitFor('.rsa-form-checkbox-label', { count }).then(() => {
      assert.equal($('li .rsa-form-checkbox-label').length, count, `Should show all columns for column selector for ${selectedOption}.`);
    });
  });
};
const renderDefaultEventTable = (assert, _this) => {
  new ReduxDataHelper(setState).columnGroup('SUMMARY').reconSize('max').eventsPreferencesConfig().columnGroups(EventColumnGroups).eventCount(55).build();
  _this.render(hbs`{{events-table-container}}`);
  assert.equal(_this.$('.rsa-investigate-events-table').length, 1);
  assert.equal(_this.$('.ember-power-select-trigger').length, 1, 'there is no option to select default column group.');
  assert.equal(_this.$('.rsa-investigate-event-counter').text().trim(), '55');
  assert.equal(_this.$('.rsa-icon-cog-filled').length, 1, 'There should be column selector icon.');
};

moduleForComponent('events-table-container', 'Integration | Component | events table', {
  integration: true,
  resolver: engineResolverFor('investigate-events'),
  beforeEach() {
    initialize({ '__container__': this.container });
    this.registry.injection('component', 'i18n', 'service:i18n');
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

test('render the events header with required fields ', function(assert) {
  renderDefaultEventTable(assert, this);
  assert.equal(this.$('.rsa-investigate-events-table__header__container').length, 1, 'render event header container');
  assert.equal(this.$('.rsa-investigate-events-table__header__container')[0].childElementCount, 2, 'rendered with two elements');
  assert.equal(this.$('.rsa-investigate-events-table__header__eventLabel')[0].textContent.trim().replace(/\s+/g, ''), 'Events55', 'rendered event header title');
});

test('it provides option to select column groups', function(assert) {
  renderDefaultEventTable(assert, this);
  assert.equal(this.$('.ember-power-select-selected-item').text().trim(), 'Summary List', 'Default Column group is Summary List.');
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
  renderDefaultEventTable(assert, this);
  clickTrigger();
  assert.equal($('.ember-power-select-search').length, 1, 'Show search filter option in drop down');
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

test('persisted column group is preselected in the drop down', function(assert) {
  new ReduxDataHelper(setState).columnGroup('MALWARE').columnGroups(EventColumnGroups).build();
  this.render(hbs`{{events-table-container}}`);
  return waitFor('.ember-power-select-selected-item').then(() => {
    assert.equal(this.$('.ember-power-select-selected-item').text().trim(), 'Malware Analysis', 'Expected Malware Analysis to be selected');
  });
});

test('it should show error message when query is invalid', function(assert) {
  new ReduxDataHelper(setState).columnGroup('SUMMARY').columnGroups(EventColumnGroups).isInvalidQuery(true).build();
  this.render(hbs`{{events-table-container}}`);
  assert.equal(this.$('.rsa-panel-message .title').text().trim(), 'No events found.', 'Appropriate error title for invaild query response');
  assert.equal(this.$('.rsa-panel-message .message').text().trim(), 'Your filter criteria is invalid. Examine query for syntax errors.', 'Appropriate error description for invaild query response');
});

test('it should not show an error message when query is valid', function(assert) {
  new ReduxDataHelper(setState).columnGroup('SUMMARY').columnGroups(EventColumnGroups).isInvalidQuery(false).build();
  this.render(hbs`{{events-table-container}}`);
  assert.notEqual(this.$('.rsa-panel-message .title').text().trim(), 'No events found.', 'Appropriate error title for invaild query response');
  assert.notEqual(this.$('.rsa-panel-message .message').text().trim(), 'Your filter criteria is invalid. Examine query for syntax errors.', 'Appropriate error description for invaild query response');
});

test('it should show "no results" message only if there are zero results', function(assert) {
  new ReduxDataHelper(setState).columnGroup('SUMMARY').columnGroups(EventColumnGroups).eventResults([]).build();
  this.render(hbs`{{events-table-container}}`);
  assert.equal(this.$('.rsa-panel-message .message').length, 1);
  assert.equal(this.$('.rsa-panel-message .message').text().trim(), 'Your filter criteria did not match any records.');
});

test('it should not show "no results" message if there are results', function(assert) {
  new ReduxDataHelper(setState).columnGroup('SUMMARY').columnGroups(EventColumnGroups).eventResults(['something']).build();
  this.render(hbs`{{events-table-container}}`);
  assert.equal(this.$('.rsa-panel-message .message').length, 0);
  assert.equal(this.$('.rsa-panel-message .message').text().trim(), '');
});
import { moduleForComponent, test, skip } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import Ember from 'ember';
import IncidentsCube from 'sa/utils/cube/incidents';
import wait from 'ember-test-helpers/wait';
import { waitFor, createIncident } from './helpers';
import { clickTrigger, nativeMouseUp } from '../../../../../../helpers/ember-power-select';

const {
  Object: EmberObject
} = Ember;

moduleForComponent('rsa-respond/landing-page/respond-index/list-view', 'Integration | Component | rsa respond/landing page/respond index/list view', {
  integration: true,

  beforeEach() {

    const allCube = IncidentsCube.create({
      array: []
    });

    allCube.get('records').pushObjects([
      EmberObject.create({
        riskScore: 10,
        id: 'INC-491',
        name: 'Suspected command and control communication with www.mozilla.com',
        createdBy: 'User X',
        created: new Date().getTime(), // Current time in milliseconds
        lastUpdated: '2015-10-10',
        statusSort: 0, // Status: New
        prioritySort: 0, // Priority: Low
        alertCount: 10,
        eventCount: 2,
        sources: ['Event Stream Analysis'],
        assignee: {
          id: '1'
        },
        categories: []
      }),
      EmberObject.create({
        riskScore: 20,
        id: 'INC-492',
        name: 'Suspected command and control communication with www.mozilla.com',
        createdBy: 'User X',
        created: new Date().getTime(), // Current time in milliseconds
        lastUpdated: '2015-10-10',
        statusSort: 1, // Status: Assigned
        prioritySort: 1, // Priority: Medium
        alertCount: 10,
        eventCount: 2,
        sources: ['ECAT', 'Web Threat Detection'],
        assignee: {
          id: '2'
        },
        categories: [{
          name: 'childCategory1',
          parent: 'parentCategory1',
          id: '1'
        }, {
          name: 'childCategory2',
          parent: 'parentCategory1',
          id: '2'
        }]
      }),
      EmberObject.create({
        riskScore: 30,
        id: 'INC-493',
        name: 'Suspected command and control communication with www.mozilla.com',
        createdBy: 'User X',
        created: new Date().getTime(), // Current time in milliseconds
        lastUpdated: '2015-10-10',
        statusSort: 2, // Status: In-Progress
        prioritySort: 2, // Priority: High
        alertCount: 10,
        eventCount: 2,
        sources: ['Malware Analysis', 'Web Threat Detection'],
        assignee: {
          id: '3'
        },
        categories: []
      })
    ]);

    const users = [
      EmberObject.create({ id: '1', name: 'User 1', email: 'user1@rsa.com' }),
      EmberObject.create({ id: '2', name: 'User 2', email: 'user2@rsa.com' }),
      EmberObject.create({ id: '3', name: 'User 3', email: 'user3@rsa.com' })
    ];

    const categoryTags = [
      EmberObject.create({
        id: '1',
        parent: 'parentCategory1',
        name: 'childCategory1'
      }),
      EmberObject.create({
        id: '2',
        parent: 'parentCategory1',
        name: 'childCategory2'
      })
    ];

    this.setProperties({
      allIncidents: allCube,
      users,
      categoryTags
    });

    localStorage.clear();
  }
});

test('it renders', function(assert) {
  assert.expect(4);
  const done = assert.async();
  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view buffer=10 allIncidents=allIncidents users=users categoryTags=categoryTags}}`);

  assert.equal(this.$('.rsa-respond-list__filter-panel').length, 1, 'Filter panel is present');
  assert.equal(this.$('.rsa-data-table').length, 1, 'Data table is present');
  assert.equal(this.$('.bulk-edit-bar').length, 1, 'Bulk edit bar is present');

  waitFor(
    () => this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length === 3
  ).then(() => {
    assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 3, 'All rows are displayed by default');
    done();
  });
});

test('Lazy rendering', function(assert) {
  assert.expect(1);
  const done = assert.async();
  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view allIncidents=allIncidents users=users categoryTags=categoryTags}}`);

  waitFor(
    () => this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length === 1
  ).then(() => {
    assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 1, 'with default buffer(0), just 1 row is rendered even if there are 3 records');
    done();
  });
});

test('Filter panel renders', function(assert) {
  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view buffer=10 allIncidents=allIncidents users=users categoryTags=categoryTags}}`);

  assert.ok(this.$('.rsa-respond-list__filter-panel__time'), 'Date/Time filter section is present');
  assert.ok(this.$('.rsa-respond-list__filter-panel__time .date-time-options').length === 1, 'Date/Time filter is present');
  const defaultDateTimeFilter = this.$('.rsa-respond-list__filter-panel__time .date-time-options .ember-power-select-selected-item');
  assert.equal(defaultDateTimeFilter.text().trim(), 'All Data', 'Default Date/Time filter is All Data');

  assert.ok(this.$('.rsa-respond-list__filter-panel__risk-score'), 'Risk Score filter is present');
  assert.ok(this.$('.rsa-respond-list__filter-panel__risk-score .rsa-form-slider').length === 1, 'Risk Score slider is present');

  assert.ok(this.$('.rsa-respond-list__filter-panel__priority'), 'Priority filter is present');
  assert.ok(this.$('.rsa-respond-list__filter-panel__priority .rsa-form-checkbox').length > 0, 'Priority options are present');

  assert.ok(this.$('.rsa-respond-list__filter-panel__status'), 'Status filter is present');
  assert.ok(this.$('.rsa-respond-list__filter-panel__status .rsa-form-checkbox').length > 0, 'Status options are present');

  assert.ok(this.$('.rsa-respond-list__filter-panel__assignee-selector'), 'Assignee filter is present');

  assert.ok(this.$('.rsa-respond-list__filter-panel__source-selector'), 'Source filter is present');

  assert.ok(this.$('.rsa-respond-list__filter-panel__category'), 'Category filter is present');
  assert.ok(this.$('.rsa-respond-list__filter-panel__category .rsa-form-tag-manager'), 'Category options are present');

  assert.ok(this.$('.rsa-respond-list__filter-panel__reset-button'), 'Filter button is present');
});

test('Date/Time filter affects the number of incidents on screen. Selected: Today', function(assert) {
  const done = assert.async(2);

  // Add record that was created 24hrs ago for purpose of checking that filter works.
  const twentyFourHoursInMilliseconds = 86400000;
  const created = new Date().getTime() - twentyFourHoursInMilliseconds;
  const incident = createIncident({ id: 'INC-1', created });

  const allIncidents = this.get('allIncidents').get('records');
  allIncidents.pushObjects([
    incident
  ]);

  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view buffer=10 allIncidents=allIncidents users=users categoryTags=categoryTags}}`);

  assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 4, 'All data rendered.');

  clickTrigger('.rsa-respond-list__filter-panel__time .date-time-options');
  nativeMouseUp('.ember-power-select-option:eq(0)'); // setting status to Today
  wait().then(() => {
    const selected = this.$('.rsa-respond-list__filter-panel__time .date-time-options .ember-power-select-selected-item');
    assert.equal(selected.text().trim(), 'Today', 'Selected Today Date/Time option');
    assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 3, 'Date/Time filter works');
    done();

    clickTrigger('.rsa-respond-list__filter-panel__time .date-time-options');
    nativeMouseUp('.ember-power-select-option:eq(5)'); // setting status to All Data
    wait().then(() => {
      const selected = this.$('.rsa-respond-list__filter-panel__time .date-time-options .ember-power-select-selected-item');
      assert.equal(selected.text().trim(), 'All Data', 'Selected All Data Date/Time option');
      assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 4, 'Date/Time filter works');
      done();
    });
  });

});

test('Date/Time filter affects the number of incidents on screen. Selected: Last Hour', function(assert) {
  const done = assert.async(2);

  // Add record that was created an hour ago + 1 millisecond for purpose of checking that filter works.
  const hourInMilliseconds = 3600000;
  const created = new Date().getTime() - (hourInMilliseconds + 1);
  const incident = createIncident({ id: 'INC-1', created });

  const allIncidents = this.get('allIncidents').get('records');
  allIncidents.pushObjects([
    incident
  ]);

  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view buffer=10 allIncidents=allIncidents users=users categoryTags=categoryTags}}`);

  assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 4, 'All data rendered.');

  clickTrigger('.rsa-respond-list__filter-panel__time .date-time-options');
  nativeMouseUp('.ember-power-select-option:eq(1)'); // setting status to Last Hour
  wait().then(() => {
    const selected = this.$('.rsa-respond-list__filter-panel__time .date-time-options .ember-power-select-selected-item');
    assert.equal(selected.text().trim(), 'Last Hour', 'Selected Last Hour Date/Time option');
    assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 3, 'Date/Time filter works');
    done();

    clickTrigger('.rsa-respond-list__filter-panel__time .date-time-options');
    nativeMouseUp('.ember-power-select-option:eq(5)'); // setting status to All Data
    wait().then(() => {
      const selected = this.$('.rsa-respond-list__filter-panel__time .date-time-options .ember-power-select-selected-item');
      assert.equal(selected.text().trim(), 'All Data', 'Selected All Data Date/Time option');
      assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 4, 'Date/Time filter works');
      done();
    });
  });

});

test('Date/Time filter affects the number of incidents on screen. Selected: Last 12 Hours', function(assert) {
  const done = assert.async(2);

  // Add record that was created 12 hours ago + 1 millisecond for purpose of checking that filter works.
  const twelveHoursInMilliseconds = 43200000;
  const created = new Date().getTime() - (twelveHoursInMilliseconds + 1);
  const incident = createIncident({ id: 'INC-1', created });

  const allIncidents = this.get('allIncidents').get('records');
  allIncidents.pushObjects([
    incident
  ]);

  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view buffer=10 allIncidents=allIncidents users=users categoryTags=categoryTags}}`);

  assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 4, 'All data rendered.');

  clickTrigger('.rsa-respond-list__filter-panel__time .date-time-options');
  nativeMouseUp('.ember-power-select-option:eq(2)'); // setting status to Last 12 Hours
  wait().then(() => {
    const selected = this.$('.rsa-respond-list__filter-panel__time .date-time-options .ember-power-select-selected-item');
    assert.equal(selected.text().trim(), 'Last 12 Hours', 'Selected Last 12 Hours Date/Time option');
    assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 3, 'Date/Time filter works');
    done();

    clickTrigger('.rsa-respond-list__filter-panel__time .date-time-options');
    nativeMouseUp('.ember-power-select-option:eq(5)'); // setting status to All Data
    wait().then(() => {
      const selected = this.$('.rsa-respond-list__filter-panel__time .date-time-options .ember-power-select-selected-item');
      assert.equal(selected.text().trim(), 'All Data', 'Selected All Data Date/Time option');
      assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 4, 'Date/Time filter works');
      done();
    });
  });

});

test('Date/Time filter affects the number of incidents on screen. Selected: Last 24 Hours', function(assert) {
  const done = assert.async(2);

  // Add record that was created 24 hours ago + 1 millisecond for purpose of checking that filter works.
  const twentyFourHoursInMilliseconds = 86400000;
  const created = new Date().getTime() - (twentyFourHoursInMilliseconds + 1);
  const incident = createIncident({ id: 'INC-1', created });

  const allIncidents = this.get('allIncidents').get('records');
  allIncidents.pushObjects([
    incident
  ]);

  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view buffer=10 allIncidents=allIncidents users=users categoryTags=categoryTags}}`);

  assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 4, 'All data rendered.');

  clickTrigger('.rsa-respond-list__filter-panel__time .date-time-options');
  nativeMouseUp('.ember-power-select-option:eq(3)'); // setting status to Last 24 Hours
  wait().then(() => {
    const selected = this.$('.rsa-respond-list__filter-panel__time .date-time-options .ember-power-select-selected-item');
    assert.equal(selected.text().trim(), 'Last 24 Hours', 'Selected Last 24 Hours Date/Time option');
    assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 3, 'Date/Time filter works');
    done();

    clickTrigger('.rsa-respond-list__filter-panel__time .date-time-options');
    nativeMouseUp('.ember-power-select-option:eq(5)'); // setting status to All Data
    wait().then(() => {
      const selected = this.$('.rsa-respond-list__filter-panel__time .date-time-options .ember-power-select-selected-item');
      assert.equal(selected.text().trim(), 'All Data', 'Selected All Data Date/Time option');
      assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 4, 'Date/Time filter works');
      done();
    });
  });

});

test('Date/Time filter affects the number of incidents on screen. Selected: Last 7 Days', function(assert) {
  const done = assert.async(2);

  // Add record that was created 7 days ago + 1 millisecond for purpose of checking that filter works.
  const sevenDaysInMilliseconds = 604800000;
  const created = new Date().getTime() - (sevenDaysInMilliseconds + 1);
  const incident = createIncident({ id: 'INC-1', created });

  const allIncidents = this.get('allIncidents').get('records');
  allIncidents.pushObjects([
    incident
  ]);

  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view buffer=10 allIncidents=allIncidents users=users categoryTags=categoryTags}}`);

  assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 4, 'All data rendered.');

  clickTrigger('.rsa-respond-list__filter-panel__time .date-time-options');
  nativeMouseUp('.ember-power-select-option:eq(4)'); // setting status to Last 7 Days
  wait().then(() => {
    const selected = this.$('.rsa-respond-list__filter-panel__time .date-time-options .ember-power-select-selected-item');
    assert.equal(selected.text().trim(), 'Last 7 Days', 'Selected Last 7 Days Date/Time option');
    assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 3, 'Date/Time filter works');
    done();

    clickTrigger('.rsa-respond-list__filter-panel__time .date-time-options');
    nativeMouseUp('.ember-power-select-option:eq(5)'); // setting status to All Data
    wait().then(() => {
      const selected = this.$('.rsa-respond-list__filter-panel__time .date-time-options .ember-power-select-selected-item');
      assert.equal(selected.text().trim(), 'All Data', 'Selected All Data Date/Time option');
      assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 4, 'Date/Time filter works');
      done();
    });
  });

});

// Note: This test is disabled until rsa-form-datetime component's bugs are addressed.
skip('Date/Time filter affects the number of incidents on screen. Selected: Custom', function(assert) {
  const done = assert.async(2);

  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view buffer=10 allIncidents=allIncidents users=users categoryTags=categoryTags}}`);

  assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 3, 'All data rendered.');

  clickTrigger('.rsa-respond-list__filter-panel__time .date-time-options');
  nativeMouseUp('.ember-power-select-option:eq(6)'); // setting status to Custom
  wait().then(() => {
    const selected = this.$('.rsa-respond-list__filter-panel__time .date-time-options .ember-power-select-selected-item');
    assert.equal(selected.text().trim(), 'Custom', 'Selected Custom Date/Time option');

    assert.equal(this.$('.custom-date-start').length, 1, 'Date/Time filter shows Custom Start Date picker.');
    assert.equal(this.$('.custom-date-end').length, 1, 'Date/Time filter shows Custom End Date picker.');

    const startDatePlaceholder = this.$('.custom-date-start .rsa-form-input').find('input')[0].placeholder;
    assert.equal(startDatePlaceholder, 'Start Date', 'Start Date placeholder shown correctly.');

    const endDatePlaceholder = this.$('.custom-date-end .rsa-form-input').find('input')[0].placeholder;
    assert.equal(endDatePlaceholder, 'End Date', 'End Date placeholder shown correctly.');

    done();

    clickTrigger('.rsa-respond-list__filter-panel__time .date-time-options');
    nativeMouseUp('.ember-power-select-option:eq(5)'); // setting status to All Data
    wait().then(() => {
      const selected = this.$('.rsa-respond-list__filter-panel__time .date-time-options .ember-power-select-selected-item');
      assert.equal(selected.text().trim(), 'All Data', 'Selected All Data Date/Time option');
      assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 3, 'Date/Time filter works');
      done();
    });
  });

});

test('Risk score filter affects the number of incidents on screen', function(assert) {
  const done = assert.async(3);
  this.set('riskScoreValues', [1, 99]);

  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view
    buffer=10
    allIncidents=allIncidents
    users=users
    categoryTags=categoryTags
    riskScoreStart=riskScoreValues
    disablePersistence=true}}`);

  const resetButton = this.$().find('.rsa-respond-list__filter-panel__reset-button button');

  waitFor(
    () => this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length === 3
  ).then(() => {
    assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 3, 'All rows are displayed by default');

    // setting risk scores to 10 and 20
    this.set('riskScoreValues', [10, 20]);

    waitFor(
      () => this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length === 2
    ).then(() => {
      assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 2, 'Risk score filter works');

      resetButton.click();

      waitFor(
        () => this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length === 3
      ).then(() => {
        assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 3, 'Reset-button resets selected risk score filters');
        done();
      });
      done();
    });
    done();
  });
});

test('Priority filter affects the number of incidents on screen', function(assert) {
  const done = assert.async(3);
  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view buffer=10 allIncidents=allIncidents users=users categoryTags=categoryTags}}`);

  const resetButton = this.$().find('.rsa-respond-list__filter-panel__reset-button button');

  waitFor(
    () => this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length === 3
  ).then(() => {
    assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 3, 'All rows are displayed by default');

    // selecting priority MEDIUM
    this.$('.rsa-respond-list__filter-panel__priority .priority-1 input:first').prop('checked', true).trigger('change');

    waitFor(
      () => this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length === 1
    ).then(() => {
      assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 1, 'Priority filter works');

      resetButton.click();

      waitFor(
        () => this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length === 3
      ).then(() => {
        assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 3, 'Reset-button resets selected priority filters');
        done();
      });
      done();
    });
    done();
  });
});

test('Status filter affects the number of incidents on screen', function(assert) {
  const done = assert.async(3);
  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view buffer=10 allIncidents=allIncidents users=users categoryTags=categoryTags}}`);

  const resetButton = this.$().find('.rsa-respond-list__filter-panel__reset-button button');

  waitFor(
    () => this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length === 3
  ).then(() => {
    assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 3, 'All rows are displayed by default');

    // selecting status ASSIGNED
    this.$('.rsa-respond-list__filter-panel__status .status-1 input:first').prop('checked', true).trigger('change');

    waitFor(
      () => this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length === 1
    ).then(() => {
      assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 1, 'Status filter works');

      resetButton.click();

      waitFor(
        () => this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length === 3
      ).then(() => {
        assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 3, 'Reset-button resets selected Status filters');
        done();
      });
      done();
    });
    done();
  });
});

test('Single-Select Source filter affects the number of incidents on the screen', function(assert) {

  const done = assert.async(3);
  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view
    buffer=10
    allIncidents=allIncidents
    users=users
    categoryTags=categoryTags}}`);

  const resetButton = this.$().find('.rsa-respond-list__filter-panel__reset-button button');

  waitFor(
    () => this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length === 3
  ).then(() => {
    assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 3, 'Reset-button resets selected Source filters');
    // Do single-select to check that filtering by source works
    clickTrigger('.rsa-respond-list__filter-panel__source-selector');
    nativeMouseUp('.ember-power-select-option:eq(0)'); // setting source to Event Stream Analysis

    waitFor(
      () => this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length === 1
    ).then(() => {
      // Check that filter was applied correctly
      assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 1, 'Source filter works');
      resetButton.click();

      waitFor(
        () => this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length === 3
      ).then(() => {
        // Check that reset button removed source filters
        assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 3, 'Reset-button resets selected Source filters');
        done();
      });
      done();
    });
    done();
  });
}),

test('Multi-Select Source filter affects the number of incidents on the screen', function(assert) {

  const done = assert.async(3);
  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view
    buffer=10
    allIncidents=allIncidents
    users=users
    categoryTags=categoryTags}}`);

  const resetButton = this.$().find('.rsa-respond-list__filter-panel__reset-button button');

  waitFor(
    () => this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length === 3
  ).then(() => {
    // Check that all rows are displayed after reset button
    assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 3, 'All rows are displayed by default');

    // Do multi-source filter select
    clickTrigger('.rsa-respond-list__filter-panel__source-selector');
    nativeMouseUp('.ember-power-select-option:eq(0)'); // setting source to Event Stream Analysis
    clickTrigger('.rsa-respond-list__filter-panel__source-selector');
    nativeMouseUp('.ember-power-select-option:eq(2)'); // setting source to ECAT

    waitFor(
      () => this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length === 2
    ).then(() => {
      // Check that multi-filter works
      assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 2, 'Multi-select source filter works');
      resetButton.click();

      waitFor(
        () => this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length === 3
      ).then(() => {
        // Check that reset button works after multi-select source filtering
        assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 3, 'Reset-button resets selected Source filters');
        done();
      });
      done();
    });
    done();
  });
}),

test('Assignee filter affects the number of incidents on screen', function(assert) {
  const done = assert.async(3);
  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view
    buffer=10
    allIncidents=allIncidents
    users=users
    categoryTags=categoryTags}}`);

  const resetButton = this.$().find('.rsa-respond-list__filter-panel__reset-button button');

  waitFor(
    () => this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length === 3
  ).then(() => {
    assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 3, 'All rows are displayed by default');

    clickTrigger('.rsa-respond-list__filter-panel__assignee-selector');
    nativeMouseUp('.ember-power-select-option:eq(1)'); // Selecting User-1

    waitFor(
      () => this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length === 1
    ).then(() => {
      assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 1, 'Assignee filter works');

      resetButton.click();

      waitFor(
        () => this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length === 3
      ).then(() => {
        assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 3, 'Reset-button resets selected Assignee filters');
        done();
      });
      done();
    });
    done();
  });
});

test('Category filter affects the number of incidents on screen', function(assert) {
  const done = assert.async(3);
  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view
    buffer=10
    allIncidents=allIncidents
    users=users
    categoryTags=categoryTags}}`);

  const resetButton = this.$().find('.rsa-respond-list__filter-panel__reset-button button');

  waitFor(
    () => this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length === 3
  ).then(() => {
    assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 3, 'All rows are displayed by default');

    // selecting first Category (it requires 3 clicks)
    this.$('.rsa-respond-list__filter-panel__category .rsa-form-tag-manager .rsa-form-tag-manager__tree-toggle__icon').click();
    wait().then(() => {

      this.$('.rsa-respond-list__filter-panel__category .rsa-form-tag-manager .rsa-content-tree .rsa-content-accordion:first-child').click();
      wait().then(() => {

        this.$('.rsa-respond-list__filter-panel__category .rsa-form-tag-manager .rsa-content-tree .rsa-content-accordion:first-child .content .rsa-content-tree__child-label:first-child').click();

        waitFor(
          () => this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length === 1
        ).then(() => {
          assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 1, 'Category filter works');

          resetButton.click();

          waitFor(
            () => this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length === 3
          ).then(() => {
            assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 3, 'Reset-button resets selected Category filters');
            done();
          });
          done();
        });
        done();
      });
    });
  });
});

test('All incients are checked and then unchecked when the header checkbox is toggled.', function(assert) {
  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view
    buffer=10
    allIncidents=allIncidents
    users=users
    categoryTags=categoryTags}}`);

  assert.equal(this.$('.rsa-form-row-checkbox').length, this.get('allIncidents.array.length'), 'The proper number of checkboxes was found');
  assert.equal(this.$('.rsa-form-row-checkbox .rsa-form-checkbox.checked').length, 0, 'None of the checkboxes are currently selected.');

  this.$('input.js-respond-listview-checkbox-header').prop('checked', true).trigger('change');
  assert.equal(this.$('.rsa-form-row-checkbox .rsa-form-checkbox.checked').length, this.get('allIncidents.array.length'), 'All of the checkboxes are currently selected.');

  this.$('input.js-respond-listview-checkbox-header').prop('checked', false).trigger('change');
  assert.equal(this.$('.rsa-form-row-checkbox .rsa-form-checkbox.checked').length, 0, 'None of the checkboxes are currently selected.');
});

test('When a bulk edit is active, the filter controls are disabled.', function(assert) {
  const done = assert.async(2);
  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view
    buffer=10
    allIncidents=allIncidents
    users=users
    categoryTags=categoryTags}}`);

  assert.equal(this.$('.rsa-respond-list__filter-panel .rsa-form-slider').hasClass('is-disabled'), false, 'The risk score filter component is not disabled.');
  assert.equal(this.$('.rsa-respond-list__filter-panel .rsa-respond-list__filter-panel__priority .rsa-form-checkbox').hasClass('disabled'), false, 'The priority filter component is not disabled.');
  assert.equal(this.$('.rsa-respond-list__filter-panel .rsa-respond-list__filter-panel__status .rsa-form-checkbox').hasClass('disabled'), false, 'The status filter component is not disabled.');
  assert.equal(this.$('.rsa-respond-list__filter-panel .rsa-respond-list__filter-panel__source-selector').length, 1, 'The source selector filter component is not disabled.');
  assert.equal(this.$('.rsa-respond-list__filter-panel .rsa-respond-list__filter-panel__assignee-selector').length, 1, 'The assignee selector filter component is not disabled.');
  assert.equal(this.$('.rsa-respond-list__filter-panel .rsa-form-tag-manager').hasClass('.is-disabled'), false, 'The form tag manager component is not disabled.');

  this.$('input.js-respond-listview-checkbox-header').prop('checked', true).trigger('change');

  wait().then(() => {
    clickTrigger('.rsa-form-assignee-select');
    nativeMouseUp('.ember-power-select-option:eq(0)');

    wait().then(() => {
      assert.equal(this.$('.rsa-respond-list__filter-panel .rsa-form-slider').hasClass('is-disabled'), true, 'The risk score filter component is disabled.');
      assert.equal(this.$('.rsa-respond-list__filter-panel .rsa-respond-list__filter-panel__priority .rsa-form-checkbox').hasClass('disabled'), true, 'The priority filter component is disabled.');
      assert.equal(this.$('.rsa-respond-list__filter-panel .rsa-respond-list__filter-panel__status .rsa-form-checkbox').hasClass('disabled'), true, 'The status filter component is disabled.');
      assert.equal(this.$('.rsa-respond-list__filter-panel .rsa-respond-list__filter-panel__sources .empty-selection').length, 1, 'The source selector placeholder is present.');
      assert.equal(this.$('.rsa-respond-list__filter-panel .rsa-respond-list__filter-panel__source-selector').length, 0, 'The source selector is not present.');
      assert.equal(this.$('.rsa-respond-list__filter-panel .rsa-respond-list__filter-panel__assignee .empty-selection').length, 1, 'The assignee selector placeholder is present.');
      assert.equal(this.$('.rsa-respond-list__filter-panel .rsa-respond-list__filter-panel__assignee-selector').length, 0, 'The assignee selector is not present.');
      assert.equal(this.$('.rsa-respond-list__filter-panel .rsa-form-tag-manager').hasClass('is-disabled'), true, 'The form tag manager component is disabled.');
      done();
    });

    done();
  });
});

import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import Ember from 'ember';
import IncidentsCube from 'sa/utils/cube/incidents';
import wait from 'ember-test-helpers/wait';
import { waitFor } from './helpers';

const { Object: EmberObject } = Ember;

moduleForComponent('rsa-respond/landing-page/respond-index/list-view', 'Integration | Component | rsa respond/landing page/respond index/list view', {
  integration: true,

  beforeEach() {
    let allCube = IncidentsCube.create({
      array: []
    });

    allCube.get('records').pushObjects([EmberObject.create({
      riskScore: 1,
      id: 'INC-491',
      name: 'Suspected command and control communication with www.mozilla.com',
      createdBy: 'User X',
      created: '2015-10-10',
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
      riskScore: 1,
      id: 'INC-492',
      name: 'Suspected command and control communication with www.mozilla.com',
      createdBy: 'User X',
      created: '2015-10-10',
      lastUpdated: '2015-10-10',
      statusSort: 1, // Status: Assigned
      prioritySort: 1, // Priority: Medium
      alertCount: 10,
      eventCount: 2,
      sources: ['ecat', 'Web Threat Detection'],
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
      riskScore: 1,
      id: 'INC-493',
      name: 'Suspected command and control communication with www.mozilla.com',
      createdBy: 'User X',
      created: '2015-10-10',
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

    let users = [
      EmberObject.create({ id: '1', firstName: 'User 1', lastName: 'LastName 1', friendlyName: 'user1', email: 'user1@rsa.com' }),
      EmberObject.create({ id: '2', firstName: 'User 2', lastName: 'LastName 2', friendlyName: 'user2', email: 'user2@rsa.com' }),
      EmberObject.create({ id: '3', firstName: 'User 3', lastName: 'LastName 3', friendlyName: 'user3', email: 'user3@rsa.com' })
    ];

    let categoryTags = [
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
  }
});

test('it renders', function(assert) {
  assert.expect(3);
  const done = assert.async();
  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view allIncidents=allIncidents users=users categoryTags=categoryTags}}`);

  assert.equal(this.$('.rsa-respond-list__filter-panel').length, 1, 'Filter panel is present');
  assert.equal(this.$('.rsa-data-table').length, 1, 'Data table is present');

  waitFor(
    () => this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length === 3
  ).then(() => {
    assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 3, 'All rows are displayed by default');
    done();
  });
});

test('Filter panel renders', function(assert) {
  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view allIncidents=allIncidents users=users categoryTags=categoryTags}}`);

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

test('Priority filter affects the number of incidents on screen', function(assert) {
  const done = assert.async(3);
  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view allIncidents=allIncidents users=users categoryTags=categoryTags}}`);

  let resetButton = this.$().find('.rsa-respond-list__filter-panel__reset-button button');

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
  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view allIncidents=allIncidents users=users categoryTags=categoryTags}}`);

  let resetButton = this.$().find('.rsa-respond-list__filter-panel__reset-button button');

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
  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view allIncidents=allIncidents users=users categoryTags=categoryTags}}`);

  let resetButton = this.$().find('.rsa-respond-list__filter-panel__reset-button button');

  waitFor(
    () => this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length === 3
  ).then(() => {
    assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 3, 'Reset-button resets selected Source filters');
    // Do single-select to check that filtering by source works
    this.$('.rsa-respond-list__filter-panel__source-selector .prompt').click();
    this.$('.rsa-respond-list__filter-panel__source-selector select').val('Event Stream Analysis').trigger('change');

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
  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view allIncidents=allIncidents users=users categoryTags=categoryTags}}`);

  let resetButton = this.$().find('.rsa-respond-list__filter-panel__reset-button button');

  waitFor(
    () => this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length === 3
  ).then(() => {
    // Check that all rows are displayed after reset button
    assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 3, 'All rows are displayed by default');

    // Do multi-source filter select
    this.$('.rsa-respond-list__filter-panel__source-selector .prompt').click();
    this.$('.rsa-respond-list__filter-panel__source-selector select').val(['Event Stream Analysis', 'ecat']).trigger('change');

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
  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view allIncidents=allIncidents users=users categoryTags=categoryTags}}`);

  let resetButton = this.$().find('.rsa-respond-list__filter-panel__reset-button button');

  waitFor(
    () => this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length === 3
  ).then(() => {
    assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 3, 'All rows are displayed by default');
    // selecting assingee 1
    this.$('.rsa-respond-list__filter-panel__assignee-selector .prompt').click();
    this.$('.rsa-respond-list__filter-panel__assignee-selector select').val('1').trigger('change');

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
  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view allIncidents=allIncidents users=users categoryTags=categoryTags}}`);

  let resetButton = this.$().find('.rsa-respond-list__filter-panel__reset-button button');

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
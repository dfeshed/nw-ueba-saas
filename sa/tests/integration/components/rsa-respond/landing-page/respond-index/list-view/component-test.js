import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import Ember from 'ember';
import IncidentsCube from 'sa/utils/cube/incidents';
import wait from 'ember-test-helpers/wait';
import { waitFor } from './helpers';

const {
  Object: EmberObject,
  Service,
  Evented } = Ember;

const eventBusStub = Service.extend(Evented, {});
const FIX_ELEMENT_ID = 'tether_fix_style_element';

function insertTetherFix() {
  let styleElement = document.createElement('style');
  styleElement.id = FIX_ELEMENT_ID;
  styleElement.innerText =
    '#ember-testing-container, #ember-testing-container * {' +
      'position: static !important;' +
    '}';

  document.body.appendChild(styleElement);
}

function removeTetherFix() {
  let styleElement = document.getElementById(FIX_ELEMENT_ID);
  document.body.removeChild(styleElement);
}

moduleForComponent('rsa-respond/landing-page/respond-index/list-view', 'Integration | Component | rsa respond/landing page/respond index/list view', {
  integration: true,

  beforeEach() {
    insertTetherFix();
    this.register('service:event-bus', eventBusStub);
    this.inject.service('event-bus', { as: 'eventBus' });

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
      statusSort: 0,
      prioritySort: 0,
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
      statusSort: 1,
      prioritySort: 1,
      alertCount: 10,
      eventCount: 2,
      sources: ['Event Stream Analysis'],
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
    })]);

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
  },

  afterEach() {
    removeTetherFix();
  }
});

test('it renders', function(assert) {
  assert.expect(4);
  const done = assert.async();
  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view allIncidents=allIncidents users=users categoryTags=categoryTags}}`);

  assert.equal(this.$('.rsa-respond-list__filter-panel').length, 1, 'Filter panel is present');
  assert.equal(this.$('.rsa-data-table').length, 1, 'Data table is present');
  assert.equal(this.$('.rsa-respond-list__select-columns').length, 1, 'Column selection is present');

  waitFor(
    () => this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length === 2
  ).then(() => {
    assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 2, 'All rows are displayed by default');
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

  assert.ok(this.$('.rsa-respond-list__filter-panel__category'), 'Category filter is present');
  assert.ok(this.$('.rsa-respond-list__filter-panel__category .rsa-form-tag-manager'), 'Category options are present');

  assert.ok(this.$('.rsa-respond-list__filter-panel__reset-button'), 'Filter button is present');
});

test('Priority filter affects the number of incidents on screen', function(assert) {
  const done = assert.async(3);
  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view allIncidents=allIncidents users=users categoryTags=categoryTags}}`);

  let resetButton = this.$().find('.rsa-respond-list__filter-panel__reset-button button');

  waitFor(
    () => this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length === 2
  ).then(() => {
    assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 2, 'All rows are displayed by default');

    // selecting priority MEDIUM
    this.$('.rsa-respond-list__filter-panel__priority .priority-1 input:first').prop('checked', true).trigger('change');

    waitFor(
      () => this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length === 1
    ).then(() => {
      assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 1, 'Priority filter works');

      resetButton.click();

      waitFor(
        () => this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length === 2
      ).then(() => {
        assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 2, 'Reset-button resets selected priority filters');
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
    () => this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length === 2
  ).then(() => {
    assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 2, 'All rows are displayed by default');

    // selecting status ASSIGNED
    this.$('.rsa-respond-list__filter-panel__status .status-1 input:first').prop('checked', true).trigger('change');

    waitFor(
      () => this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length === 1
    ).then(() => {
      assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 1, 'Status filter works');

      resetButton.click();

      waitFor(
        () => this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length === 2
      ).then(() => {
        assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 2, 'Reset-button resets selected Status filters');
        done();
      });
      done();
    });
    done();
  });
});

test('Assignee filter affects the number of incidents on screen', function(assert) {
  const done = assert.async(3);
  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view allIncidents=allIncidents users=users categoryTags=categoryTags}}`);

  let resetButton = this.$().find('.rsa-respond-list__filter-panel__reset-button button');

  waitFor(
    () => this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length === 2
  ).then(() => {
    assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 2, 'All rows are displayed by default');
    // selecting assingee 1
    this.$('.rsa-respond-list__filter-panel__assignee-selector .prompt').click();
    this.$('.rsa-respond-list__filter-panel__assignee-selector select').val('1').trigger('change');

    waitFor(
      () => this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length === 1
    ).then(() => {
      assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 1, 'Assignee filter works');

      resetButton.click();

      waitFor(
        () => this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length === 2
      ).then(() => {
        assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 2, 'Reset-button resets selected Assignee filters');
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
    () => this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length === 2
  ).then(() => {
    assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 2, 'All rows are displayed by default');

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
            () => this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length === 2
          ).then(() => {
            assert.equal(this.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 2, 'Reset-button resets selected Category filters');
            done();
          });
          done();
        });
        done();
      });
    });
  });
});

test('Column selector displays columns', function(assert) {
  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view allIncidents=allIncidents users=users categoryTags=categoryTags}}`);

  this.get('eventBus').trigger('rsa-content-tooltip-display-selectColumnTooltip');

  return wait().then(() => {
    assert.equal(this.$('.rsa-content-tooltip .tooltip-content .rsa-form-checkbox').length, 12, 'Displaying all available columns on column-selector');
    assert.equal(this.$('.rsa-content-tooltip .tooltip-content .rsa-form-checkbox.is-selected').length, 7, 'Default visible columns are selected by default');

    assert.equal(this.$('.rsa-data-table .rsa-data-table-header .rsa-data-table-header-row .rsa-data-table-header-cell').length, 7, 'Selected columns are visible by default');
  });
});

test('Column selection affects visible columns on screen', function(assert) {
  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view allIncidents=allIncidents users=users categoryTags=categoryTags}}`);

  assert.equal(this.$('.js-header-cell-rsa-respond-list-events').length, 0, 'Event-count column is not visible by default');

  this.get('eventBus').trigger('rsa-content-tooltip-display-selectColumnTooltip');

  return wait().then(() => {
    this.$('.column-selection-eventCount input:first').prop('checked', true).trigger('change');

    return wait().then(() => {
      assert.equal(this.$('.js-header-cell-rsa-respond-list-events').length, 1, 'Event-count column is visible after column selection');
    });
  });
});
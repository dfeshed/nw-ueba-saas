import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import Ember from 'ember';
import IncidentsCube from 'sa/utils/cube/incidents';
import wait from 'ember-test-helpers/wait';

const {
  run,
  Service,
  Evented,
  Object: EmberObject
} = Ember;

const eventBusStub = Service.extend(Evented, {});
const FIX_ELEMENT_ID = 'tether_fix_style_element';

function insertTetherFix() {
  const styleElement = document.createElement('style');
  styleElement.id = FIX_ELEMENT_ID;
  styleElement.innerText =
    '#ember-testing-container, #ember-testing-container * {' +
      'position: static !important;' +
    '}';

  document.body.appendChild(styleElement);
}

function removeTetherFix() {
  const styleElement = document.getElementById(FIX_ELEMENT_ID);
  document.body.removeChild(styleElement);
}


moduleForComponent('rsa-respond/landing-page/respond-index/list-view/bulk-edit-bar', 'Integration | Component | rsa respond/landing page/respond index/list view/bulk edit bar', {
  integration: true,

  beforeEach() {
    const incidentCube = IncidentsCube.create({
      array: []
    });

    insertTetherFix();
    this.register('service:event-bus', eventBusStub);
    this.inject.service('event-bus', { as: 'eventBus' });

    incidentCube.get('records').pushObjects([
      EmberObject.create({
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

    const users = [
      EmberObject.create({ id: '1', firstName: 'User 1', lastName: 'LastName 1', friendlyName: 'user1', email: 'user1@rsa.com' }),
      EmberObject.create({ id: '2', firstName: 'User 2', lastName: 'LastName 2', friendlyName: 'user2', email: 'user2@rsa.com' }),
      EmberObject.create({ id: '3', firstName: 'User 3', lastName: 'LastName 3', friendlyName: 'user3', email: 'user3@rsa.com' })
    ];

    this.setProperties({
      incidents: incidentCube.get('records'),
      isBulkEditInProgress: false,
      showBulkEditMessage: false,
      users
    });
  },

  afterEach() {
    removeTetherFix();
  }
});

test('The rsa-bulk-edit-bar component renders with the proper elements.', function(assert) {
  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view/bulk-edit-bar
    users=users
    incidents=incidents
    saveAction=bulkSaveAction
    isBulkEditInProgress=isBulkEditInProgress
    showSuccessMessage=showBulkEditMessage}}`);

  assert.equal(this.$('.rsa-bulk-edit-button-group').length, 2, 'Two rsa-bulk-edit-button-group elements were found.');

  // First Button Group
  // Status
  assert.equal(this.$('.rsa-bulk-edit-button-group:first span.rsa-form-status-select').length, 1, 'One span.rsa-form-status-select element was found.');
  assert.equal(this.$('.rsa-bulk-edit-button-group:first span.rsa-form-status-select div.rsa-form-status-select').length, 1, 'One div.rsa-form-status-select element was found.');
  assert.equal(this.$('.rsa-bulk-edit-button-group:first span.rsa-form-status-select div.rsa-form-status-select button.rsa-form-button').length, 1, 'One button.rsa-form-button for element for select status was found.');
  assert.equal(this.$('.rsa-bulk-edit-button-group:first span.rsa-form-status-select div.rsa-form-status-select button.rsa-form-button').text().trim(), 'Status', 'The label for the select status dropdown is correct.');
  assert.equal(this.$('.rsa-bulk-edit-button-group:first span.rsa-form-status-select + div.rsa-content-tooltip').length, 1, 'An associated rsa-content-tooltip element was found for the select status dropdown.');

  // Assignee
  assert.equal(this.$('.rsa-bulk-edit-button-group:first span.rsa-form-assignee-select').length, 1, 'One span.rsa-form-assignee-select element was found.');
  assert.equal(this.$('.rsa-bulk-edit-button-group:first span.rsa-form-assignee-select div.rsa-form-assignee-select').length, 1, 'One div.rsa-form-assignee-select element was found.');
  assert.equal(this.$('.rsa-bulk-edit-button-group:first span.rsa-form-assignee-select div.rsa-form-assignee-select button.rsa-form-button').length, 1, 'One button.rsa-form-button for element for assignee status was found.');
  assert.equal(this.$('.rsa-bulk-edit-button-group:first span.rsa-form-assignee-select div.rsa-form-assignee-select button.rsa-form-button').text().trim(), 'Assignee', 'The label for the select assignee dropdown is correct.');
  assert.equal(this.$('.rsa-bulk-edit-button-group:first span.rsa-form-assignee-select + div.rsa-content-tooltip').length, 1, 'An associated rsa-content-tooltip element was found for the select assignee dropdown.');

  // Priority
  assert.equal(this.$('.rsa-bulk-edit-button-group:first span.rsa-form-priority-select').length, 1, 'One span.rsa-form-priority-select element was found.');
  assert.equal(this.$('.rsa-bulk-edit-button-group:first span.rsa-form-priority-select div.rsa-form-priority-select').length, 1, 'One div.rsa-form-priority-select element was found.');
  assert.equal(this.$('.rsa-bulk-edit-button-group:first span.rsa-form-priority-select div.rsa-form-priority-select button.rsa-form-button').length, 1, 'One button.rsa-form-button for element for priority status was found.');
  assert.equal(this.$('.rsa-bulk-edit-button-group:first span.rsa-form-priority-select div.rsa-form-priority-select button.rsa-form-button').text().trim(), 'Priority', 'The label for the select priority dropdown is correct.');
  assert.equal(this.$('.rsa-bulk-edit-button-group:first span.rsa-form-priority-select + div.rsa-content-tooltip').length, 1, 'An associated rsa-content-tooltip element was found for the select priority dropdown.');

  // Second Button Group
  assert.equal(this.$('.rsa-bulk-edit-button-group:nth-child(2) .rsa-form-button-wrapper button.rsa-form-button').length, 2, 'Two buttons within the second button group exist.');
  assert.equal(this.$('.rsa-bulk-edit-button-group:nth-child(2) .rsa-form-button-wrapper:first button.rsa-form-button').text().trim(), 'Save', 'The first button within the second button group has the proper "Save" label.');
  assert.equal(this.$('.rsa-bulk-edit-button-group:nth-child(2) .rsa-form-button-wrapper:nth-child(2) button.rsa-form-button').text().trim(), 'Cancel', 'The first button within the second button group has the proper "Cancel" label.');
  assert.equal(this.$('.rsa-bulk-edit-button-group:nth-child(2) .rsa-bulk-edit-update-message').length, 1, 'The update message element was found.');
});

test('The rsa-bulk-edit button select lists are active when an incident is checked.', function(assert) {
  const incFourNintyOne = this.get('incidents').findBy('id','INC-491');

  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view/bulk-edit-bar
    users=users
    incidents=incidents
    saveAction=bulkSaveAction
    isBulkEditInProgress=isBulkEditInProgress
    showSuccessMessage=showBulkEditMessage}}`);

  assert.equal(this.$('.rsa-form-status-select').hasClass('is-disabled'), true, 'The select status tooltip is not currently enabled');
  assert.equal(this.$('.rsa-form-assignee-select').hasClass('is-disabled'), true, 'The assignee status tooltip is not currently enabled');
  assert.equal(this.$('.rsa-form-priority-select').hasClass('is-disabled'), true, 'The priority status tooltip is not currently enabled');

  run(() => {
    incFourNintyOne.set('checked', true);
  });

  assert.equal(this.$('.rsa-form-status-select').hasClass('is-disabled'), false, 'The select status tooltip is currently enabled');
  assert.equal(this.$('.rsa-form-assignee-select').hasClass('is-disabled'), false, 'The assignee status tooltip is currently enabled');
  assert.equal(this.$('.rsa-form-priority-select').hasClass('is-disabled'), false, 'The priority status tooltip is currently enabled');
});

test('Only the rsa-bulk-edit status select list is active when a closed incident is checked.', function(assert) {
  const incFourNintyTwo = this.get('incidents').findBy('id','INC-492');

  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view/bulk-edit-bar
    users=users
    incidents=incidents
    saveAction=bulkSaveAction
    isBulkEditInProgress=isBulkEditInProgress
    showSuccessMessage=showBulkEditMessage}}`);

  assert.equal(this.$('.rsa-form-status-select').hasClass('is-disabled'), true, 'The select status tooltip is not currently enabled');
  assert.equal(this.$('.rsa-form-assignee-select').hasClass('is-disabled'), true, 'The assignee status tooltip is not currently enabled');
  assert.equal(this.$('.rsa-form-priority-select').hasClass('is-disabled'), true, 'The priority status tooltip is not currently enabled');

  run(() => {
    incFourNintyTwo.set('statusSort', 5);
    incFourNintyTwo.set('checked', true);
  });

  assert.equal(this.$('.rsa-form-status-select').hasClass('is-disabled'), false, 'The select status tooltip is currently enabled');
  assert.equal(this.$('.rsa-form-assignee-select').hasClass('is-disabled'), true, 'The assignee status tooltip is currently enabled');
  assert.equal(this.$('.rsa-form-priority-select').hasClass('is-disabled'), true, 'The priority status tooltip is currently enabled');
});

test('The Save and Cancel buttons in the rsa-bulk-edit bar should appear when a value is chosen from a select list', function(assert) {
  const done = assert.async(2);
  const incFourNintyOne = this.get('incidents').findBy('id','INC-491');

  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view/bulk-edit-bar
    users=users
    incidents=incidents
    saveAction=bulkSaveAction
    isBulkEditInProgress=isBulkEditInProgress
    showSuccessMessage=showBulkEditMessage}}`);

  assert.equal(this.$('.rsa-bulk-edit-button-group:nth-child(2) .rsa-form-button-wrapper:first').hasClass('is-hidden'), true, 'The Save button is currently hidden.');
  assert.equal(this.$('.rsa-bulk-edit-button-group:nth-child(2) .rsa-form-button-wrapper:nth-child(2)').hasClass('is-hidden'), true, 'The Cancel button is currently hidden.');

  run(() => {
    incFourNintyOne.set('checked', true);
    done();
  });

  this.$('.rsa-content-help-trigger.rsa-form-status-select .rsa-form-button').click();

  wait().then(() => {
    this.$('ul.rsa-form-status-select li[value="2"]').click();
    assert.equal(this.$('.rsa-bulk-edit-button-group:nth-child(2) .rsa-form-button-wrapper:first').hasClass('is-hidden'), false, 'The Save button is currently not hidden.');
    assert.equal(this.$('.rsa-bulk-edit-button-group:nth-child(2) .rsa-form-button-wrapper:nth-child(2)').hasClass('is-hidden'), false, 'The Cancel button is currently not hidden.');
    done();
  });
});

test('Clicking the Save button after selecting values successfully saves the incident.', function(assert) {
  const done = assert.async(5);
  const incFourNintyOne = this.get('incidents').findBy('id','INC-491');

  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view/bulk-edit-bar
    users=users
    incidents=incidents
    saveAction=bulkSaveAction
    isBulkEditInProgress=isBulkEditInProgress
    showSuccessMessage=showBulkEditMessage}}`);

  run(() => {
    incFourNintyOne.set('checked', true);
    done();
  });

  this.$('.rsa-content-help-trigger.rsa-form-status-select .rsa-form-button').click();
  wait().then(() => {
    this.$('ul.rsa-form-status-select li[value="2"]').click();

    this.$('.rsa-content-help-trigger.rsa-form-assignee-select .rsa-form-button').click();
    wait().then(() => {
      this.$('ul.rsa-form-assignee-select li[value="2"]').click();

      this.$('.rsa-content-help-trigger.rsa-form-priority-select .rsa-form-button').click();
      wait().then(() => {
        this.$('ul.rsa-form-priority-select li[value="2"]').click();

        wait().then(() => {
          this.$('.rsa-bulk-edit-button-group:nth-child(2) .rsa-form-button-wrapper:first .rsa-form-button').click();
          assert.equal(this.$('.rsa-bulk-edit-update-message').hasClass('is-shown'), false, 'The bulk edit message is visible');
          assert.equal(this.$('.rsa-bulk-edit-update-message').text().trim(), '1 record updated successfully', 'The bulk edit message shows one item updated.');
          done();
        });
        done();
      });
      done();
    });

    done();
  });
});

import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import Ember from 'ember';
import IncidentsCube from 'sa/utils/cube/incidents';
import wait from 'ember-test-helpers/wait';
import { clickTrigger, nativeMouseUp } from '../../../../../../../helpers/ember-power-select';

const {
  run,
  Object: EmberObject
} = Ember;

moduleForComponent('rsa-respond/landing-page/respond-index/list-view/bulk-edit-bar', 'Integration | Component | rsa respond/landing page/respond index/list view/bulk edit bar', {
  integration: true,

  beforeEach() {
    const incidentCube = IncidentsCube.create({
      array: []
    });

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
      EmberObject.create({ id: '1', name: 'User 1', email: 'user1@rsa.com' }),
      EmberObject.create({ id: '2', name: 'User 2', email: 'user2@rsa.com' }),
      EmberObject.create({ id: '3', name: 'User 3', email: 'user3@rsa.com' })
    ];

    this.setProperties({
      incidents: incidentCube.get('records'),
      isBulkEditInProgress: false,
      showBulkEditMessage: false,
      users
    });
  }
});

test('The rsa-bulk-edit-bar component renders with the proper elements.', function(assert) {
  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view/bulk-edit-bar
    users=users
    incidents=incidents
    saveAction=bulkSaveAction
    deleteAction=bulkDeleteAction
    isBulkEditInProgress=isBulkEditInProgress
    showMessage=showBulkEditMessage}}`);

  assert.equal(this.$('.rsa-bulk-edit-options-group').length, 1, 'Selector group is renderer.');
  assert.equal(this.$('.rsa-bulk-edit-button-group').length, 1, 'Button group was found.');

  // First Button Group
  // Status
  assert.equal(this.$('.rsa-bulk-edit-options-group .rsa-form-status-select').length, 1, 'Status selector was found.');
  assert.equal(this.$('.rsa-bulk-edit-options-group .rsa-form-status-select .ember-power-select-placeholder').text().trim(), 'Status', 'The label for the select status dropdown is correct.');

  // Assignee
  assert.equal(this.$('.rsa-bulk-edit-options-group .rsa-form-assignee-select').length, 1, 'Assignee selector was found.');
  assert.equal(this.$('.rsa-bulk-edit-options-group .rsa-form-assignee-select .ember-power-select-placeholder').text().trim(), 'Assignee', 'The label for the select assignee dropdown is correct.');

  // Priority
  assert.equal(this.$('.rsa-bulk-edit-options-group .rsa-form-priority-select').length, 1, 'Priority selector was found.');
  assert.equal(this.$('.rsa-bulk-edit-options-group .rsa-form-priority-select .ember-power-select-placeholder').text().trim(), 'Priority', 'The label for the select priority dropdown is correct.');

  // Second Button Group
  assert.equal(this.$('.rsa-bulk-edit-button-group .rsa-form-button-wrapper button.rsa-form-button').length, 3, 'Two buttons within the second button group exist.');
  assert.equal(this.$('.rsa-bulk-edit-button-group .rsa-bulk-edit-button-group__save-btn').text().trim(), 'Save', 'The save button exists and has the proper "Save" label.');
  assert.equal(this.$('.rsa-bulk-edit-button-group .rsa-bulk-edit-button-group__cancel-btn').text().trim(), 'Cancel', 'The cancel button exists and has the proper "Cancel" label.');
  assert.equal(this.$('.rsa-bulk-edit-button-group .rsa-bulk-edit-button-group__delete-btn').text().trim(), 'Delete', 'The delete button exists and has the proper "Delete" label.');
  assert.equal(this.$('.rsa-bulk-edit-button-group .rsa-bulk-edit-message').length, 1, 'The update message element was found.');
  assert.notOk(this.$('.rsa-bulk-edit-button-group .rsa-bulk-edit-message').hasClass('is-shown'), 'The update message is not visible');
});

test('The rsa-bulk-edit button select lists are active when an incident is checked.', function(assert) {
  const incFourNintyOne = this.get('incidents').findBy('id', 'INC-491');

  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view/bulk-edit-bar
    users=users
    incidents=incidents
    saveAction=bulkSaveAction
    deleteAction=bulkDeleteAction
    isBulkEditInProgress=isBulkEditInProgress
    showMessage=showBulkEditMessage}}`);

  assert.ok(this.$('.rsa-form-status-select .ember-power-select-trigger').attr('aria-disabled'), 'The status selector is not currently enabled');
  assert.ok(this.$('.rsa-form-assignee-select .ember-power-select-trigger').attr('aria-disabled'), 'The assignee selector is not currently enabled');
  assert.ok(this.$('.rsa-form-priority-select .ember-power-select-trigger').attr('aria-disabled'), 'The priority selector is not currently enabled');

  run(() => {
    incFourNintyOne.set('checked', true);
  });

  assert.notOk(this.$('.rsa-form-status-select .ember-power-select-trigger').attr('aria-disabled'), 'The status selector is enabled');
  assert.notOk(this.$('.rsa-form-assignee-select .ember-power-select-trigger').attr('aria-disabled'), 'The assignee selector is enabled');
  assert.notOk(this.$('.rsa-form-priority-select .ember-power-select-trigger').attr('aria-disabled'), 'The priority selector is enabled');
});

test('Only the rsa-bulk-edit status select list is active when a closed incident is checked.', function(assert) {
  const incFourNintyTwo = this.get('incidents').findBy('id', 'INC-492');

  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view/bulk-edit-bar
    users=users
    incidents=incidents
    saveAction=bulkSaveAction
    deleteAction=bulkDeleteAction
    isBulkEditInProgress=isBulkEditInProgress
    showMessage=showBulkEditMessage}}`);

  assert.ok(this.$('.rsa-form-status-select .ember-power-select-trigger').attr('aria-disabled'), 'The status selector is not currently enabled');
  assert.ok(this.$('.rsa-form-assignee-select .ember-power-select-trigger').attr('aria-disabled'), 'The assignee selector is not currently enabled');
  assert.ok(this.$('.rsa-form-priority-select .ember-power-select-trigger').attr('aria-disabled'), 'The priority selector is not currently enabled');

  run(() => {
    incFourNintyTwo.set('statusSort', 5);
    incFourNintyTwo.set('checked', true);
  });

  assert.notOk(this.$('.rsa-form-status-select .ember-power-select-trigger').attr('aria-disabled'), 'The status selector is now enabled');
  assert.ok(this.$('.rsa-form-assignee-select .ember-power-select-trigger').attr('aria-disabled'), 'The assignee selector remains disabled');
  assert.ok(this.$('.rsa-form-priority-select .ember-power-select-trigger').attr('aria-disabled'), 'The priority selector remains disabled');
});

test('The Save and Cancel buttons in the rsa-bulk-edit bar should appear when a value is chosen from a select list', function(assert) {
  const done = assert.async(2);
  const incFourNintyOne = this.get('incidents').findBy('id', 'INC-491');

  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view/bulk-edit-bar
    users=users
    incidents=incidents
    saveAction=bulkSaveAction
    deleteAction=bulkDeleteAction
    isBulkEditInProgress=isBulkEditInProgress
    showMessage=showBulkEditMessage}}`);

  assert.ok(this.$('.rsa-bulk-edit-button-group .rsa-bulk-edit-button-group__save-btn').hasClass('is-hidden'), 'The Save button is currently hidden.');
  assert.ok(this.$('.rsa-bulk-edit-button-group .rsa-bulk-edit-button-group__cancel-btn').hasClass('is-hidden'), 'The Cancel button is currently hidden.');

  run(() => {
    incFourNintyOne.set('checked', true);
    done();
  });

  // changing any value
  clickTrigger();
  nativeMouseUp('.ember-power-select-option:eq(0)');

  wait().then(() => {
    assert.notOk(this.$('.rsa-bulk-edit-button-group .rsa-form-button-wrapper:first').hasClass('is-hidden'), 'The Save button is currently not hidden.');
    assert.notOk(this.$('.rsa-bulk-edit-button-group .rsa-form-button-wrapper:nth-child(2)').hasClass('is-hidden'), 'The Cancel button is currently not hidden.');
    done();
  });
});

test('Clicking the Save button after selecting values successfully saves the incident.', function(assert) {
  const done = assert.async(2);
  const incFourNintyOne = this.get('incidents').findBy('id', 'INC-491');

  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view/bulk-edit-bar
    users=users
    incidents=incidents
    saveAction=bulkSaveAction
    deleteAction=bulkDeleteAction
    isBulkEditInProgress=isBulkEditInProgress
    showMessage=showBulkEditMessage}}`);

  run(() => {
    incFourNintyOne.set('checked', true);
    done();
  });

  // chaging the status
  clickTrigger('.rsa-form-status-select');
  nativeMouseUp('.ember-power-select-option:eq(0)');

  // chaging the assignee
  clickTrigger('.rsa-form-assignee-select');
  nativeMouseUp('.ember-power-select-option:eq(0)');

  // chaging the priority
  clickTrigger('.rsa-form-priority-select');
  nativeMouseUp('.ember-power-select-option:eq(0)');

  wait().then(() => {
    this.$('.rsa-bulk-edit-button-group .rsa-bulk-edit-button-group__save-btn .rsa-form-button').click();
    assert.ok(this.$('.rsa-bulk-edit-message').hasClass('is-shown'), 'The bulk edit message is visible');
    assert.equal(this.$('.rsa-bulk-edit-message').text().trim(), '1 record updated successfully', 'The bulk edit message shows one item updated.');
    done();
  });
});

test('Selecting an incident and clicking the delete button will display the "Incident deleted" message.', function(assert) {
  const done = assert.async(3);
  const incFourNintyOne = this.get('incidents').findBy('id', 'INC-491');

  this.render(hbs`<div id='modalDestination'></div>{{rsa-respond/landing-page/respond-index/list-view/bulk-edit-bar
    users=users
    incidents=incidents
    saveAction=bulkSaveAction
    deleteAction=bulkDeleteAction
    isBulkEditInProgress=isBulkEditInProgress
    showMessage=showBulkEditMessage}}`);

  assert.ok(this.$('.rsa-bulk-edit-button-group .rsa-bulk-edit-button-group__delete-btn').hasClass('is-disabled'), 'The delete button is currently disabled.');

  run(() => {
    incFourNintyOne.set('checked', true);
    done();
  });

  assert.notOk(this.$('.rsa-bulk-edit-button-group .rsa-bulk-edit-button-group__delete-btn').hasClass('is-disabled'), 'The delete button is currently enabled.');

  wait().then(() => {
    this.$('.rsa-bulk-edit-button-group .rsa-bulk-edit-button-group__delete-btn .rsa-form-button').click();

    wait().then(() => {
      assert.equal(this.$('.rsa-respond-bulk-edit-delete-confirm:first').length, 1, 'The delete confirmation modal has appeared.');
      assert.equal(this.$('.rsa-respond-bulk-edit-delete-confirm:first p').text().trim(), 'Please confirm you want to delete this incident. Once this incident is deleted, it cannot be recovered.', 'The proper messaging is displayed.');
      assert.equal(this.$('.rsa-respond-bulk-edit-delete-confirm:first .rsa-form-button-wrapper:first .rsa-form-button').text().trim(), 'Delete', 'The delete button appears with the proper label.');
      assert.equal(this.$('.rsa-respond-bulk-edit-delete-confirm:first .rsa-form-button-wrapper:last-child .rsa-form-button').text().trim(), 'Cancel', 'The cancel button appears with the proper label.');

      this.$('.rsa-respond-bulk-edit-delete-confirm:first .rsa-form-button-wrapper:first .rsa-form-button').click();

      assert.equal(this.$('.rsa-bulk-edit-message').text().trim(), '1 incident successfully deleted', 'The proper success message is shown.');
      done();
    });
    done();
  });
});

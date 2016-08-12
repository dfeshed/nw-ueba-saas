import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import Ember from 'ember';

const { Object: EmberObject } = Ember;

moduleForComponent('rsa-journal-entry', 'Integration | Component | rsa journal entry', {
  integration: true
});

test('it renders default elements', function(assert) {

  let journalEntry = EmberObject.create({
    'id': '10',
    'note': 'Checked on the accounts deactivated. Called the concerned dept to validate. Attached a list of accounts for future references',
    'filenames': ['hr-passwords.pdf', 'log.txt'],
    'milestone': 'RECONNAISSANCE',
    'created': 1452485774539,
    'user': 'Tier 1 Analyst'
  });

  this.set('journalEntry', journalEntry);


  this.render(hbs`{{rsa-respond/incident-detail/journal-entry journal=journalEntry}}`);

  assert.equal(this.$('.rsa-journal-entry').length, 1, 'Journal-entry component exists.');
  assert.equal(this.$('.rsa-journal-entry__created-by').length, 1, 'Created-by element exists.');
  assert.equal(this.$('.rsa-journal-entry__note').length, 1, 'Note element exists.');
  assert.equal(this.$('.rsa-journal-entry__milestones').length, 1, 'Milestones element exists.');
  assert.equal(this.$('.rsa-journal-entry__milestones .rsa-journal-entry__milestone').length, 1, 'Milestones number of elements matches.');
  assert.equal(this.$('.rsa-journal-entry__created_time-ago').length, 1, 'Created time-ago element exists.');
  assert.equal(this.$('.rsa-journal-entry__edit-journal').length, 1, 'Edit journal button exists.');
  assert.equal(this.$('.rsa-journal-entry__edit-journal').is(':visible'), false, 'Edit journal button is hidden.');
  assert.equal(this.$('.rsa-journal-entry__delete-journal').length, 1, 'Delete journal button exists.');
  assert.equal(this.$('.rsa-journal-entry__delete-journal').is(':visible'), false, 'Delete journal button is hidden.');

  assert.equal(this.$('.rsa-journal-entry__delete-dialog').length, 1, 'Delete journal dialog exists.');
  assert.equal(this.$('.rsa-journal-entry__delete-dialog__prompt').length, 1, 'Delete journal dialog prompt exists.');
  assert.equal(this.$('.rsa-journal-entry__delete-dialog__actions').length, 1, 'Delete journal action div exists.');
  assert.equal(this.$('.rsa-journal-entry__delete-dialog__actions__confirm').length, 1, 'Delete journal confirm button exists.');
  assert.equal(this.$('.rsa-journal-entry__delete-dialog__actions__cancel').length, 1, 'Delete journal cancel button exists.');

  assert.equal(this.$('.rsa-journal-entry__edit-note').length, 0, 'Edit-Note element does not exist.');
  assert.equal(this.$('.rsa-journal-entry__edit-milestone').length, 0, 'Edit-Milestones element does not exist.');
  assert.equal(this.$('.rsa-journal-entry__actions__cancel').length, 0, 'Cancel button element does not exist.');
  assert.equal(this.$('.rsa-journal-entry__actions__save').length, 0, 'Save button element does not exist.');

});

test('it renders editable elements with addMode', function(assert) {

  let journalEntry = EmberObject.create({
    'id': '10',
    'note': 'Checked on the accounts deactivated. Called the concerned dept to validate. Attached a list of accounts for future references',
    'filenames': ['hr-passwords.pdf', 'log.txt'],
    'milestone': 'RECONNAISSANCE',
    'created': 1452485774539,
    'user': 'Tier 1 Analyst'
  });

  this.set('journalEntry', journalEntry);

  this.render(hbs`{{rsa-respond/incident-detail/journal-entry journal=journalEntry addMode=true}}`);

  assert.equal(this.$('.rsa-journal-entry').length, 1, 'Journal-entry component exists.');
  assert.equal(this.$('.rsa-journal-entry__edit-note').length, 1, 'Note element exists.');
  assert.equal(this.$('.rsa-journal-entry__edit-milestone').length, 1, 'Milestones element exists.');
  assert.equal(this.$('.rsa-journal-entry__actions__cancel').length, 1, 'Cancel button element exists.');
  assert.equal(this.$('.rsa-journal-entry__actions__save').length, 1, 'Save button element exists.');

  assert.notOk(this.$('.rsa-journal-entry__actions__save').is(':visible'), 'Save button is not visible.');

  this.$('.rsa-journal-entry').trigger('click');

  assert.ok(this.$('.rsa-journal-entry__actions__save').is(':visible'), 'Save button is visible.');
});

test('Elements are not visible by default with addMode', function(assert) {

  let journalEntry = EmberObject.create({
    'id': '10',
    'note': 'Checked on the accounts deactivated. Called the concerned dept to validate. Attached a list of accounts for future references',
    'filenames': ['hr-passwords.pdf', 'log.txt'],
    'milestone': 'RECONNAISSANCE',
    'created': 1452485774539,
    'user': 'Tier 1 Analyst'
  });

  this.set('journalEntry', journalEntry);

  this.render(hbs`{{rsa-respond/incident-detail/journal-entry journal=journalEntry addMode=true}}`);

  assert.notOk(this.$('.rsa-journal-entry__actions__save').is(':visible'), 'Note element is not visible.');
  assert.notOk(this.$('.rsa-journal-entry__edit-milestone').is(':visible'), 'Milestones element is not visible.');
  assert.notOk(this.$('.rsa-journal-entry__actions__cancel').is(':visible'), 'Cancel button is not visible.');
  assert.notOk(this.$('.rsa-journal-entry__actions__save').is(':visible'), 'Save button is not visible.');

  this.$('.rsa-journal-entry').trigger('click');

  assert.ok(this.$('.rsa-journal-entry__actions__save').is(':visible'), 'Note element is visible.');
  assert.ok(this.$('.rsa-journal-entry__edit-milestone').is(':visible'), 'Milestones element is visible.');
  assert.ok(this.$('.rsa-journal-entry__actions__cancel').is(':visible'), 'Cancel button is visible.');
  assert.ok(this.$('.rsa-journal-entry__actions__save').is(':visible'), 'Save button is visible.');
});

test('It switches to edit mode after clicking the edit button', function(assert) {

  let journalEntry = EmberObject.create({
    'id': '10',
    'note': 'Checked on the accounts deactivated. Called the concerned dept to validate. Attached a list of accounts for future references',
    'filenames': ['hr-passwords.pdf', 'log.txt'],
    'milestone': 'RECONNAISSANCE',
    'created': 1452485774539,
    'user': 'Tier 1 Analyst'
  });

  this.set('journalEntry', journalEntry);

  this.render(hbs`{{rsa-respond/incident-detail/journal-entry journal=journalEntry}}`);

  assert.equal(this.$('.rsa-journal-entry__edit-note').length, 0, 'Edit-Note element doesnt exist.');
  assert.equal(this.$('.rsa-journal-entry__edit-milestone').length, 0, 'Edit-Milestones element doesnt exist.');
  assert.equal(this.$('.rsa-journal-entry__actions__cancel').length, 0, 'Cancel button element doesnt exist.');
  assert.equal(this.$('.rsa-journal-entry__actions__save').length, 0, 'Save button element doesnt exist.');

  this.$('.rsa-journal-entry__edit-journal').trigger('click');

  assert.ok(this.$('.rsa-journal-entry__actions__save').is(':visible'), 'Note element is visible.');
  assert.ok(this.$('.rsa-journal-entry__edit-milestone').is(':visible'), 'Milestones element is visible.');
  assert.ok(this.$('.rsa-journal-entry__actions__cancel').is(':visible'), 'Cancel button is visible.');
  assert.ok(this.$('.rsa-journal-entry__actions__save').is(':visible'), 'Save button is visible.');
});

test('Test delete confirm dialog shows up when clicking the delete button', function(assert) {

  let journalEntry = EmberObject.create({
    'id': '10',
    'note': 'Checked on the accounts deactivated. Called the concerned dept to validate. Attached a list of accounts for future references',
    'filenames': ['hr-passwords.pdf', 'log.txt'],
    'milestone': 'RECONNAISSANCE',
    'created': 1452485774539,
    'user': 'Tier 1 Analyst'
  });

  this.set('journalEntry', journalEntry);
  this.render(hbs`<div id='modalDestination'></div>{{rsa-respond/incident-detail/journal-entry journal=journalEntry}}`);

  assert.notOk(this.$('.rsa-journal-entry__delete-dialog').is(':visible'), 'Delete dialog is not visible by default.');

  this.$('.rsa-journal-entry__delete-journal').click();
  assert.equal(this.$('.rsa-journal-entry__delete-dialog').length, 1);

});
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import Ember from 'ember';

const { Object: EmberObject } = Ember;

moduleForComponent('rsa-journal-entry', 'Integration | Component | rsa journal wrapper', {
  integration: true
});

test('it renders default elements', function(assert) {

  let incident = EmberObject.create({
    'id': '10',
    'note': 'Checked on the accounts deactivated. Called the concerned dept to validate. Attached a list of accounts for future references',
    'filenames': ['hr-passwords.pdf', 'log.txt'],
    'milestone': 'RECONNAISSANCE',
    'created': 1452485774539,
    'user': 'Tier 1 Analyst',
    notes: [{
      author: 'admin',
      milestone: 'CONTAINMENT',
      notes: 'some text'
    }, {
      author: 'admin',
      milestone: 'CONTAINMENT',
      notes: 'some text 2'
    }]
  });

  this.set('incident', incident);


  this.render(hbs`{{rsa-respond/incident-detail/journal-wrapper incident=incident}}`);

  assert.equal(this.$('.rsa-journal-wrapper').length, 1, 'Journal-wrapper component exists.');
  assert.equal(this.$('.rsa-journal-wrapper__title').length, 1, 'Title element exists.');
  assert.equal(this.$('.rsa-journal-wrapper__filter-button').length, 1, 'Filter button element exists.');
  assert.equal(this.$('.rsa-journal-wrapper__journal-sort').length, 1, 'Sort panel element exists.');
  assert.equal(this.$('.rsa-journal-entry.add-mode').length, 1, 'It renders one journal entry in add-mode to allow enter a new Journal Entry.');
  assert.equal(this.$('.rsa-journal-wrapper__journal-viewport').length, 1, 'Journal viewport element exists.');
  assert.equal(this.$('.rsa-journal-wrapper__journal-viewport .rsa-journal-entry').length, 2, 'Renders the right number of journal entries');

});

test('Filter bar can be visible', function(assert) {

  let incident = EmberObject.create({
    'id': '10',
    'note': 'Checked on the accounts deactivated. Called the concerned dept to validate. Attached a list of accounts for future references',
    'filenames': ['hr-passwords.pdf', 'log.txt'],
    'milestone': 'RECONNAISSANCE',
    'created': 1452485774539,
    'user': 'Tier 1 Analyst',
    notes: []
  });

  this.set('incident', incident);

  this.render(hbs`{{rsa-respond/incident-detail/journal-wrapper incident=incident}}`);

  let sortPanel = this.$('.rsa-journal-wrapper__journal-sort');
  assert.ok(sortPanel.hasClass('hidden'), 'Sort panel is hidden by default');

  this.$('.rsa-journal-wrapper__filter-button').trigger('click');

  assert.notOk(sortPanel.hasClass('hidden'), 'Sort panel is visible');

});

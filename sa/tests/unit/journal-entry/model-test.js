import { moduleForModel, test } from 'ember-qunit';

moduleForModel('journal-entry', 'Unit | Model | journal entry', {
  // Specify the other units that are required for this test.
  needs: []
});

test('it exists', function(assert) {
  let model = this.subject();
  assert.ok(!!model);
});


test('check model values', function(assert) {

  let myModel = {
    journalId: '1',
    incidentId: 'INC-501',
    journalMap: {
      notes: 'my note',
      milestone: 'DELIVERY',
      author: 'admin'
    }
  };

  let model = this.subject(myModel);

  assert.equal(model.get('journalId'), '1', 'Valid JournalId is returned.');
  assert.equal(model.get('incidentId'), 'INC-501', 'Valid IncidentId is returned');
  assert.equal(model.get('journalMap.notes'), 'my note', 'Valid notes are returned.');
  assert.equal(model.get('journalMap.author'), 'admin', 'Valid author is returned.');
  assert.equal(model.get('journalMap.milestone'), 'DELIVERY', 'Valid milestone is returned.');
});

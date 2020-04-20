import { test, moduleFor } from 'ember-qunit';
import { patchSocket } from '../../../helpers/patch-socket';
import incidentRules from 'configure/actions/api/respond/incident-rules';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

moduleFor('service:request', {
  beforeEach() {
    initialize(this);
  }
});

test('it creates the proper query for the getIncidentRules API function', function(assert) {
  assert.expect(3);
  patchSocket((method, modelName, query) => {
    assert.equal(method, 'findAll');
    assert.equal(modelName, 'incident-rules');
    assert.deepEqual(query, {});
  });
  incidentRules.getIncidentRules();
});

test('it creates the proper query for the getIncidentFields API function', function(assert) {
  assert.expect(3);
  patchSocket((method, modelName, query) => {
    assert.equal(method, 'findAll');
    assert.equal(modelName, 'incident-fields');
    assert.deepEqual(query, {});
  });
  incidentRules.getIncidentFields();
});

test('it creates the proper query for the getIncidentRule API function', function(assert) {
  const ruleId = 'ABCiseasyas123';
  assert.expect(3);
  patchSocket((method, modelName, query) => {
    assert.equal(method, 'queryRecord');
    assert.equal(modelName, 'incident-rules');
    assert.deepEqual(query, {
      data: {
        id: ruleId
      }
    });
  });
  incidentRules.getIncidentRule(ruleId);
});

test('it creates the proper query for the deleteIncidentRule API function', function(assert) {
  const ruleId = 'ABCiseasyas123';
  assert.expect(3);
  patchSocket((method, modelName, query) => {
    assert.equal(method, 'deleteRecord');
    assert.equal(modelName, 'incident-rules');
    assert.deepEqual(query, {
      data: {
        id: ruleId
      }
    });
  });
  incidentRules.deleteIncidentRule(ruleId);
});

test('it creates the proper query for the cloneIncidentRule API function', function(assert) {
  const ruleId = 'originalityIsUndetectedPlagiarism';
  assert.expect(3);
  patchSocket((method, modelName, query) => {
    assert.equal(method, 'createRecord');
    assert.equal(modelName, 'incident-rule-clone');
    assert.deepEqual(query, {
      data: {
        id: ruleId
      }
    });
  });
  incidentRules.cloneIncidentRule(ruleId);
});

test('it creates the proper query for the createIncidentRule API function', function(assert) {
  const newRule = {
    name: 'Look but don\'t touch'
  };
  assert.expect(3);
  patchSocket((method, modelName, query) => {
    assert.equal(method, 'createRecord');
    assert.equal(modelName, 'incident-rules');
    assert.deepEqual(query, {
      data: newRule
    });
  });
  incidentRules.createIncidentRule(newRule);
});

test('it creates the proper query for the reorderIncidentRules API function', function(assert) {
  const ruleIds = ['A', 'B', 'D', 'C'];
  assert.expect(3);
  patchSocket((method, modelName, query) => {
    assert.equal(method, 'updateRecord');
    assert.equal(modelName, 'incident-rules-reorder');
    assert.deepEqual(query, {
      data: ruleIds
    });
  });
  incidentRules.reorderIncidentRules(ruleIds);
});

test('it creates the proper query for the saveIncidentRule API function', function(assert) {
  const rule = {
    name: 'XYZ 123'
  };
  assert.expect(3);
  patchSocket((method, modelName, query) => {
    assert.equal(method, 'updateRecord');
    assert.equal(modelName, 'incident-rules');
    assert.deepEqual(query, {
      data: rule
    });
  });
  incidentRules.saveIncidentRule(rule);
});

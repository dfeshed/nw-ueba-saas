import { Promise } from 'rsvp';
import { module, test } from 'qunit';
import { patchFetch } from '../../helpers/patch-fetch';
import { setupTest } from 'ember-qunit';
import data from '../../helpers/actions-data';
import { waitFor } from 'ember-wait-for-test-helper/wait-for';

const assertForEventAnalysisPanelIPActions = (actions, assert) => {
  assert.equal(actions.length, 4, 'Should have minimum four actions');
  assert.ok(actions.find((action) => action.label === 'copyMetaAction'), 'Should have copy action');
};

const assertForEventAnalysisPanelDefaultActions = (actions, assert) => {
  assert.equal(actions.length, 3, 'Should have minimum three actions');
  assert.ok(actions.find((action) => action.label === 'nw-event-value-drillable-contains'), 'Should have copy action');
};

const assertForNonSupportedActions = (actions, assert) => {
  assert.ok(data.data.find((action) => action.id === 'contextServiceDefaultAction'), 'Should have contextServiceDefaultAction action');
  assert.notOk(actions.find((action) => action.id === 'contextServiceDefaultAction'), 'Should have contextServiceDefaultAction action');
};

let fetchResolved;

module('Unit | Service | contextual-actions', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    fetchResolved = false;
    patchFetch(() => {
      return new Promise(function(resolve) {
        resolve({
          ok: true,
          json() {
            return new Promise(function(r) {
              r(data);
              fetchResolved = true;
            });
          }
        });
      });
    });
  });

  test('initialize service with proper input', async function(assert) {
    const service = this.owner.lookup('service:contextual-actions');

    await waitFor(() => fetchResolved === true);

    assertForEventAnalysisPanelIPActions(service.getContextualActionsForGivenScope('EventAnalysisPanel', 'ip.src'), assert);
    assertForEventAnalysisPanelDefaultActions(service.getContextualActionsForGivenScope('EventAnalysisPanel', 'test', 'Text'), assert);
    assertForNonSupportedActions(service.getContextualActionsForGivenScope('EventAnalysisPanel', 'test'), assert);
  });

});

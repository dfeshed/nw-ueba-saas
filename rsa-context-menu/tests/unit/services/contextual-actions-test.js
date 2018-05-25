import { Promise } from 'rsvp';
import { module, test } from 'qunit';
import { patchFetch } from '../../helpers/patch-fetch';
import { setupTest } from 'ember-qunit';
import data from '../../helpers/actions-data';
import { next } from '@ember/runloop';

let serv = null;

const assertForEventGridIPActions = (actions, assert) => {
  assert.equal(actions.length, 3, 'Should have minimum three actions');
  assert.ok(actions.find((action) => action.label === 'copyMetaAction'), 'Should have copy action');
};

const assertForEventGridDefaultActions = (actions, assert) => {
  assert.equal(actions.length, 2, 'Should have minimum three actions');
  assert.ok(actions.find((action) => action.label === 'copyMetaAction'), 'Should have copy action');
};

const assertForNonSupportedActions = (actions, assert) => {
  assert.ok(data.data.find((action) => action.id === 'contextServiceDefaultAction'), 'Should have contextServiceDefaultAction action');
  assert.notOk(actions.find((action) => action.id === 'contextServiceDefaultAction'), 'Should have contextServiceDefaultAction action');
};

module('Unit | Service | contextual-actions', function(hooks) {
  setupTest(hooks);

  test('initialize service with proper input', async function(assert) {
    patchFetch(() => {
      return new Promise(function(resolve) {
        resolve({
          ok: true,
          json() {
            return new Promise(function(r) {
              r(data);
            });
          }
        });
      });
    });
    serv = this.owner.lookup('service:contextual-actions');
    assert.ok(serv);
    next(() => {
      assertForEventGridIPActions(serv.getContextualActionsForGivenScope('EventGrid', 'ip.src'), assert);
      assertForEventGridDefaultActions(serv.getContextualActionsForGivenScope('EventGrid', 'test'), assert);
      assertForNonSupportedActions(serv.getContextualActionsForGivenScope('EventGrid', 'test'), assert);
    });
  });

});
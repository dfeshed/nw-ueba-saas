import { module, test } from 'qunit';
import Service from '@ember/service';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { computed } from '@ember/object';
import { settled, waitUntil } from '@ember/test-helpers';
import IncidentRulesRoute from 'configure/routes/respond/incident-rules';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { patchFlash } from '../../../../../helpers/patch-flash';
import { normalizedState } from '../../../../../integration/component/respond/risk-scoring/data';
import { throwSocket } from '../../../../../helpers/patch-socket';
import { t } from '../../../../../integration/component/respond/risk-scoring/helpers';

let redux;

module('Unit | Route | respond/incident-rules', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  const setupRoute = function() {
    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'respond/incident-rules'
    }));
    redux = this.owner.lookup('service:redux');
    const PatchedRoute = IncidentRulesRoute.extend({
      redux: computed(function() {
        return redux;
      })
    });
    return PatchedRoute.create();
  };

  test('model hook should fetch risk scoring settings', async function(assert) {
    assert.expect(1);

    patchReducer(this, {});
    const route = setupRoute.call(this);

    await route.model();

    await waitUntil(() => {
      const { configure: { respond: { riskScoring: { riskScoringStatus, riskScoringSettings } } } } = redux.getState();
      const riskScoringHydrated = riskScoringSettings && Object.keys(riskScoringSettings).length === 2;
      const riskScoringStatusComplete = riskScoringStatus && riskScoringStatus === 'completed';
      if (riskScoringHydrated && riskScoringStatusComplete) {
        assert.deepEqual(normalizedState.configure.respond.riskScoring.riskScoringSettings, riskScoringSettings);
      }
      return riskScoringHydrated && riskScoringStatusComplete;
    });
  });

  test('model hook will flash error message when fetch risk scoring settings fails', async function(assert) {
    assert.expect(3);

    const done = throwSocket({ methodToThrow: 'findAll', modelNameToThrow: 'risk-scoring-settings' });

    patchFlash((flash) => {
      assert.equal(flash.type, 'error');
      assert.equal(flash.message.string, t(this, 'actionMessages.fetchFailure'));
    });

    patchReducer(this, {});
    const route = setupRoute.call(this);

    await route.model();

    await waitUntil(() => {
      const { configure: { respond: { riskScoring: { riskScoringStatus } } } } = redux.getState();
      const riskScoringStatusError = riskScoringStatus && riskScoringStatus === 'error';
      if (riskScoringStatusError) {
        assert.ok(riskScoringStatusError, 'Upon failure the risk scoring status was updated');
      }
      return riskScoringStatusError;
    });

    await settled().then(() => done());
  });

});

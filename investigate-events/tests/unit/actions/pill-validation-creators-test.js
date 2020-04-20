import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import validationCreators from 'investigate-events/actions/pill-validation-creators';
import ACTION_TYPES from 'investigate-events/actions/types';
import { QueryFilter } from 'investigate-events/util/filter-types';
import { patchSocket } from '../../helpers/patch-socket';
import { patchReducer } from '../../helpers/vnext-patch';
import { bindActionCreators } from 'redux';
import ReduxDataHelper from '../../helpers/redux-data-helper';
import { settled } from '@ember/test-helpers';

// Client and server validations for single pills are being tested as part of add/edit guided pills in pill-creators-test
module('Unit | Actions | Pill Validation Creators', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('_serverSideValidation will flag isInvalid and add a validation error in state from the response returned from web socket', async function(assert) {
    assert.expect(5);
    const done = assert.async();

    const mockedPillsData = [
      {
        meta: 'action',
        operator: '=',
        value: 'xxx',
        id: 1
      }
    ];
    patchSocket((method, modelName, query) => {
      assert.equal(modelName, 'core-queries-validate');
      assert.equal(method, 'query');
      assert.deepEqual(query, {
        data: {
          endpointId: 1,
          queries: ['action%20%3D%20xxx']
        }
      });
    });

    new ReduxDataHelper((state) => patchReducer(this, state)).language().hasSummaryData(true, 1).pillsDataPopulated(mockedPillsData).build();

    const redux = this.owner.lookup('service:redux');
    const init = bindActionCreators(validationCreators._serverSideValidation, redux.dispatch.bind(redux));

    const position = 0;
    init({
      meta: 'action',
      operator: '=',
      value: 'xxx',
      id: 1
    }, position);

    return settled().then(() => {
      const { investigate: { queryNode: { pillsData } } } = redux.getState();
      const currentPill = pillsData[position];
      assert.ok(currentPill.isInvalid, 'Expected error flag');
      assert.equal(currentPill.validationError.message, 'Server error', 'Excpected server validation error');
      done();
    });
  });

  test('validateDispatch action creator returns proper type and payload', function(assert) {
    assert.expect(5);
    // pillsData: // [ { pillData, position } ]
    const p1 = { type: QueryFilter, a: 'a' };
    const p2 = { type: QueryFilter, a: 'b' };

    const pills = [
      {
        pillData: p1,
        position: 0
      },
      {
        pillData: p2,
        position: 1
      }
    ];

    const action = validationCreators._validateDispatch(pills, false);

    assert.equal(action.type, ACTION_TYPES.BATCH_VALIDATE_GUIDED_PILL, 'action has the correct type');
    const { pillsData } = action.payload;
    assert.deepEqual(pillsData[0].pillData, p1, 'action pillData has the right value');
    assert.equal(pillsData[0].position, 0, 'action pillData has the right value');
    assert.deepEqual(pillsData[1].pillData, p2, 'action pillData has the right value');
    assert.ok(pillsData[1].position, 1, 'action position has the right value');
  });

  test('VALIDATION_IN_PROGRESS action creator returns proper type and payload', function(assert) {

    // pillsData: // [ { pillData, position } ]
    const p1 = { type: QueryFilter, a: 'a' };
    const p2 = { type: QueryFilter, a: 'b' };

    const pillsData = [
      {
        pillData: p1,
        position: 0
      },
      {
        pillData: p2,
        position: 1
      }
    ];

    const action = validationCreators._validationFlagUpdate(pillsData, false, false);

    assert.equal(action.type, ACTION_TYPES.VALIDATION_IN_PROGRESS, 'action has the correct type');
    const { validationFlag, positionArray } = action.payload;
    assert.deepEqual(positionArray, [0, 1], 'position array contains the correct pill positions');
    assert.notOk(validationFlag, 'Correct flag should be passed in');
  });

});
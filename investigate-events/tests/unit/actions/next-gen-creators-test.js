import { module, test } from 'qunit';
import { bindActionCreators } from 'redux';
import { settled } from '@ember/test-helpers';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import ReduxDataHelper from '../../helpers/redux-data-helper';
import { throwSocket } from '../../helpers/patch-socket';
import { patchReducer } from '../../helpers/vnext-patch';
import { invalidServerResponse } from './data';
import nextGenCreators from 'investigate-events/actions/next-gen-creators';
import ACTION_TYPES from 'investigate-events/actions/types';


module('Unit | Actions | NextGen Creators', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('addNextGenPill action creator returns proper type and payload', function(assert) {
    assert.expect(5);
    const done = assert.async();
    const getState = () => {
      return new ReduxDataHelper().language().invalidPillsDataPopulated().build();
    };

    const myDispatch = (action) => {
      if (typeof action === 'function') {
        action(validateDispatch, getState);
      } else {
        assert.equal(action.type, ACTION_TYPES.ADD_NEXT_GEN_PILL, 'action has the correct type');
        assert.deepEqual(action.payload.pillData, { meta: 'ip.proto', operator: '=', value: 'boom' }, 'action pillData has the right value');
        assert.equal(action.payload.position, 0, 'action position has the right value');
      }
    };

    const validateDispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.VALIDATE_NEXT_GEN_PILL, 'action has the correct type - validate');
      action.promise.catch((error) => {
        assert.equal(error.meta, 'You must enter an 8 bit Integer.', 'Expected validaiton error');
        done();
      });
    };

    // this thunk will shoot out 2 actions - one to add and one to validate
    const thunk = nextGenCreators.addNextGenPill({
      pillData: {
        meta: 'ip.proto',
        operator: '=',
        value: 'boom'
      },
      position: 0
    });
    thunk(myDispatch);
  });

  test('editNextGenPill action creator returns proper type and payload', function(assert) {
    assert.expect(4);
    const done = assert.async();
    const getState = () => {
      return new ReduxDataHelper().language().invalidPillsDataPopulated().build();
    };

    const myDispatch = (action) => {
      if (typeof action === 'function') {
        action(validateDispatch, getState);
      } else {
        assert.equal(action.type, ACTION_TYPES.EDIT_NEXT_GEN_PILL, 'action has the correct type');
        assert.deepEqual(action.payload.pillData, { meta: 'ip.proto', operator: '=', value: 'boom' }, 'action pillData has the right value');
      }
    };

    const validateDispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.VALIDATE_NEXT_GEN_PILL, 'action has the correct type - validate');
      action.promise.catch((error) => {
        assert.equal(error.meta, 'You must enter an 8 bit Integer.', 'Expected validaiton error');
        done();
      });
    };

    // this thunk will shoot out 2 actions - one to edit and one to validate
    const thunk = nextGenCreators.editNextGenPill({
      pillData: {
        meta: 'ip.proto',
        operator: '=',
        value: 'boom'
      },
      position: 0
    });
    thunk(myDispatch);
  });

  test('deleteNextGenPill action creator returns proper types/payloads', function(assert) {
    const done = assert.async(2);
    const state = new ReduxDataHelper()
      .language()
      .pillsDataPopulated()
      .makeSelected(['1', '2'])
      .build();

    const secondDispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.DESELECT_NEXT_GEN_PILLS, 'action has the correct type');
      assert.deepEqual(action.payload.pillData, state.investigate.queryNode.pillsData, 'action pillData has the right value');
      done();
    };

    const firstDispatch = (action) => {
      // if is function, is _deselectAllNextGenPills thunk
      if (typeof action === 'function') {
        const thunk2 = action;
        const getState = () => state;
        thunk2(secondDispatch, getState);
      } else {
        assert.equal(action.type, ACTION_TYPES.DELETE_NEXT_GEN_PILLS, 'action has the correct type');
        assert.deepEqual(action.payload.pillData, ['foo'], 'action pillData has the right value');
        done();
      }
    };

    const thunk1 = nextGenCreators.deleteNextGenPill({
      pillData: 'foo'
    });

    thunk1(firstDispatch);
  });

  test('deselectNextGenPills action creator returns proper type and payload', function(assert) {
    const { pillsData } = new ReduxDataHelper()
      .pillsDataPopulated()
      .build()
      .investigate
      .queryNode;

    const action = nextGenCreators.deselectNextGenPills({
      pillData: pillsData
    });

    assert.equal(action.type, ACTION_TYPES.DESELECT_NEXT_GEN_PILLS, 'action has the correct type');
    assert.deepEqual(action.payload.pillData, pillsData, 'action pillData has the right value');
  });

  test('deselectAllNextGenPills action creator returns proper type and payload', function(assert) {
    const done = assert.async();

    const getState = () => {
      return new ReduxDataHelper().pillsDataPopulated().makeSelected(['1', '2']).build();
    };
    const { pillsData } = getState().investigate.queryNode;

    const thunk = nextGenCreators.deselectAllNextGenPills();

    const dispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.DESELECT_NEXT_GEN_PILLS, 'action has the correct type');
      assert.deepEqual(action.payload.pillData, pillsData, 'action pillData has the right value');
      done();
    };

    thunk(dispatch, getState);
  });

  test('selectNextGenPills action creator returns proper type and payload', function(assert) {
    const { pillsData } = new ReduxDataHelper()
      .pillsDataPopulated()
      .build()
      .investigate
      .queryNode;

    const action = nextGenCreators.selectNextGenPills({
      pillData: pillsData
    });

    assert.equal(action.type, ACTION_TYPES.SELECT_NEXT_GEN_PILLS, 'action has the correct type');
    assert.deepEqual(action.payload.pillData, pillsData, 'action pillData has the right value');
  });

  test('_serverSideValidation will flag isInvalid and add a validation error in state from the response returned from web socket', async function(assert) {
    assert.expect(2);

    const mockedPillsData = [
      {
        meta: 'sessionid',
        operator: '=',
        value: 242424242424242424242424,
        id: 1
      }
    ];

    const done = throwSocket({ methodToThrow: 'query', modelNameToThrow: 'core-query-validate', message: invalidServerResponse });

    new ReduxDataHelper((state) => patchReducer(this, state)).language().hasSummaryData(true, 1).pillsDataPopulated(mockedPillsData).build();

    const redux = this.owner.lookup('service:redux');
    const init = bindActionCreators(nextGenCreators._serverSideValidation, redux.dispatch.bind(redux));

    const position = 0;
    init({
      meta: 'sessionid',
      operator: '=',
      value: '242424242424242424242424',
      id: 1
    }, position);

    return settled().then(() => {
      const { investigate: { queryNode: { pillsData } } } = redux.getState();
      const currentPill = pillsData[position];
      assert.ok(currentPill.isInvalid, 'Expected error flag');
      assert.equal(currentPill.validationError.message, 'expecting <comma-separated list of numeric ranges, values, or value aliases> or <comma-separated list of keys> here: \'242424242424242424242424\'', 'Excpected server validation error');
      done();
    });
  });

  test('openNextGenPillForEdit dispatches the proper events', function(assert) {
    assert.expect(4);
    const done = assert.async();
    const state = new ReduxDataHelper()
      .language()
      .pillsDataPopulated()
      .makeSelected(['1', '2'])
      .build();

    const secondDispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.DESELECT_NEXT_GEN_PILLS, 'action has the correct type');
      assert.deepEqual(action.payload.pillData, state.investigate.queryNode.pillsData, 'action pillData has the right value');
      done();
    };

    const firstDispatch = (action) => {
      if (typeof action === 'function') {
        action(secondDispatch, () => state);
      } else {
        assert.equal(action.type, ACTION_TYPES.OPEN_NEXT_GEN_PILL_FOR_EDIT, 'action has the correct type');
        assert.deepEqual(action.payload.pillData, { meta: 'ip.proto', operator: '=', value: 'boom' }, 'action pillData has the right value');
      }
    };

    const thunk = nextGenCreators.openNextGenPillForEdit({
      pillData: {
        meta: 'ip.proto',
        operator: '=',
        value: 'boom'
      },
      position: 0
    });
    thunk(firstDispatch);
  });

  test('addFreeFormFilter action creator returns proper type and payload', function(assert) {
    assert.expect(2);
    const thunk = nextGenCreators.addFreeFormFilter('medium = 50');

    const getState = () => {
      return new ReduxDataHelper().language().build();
    };

    const dispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.REPLACE_ALL_NEXT_GEN_PILLS, 'action has the correct type');
      assert.deepEqual(action.payload.pillData, [
        {
          complexFilterText: undefined,
          meta: 'medium',
          operator: '=',
          value: '50'
        }
      ], 'action pillData has the right value');
    };

    thunk(dispatch, getState);
  });

  test('updatedFreeFormText action creator returns proper type and payload', function(assert) {
    assert.expect(2);
    const thunk = nextGenCreators.updatedFreeFormText('medium = 50');

    const getState = () => {
      return new ReduxDataHelper().language().build();
    };

    const dispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.UPDATE_FREE_FORM_TEXT, 'action has the correct type');
      assert.deepEqual(action.payload.pillData, {
        complexFilterText: undefined,
        meta: 'medium',
        operator: '=',
        value: '50'
      }, 'action pillData has the right value');
    };

    thunk(dispatch, getState);
  });

  test('resetNextGenPill action creator returns proper type and payload', function(assert) {
    const action = nextGenCreators.resetNextGenPill('foo');
    assert.equal(action.type, ACTION_TYPES.RESET_NEXT_GEN_PILL, 'action has the correct type');
    assert.deepEqual(action.payload.pillData, 'foo', 'action pillData has the right value');
  });

});
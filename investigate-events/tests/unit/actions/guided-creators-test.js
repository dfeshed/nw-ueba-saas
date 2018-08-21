import { module, test } from 'qunit';
import { bindActionCreators } from 'redux';
import { settled } from '@ember/test-helpers';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import ReduxDataHelper from '../../helpers/redux-data-helper';
import { throwSocket } from '../../helpers/patch-socket';
import { patchReducer } from '../../helpers/vnext-patch';
import { invalidServerResponse } from './data';
import guidedCreators from 'investigate-events/actions/guided-creators';
import ACTION_TYPES from 'investigate-events/actions/types';


module('Unit | Actions | Guided Creators', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('addGuidedPill action creator returns proper type and payload', function(assert) {
    assert.expect(5);
    const done = assert.async();
    const getState = () => {
      return new ReduxDataHelper().language().invalidPillsDataPopulated().build();
    };

    const myDispatch = (action) => {
      if (typeof action === 'function') {
        action(validateDispatch, getState);
      } else {
        assert.equal(action.type, ACTION_TYPES.ADD_GUIDED_PILL, 'action has the correct type');
        assert.deepEqual(action.payload.pillData, { meta: 'ip.proto', operator: '=', value: 'boom' }, 'action pillData has the right value');
        assert.equal(action.payload.position, 0, 'action position has the right value');
      }
    };

    const validateDispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.VALIDATE_GUIDED_PILL, 'action has the correct type - validate');
      action.promise.catch((error) => {
        assert.equal(error.meta, 'You must enter an 8 bit Integer.', 'Expected validaiton error');
        done();
      });
    };

    // this thunk will shoot out 2 actions - one to add and one to validate
    const thunk = guidedCreators.addGuidedPill({
      pillData: {
        meta: 'ip.proto',
        operator: '=',
        value: 'boom'
      },
      position: 0
    });
    thunk(myDispatch);
  });

  test('editGuidedPill action creator returns proper type and payload', function(assert) {
    assert.expect(4);
    const done = assert.async();
    const getState = () => {
      return new ReduxDataHelper().language().invalidPillsDataPopulated().build();
    };

    const myDispatch = (action) => {
      if (typeof action === 'function') {
        action(validateDispatch, getState);
      } else {
        assert.equal(action.type, ACTION_TYPES.EDIT_GUIDED_PILL, 'action has the correct type');
        assert.deepEqual(action.payload.pillData, { meta: 'ip.proto', operator: '=', value: 'boom' }, 'action pillData has the right value');
      }
    };

    const validateDispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.VALIDATE_GUIDED_PILL, 'action has the correct type - validate');
      action.promise.catch((error) => {
        assert.equal(error.meta, 'You must enter an 8 bit Integer.', 'Expected validaiton error');
        done();
      });
    };

    // this thunk will shoot out 2 actions - one to edit and one to validate
    const thunk = guidedCreators.editGuidedPill({
      pillData: {
        meta: 'ip.proto',
        operator: '=',
        value: 'boom'
      },
      position: 0
    });
    thunk(myDispatch);
  });

  test('deleteGuidedPill action creator returns proper types/payloads', function(assert) {
    const done = assert.async(2);
    const state = new ReduxDataHelper()
      .language()
      .pillsDataPopulated()
      .markSelected(['1', '2'])
      .build();

    const secondDispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.DESELECT_GUIDED_PILLS, 'action has the correct type');
      assert.deepEqual(action.payload.pillData, state.investigate.queryNode.pillsData, 'action pillData has the right value');
      done();
    };

    const firstDispatch = (action) => {
      // if is function, is _deselectAllGuidedPills thunk
      if (typeof action === 'function') {
        const thunk2 = action;
        const getState = () => state;
        thunk2(secondDispatch, getState);
      } else {
        assert.equal(action.type, ACTION_TYPES.DELETE_GUIDED_PILLS, 'action has the correct type');
        assert.deepEqual(action.payload.pillData, 'foo', 'action pillData has the right value');
        done();
      }
    };

    const thunk1 = guidedCreators.deleteGuidedPill({
      pillData: 'foo'
    });

    thunk1(firstDispatch);
  });


  test('deselectGuidedPills action creator returns proper type and payload', function(assert) {
    const { pillsData } = new ReduxDataHelper()
      .pillsDataPopulated()
      .build()
      .investigate
      .queryNode;

    const action = guidedCreators.deselectGuidedPills({
      pillData: pillsData
    });

    assert.equal(action.type, ACTION_TYPES.DESELECT_GUIDED_PILLS, 'action has the correct type');
    assert.deepEqual(action.payload.pillData, pillsData, 'action pillData has the right value');
  });

  test('deselectAllGuidedPills action creator returns proper type and payload', function(assert) {
    const done = assert.async();

    const getState = () => {
      return new ReduxDataHelper().pillsDataPopulated().markSelected(['1', '2']).build();
    };
    const { pillsData } = getState().investigate.queryNode;

    const thunk = guidedCreators.deselectAllGuidedPills();

    const dispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.DESELECT_GUIDED_PILLS, 'action has the correct type');
      assert.deepEqual(action.payload.pillData, pillsData, 'action pillData has the right value');
      done();
    };

    thunk(dispatch, getState);
  });

  test('selectGuidedPills action creator returns proper type and payload', function(assert) {
    const { pillsData } = new ReduxDataHelper()
      .pillsDataPopulated()
      .build()
      .investigate
      .queryNode;

    const action = guidedCreators.selectGuidedPills({
      pillData: pillsData
    });

    assert.equal(action.type, ACTION_TYPES.SELECT_GUIDED_PILLS, 'action has the correct type');
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
    const init = bindActionCreators(guidedCreators._serverSideValidation, redux.dispatch.bind(redux));

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

  test('openGuidedPillForEdit dispatches the proper events', function(assert) {
    const { pillsData } = new ReduxDataHelper()
      .language()
      .pillsDataPopulated()
      .markEditing(['1'])
      .build()
      .investigate
      .queryNode;

    const action = guidedCreators.openGuidedPillForEdit({
      pillData: pillsData
    });

    assert.equal(action.type, ACTION_TYPES.OPEN_GUIDED_PILL_FOR_EDIT, 'action has the correct type');
    assert.deepEqual(action.payload.pillData, pillsData, 'action pillData has the right value');
  });

  test('addFreeFormFilter action creator returns proper type and payload', function(assert) {
    assert.expect(2);
    const thunk = guidedCreators.addFreeFormFilter('medium = 50');

    const getState = () => {
      return new ReduxDataHelper().language().build();
    };

    const dispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.REPLACE_ALL_GUIDED_PILLS, 'action has the correct type');
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
    const thunk = guidedCreators.updatedFreeFormText('medium = 50');

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

  test('resetGuidedPill action creator returns proper type and payload', function(assert) {
    const action = guidedCreators.resetGuidedPill('foo');
    assert.equal(action.type, ACTION_TYPES.RESET_GUIDED_PILL, 'action has the correct type');
    assert.deepEqual(action.payload.pillData, 'foo', 'action pillData has the right value');
  });

  test('selectAllPillsTowardsDirection -> right dispatches the proper events', function(assert) {
    assert.expect(2);
    const done = assert.async();

    const position = 0;
    const direction = 'right';
    const getState = () => {
      return new ReduxDataHelper()
        .language()
        .pillsDataPopulated()
        .markSelected(['1'])
        .build();
    };

    const myDispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.SELECT_GUIDED_PILLS, 'action has the correct type');
      assert.deepEqual(action.payload.pillData,
        [{
          id: '2',
          meta: 'b',
          operator: '=',
          value: '\'y\'',
          isSelected: false,
          isEditing: false,
          isFocused: false,
          isInvalid: false,
          complexFilterText: undefined
        }],
        'action pillData will contain the pills that will need to be selected');
      done();
    };

    const thunk = guidedCreators.selectAllPillsTowardsDirection(position, direction);
    thunk(myDispatch, getState);
  });

  test('selectAllPillsTowardsDirection -> left dispatches the proper events', function(assert) {
    assert.expect(2);
    const done = assert.async();

    const position = 1;
    const direction = 'left';

    const getState = () => {
      return new ReduxDataHelper()
        .language()
        .pillsDataPopulated()
        .markSelected(['2'])
        .build();
    };

    const myDispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.SELECT_GUIDED_PILLS, 'action has the correct type');
      assert.deepEqual(action.payload.pillData,
        [{
          id: '1',
          meta: 'a',
          operator: '=',
          value: '\'x\'',
          isSelected: false,
          isEditing: false,
          isFocused: false,
          isInvalid: false,
          complexFilterText: undefined
        }],
        'action pillData will contain the pills that will need to be selected');
      done();
    };

    const thunk = guidedCreators.selectAllPillsTowardsDirection(position, direction);
    thunk(myDispatch, getState);
  });

  test('handleIncomingQueryHashes action creator returns proper type and payload', function(assert) {
    const action = guidedCreators.handleIncomingQueryHashes(['foo']);
    assert.equal(action.type, ACTION_TYPES.RETRIEVE_QUERY_PARAMS_FOR_HASHES, 'action has the correct type');
    assert.deepEqual(action.meta.hashes, ['foo'], 'action hashes has the right value');
  });

  test('retrieveHashForQueryParams dispatches the proper events', function(assert) {
    assert.expect(1);
    const done = assert.async();

    const getState = () => {
      return new ReduxDataHelper()
        .language()
        .pillsDataPopulated()
        .build();
    };

    const myDispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.RETRIEVE_HASH_FOR_QUERY_PARAMS, 'action has the correct type');
      done();
    };

    const thunk = guidedCreators.retrieveHashForQueryParams();
    thunk(myDispatch, getState);
  });

});
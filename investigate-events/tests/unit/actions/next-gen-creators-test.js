import { module, test } from 'qunit';
import nextGenCreators from 'investigate-events/actions/next-gen-creators';
import ACTION_TYPES from 'investigate-events/actions/types';
import ReduxDataHelper from '../../helpers/redux-data-helper';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

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
      const string = action.payload.validatedPillData.validationError;
      assert.deepEqual(action.payload.validatedPillData, {
        id: 1,
        meta: 'ip.proto',
        operator: '=',
        value: 'boom',
        isInvalid: true,
        validationError: string
      }, 'validate action pillData has the right value');
      done();
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
      const string = action.payload.validatedPillData.validationError;
      assert.deepEqual(action.payload.validatedPillData, {
        id: 1,
        meta: 'ip.proto',
        operator: '=',
        value: 'boom',
        isInvalid: true,
        validationError: string
      }, 'validate action pillData has the right value');
      done();
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
      assert.deepEqual(action.payload.pillData, state.investigate.nextGen.pillsData, 'action pillData has the right value');
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
      .nextGen;

    const action = nextGenCreators.deselectNextGenPills({
      pillData: pillsData
    });

    assert.equal(action.type, ACTION_TYPES.DESELECT_NEXT_GEN_PILLS, 'action has the correct type');
    assert.deepEqual(action.payload.pillData, pillsData, 'action pillData has the right value');
  });

  test('selectNextGenPills action creator returns proper type and payload', function(assert) {
    const { pillsData } = new ReduxDataHelper()
      .pillsDataPopulated()
      .build()
      .investigate
      .nextGen;

    const action = nextGenCreators.selectNextGenPills({
      pillData: pillsData
    });

    assert.equal(action.type, ACTION_TYPES.SELECT_NEXT_GEN_PILLS, 'action has the correct type');
    assert.deepEqual(action.payload.pillData, pillsData, 'action pillData has the right value');
  });

});
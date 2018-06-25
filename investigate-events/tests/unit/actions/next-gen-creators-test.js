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

  test('deleteNextGenPill action creator returns proper type and payload', function(assert) {
    const action = nextGenCreators.deleteNextGenPill({
      pillData: 'foo'
    });

    assert.equal(action.type, ACTION_TYPES.DELETE_NEXT_GEN_PILL, 'action has the correct type');
    assert.equal(action.payload.pillData, 'foo', 'action pillData has the right value');
  });
});
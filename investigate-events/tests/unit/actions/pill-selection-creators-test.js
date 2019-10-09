import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../helpers/redux-data-helper';
import pillCreators from 'investigate-events/actions/pill-selection-creators';
import ACTION_TYPES from 'investigate-events/actions/types';

module('Unit | Actions | Pill Selection Creators', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('deselectGuidedPills action creator returns proper type and payload', function(assert) {
    const done = assert.async();
    const state = new ReduxDataHelper()
      .pillsDataPopulated()
      .build();
    const { pillsData } = state.investigate.queryNode;
    const getState = () => state;

    const secondDispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.DESELECT_GUIDED_PILLS, 'action has the correct type');
      assert.deepEqual(action.payload.pillData, pillsData, 'action pillData has the right value');
      done();
    };
    const firstDispatch = (thunk2) => {
      thunk2(secondDispatch, getState);
    };
    const thunk1 = pillCreators.deselectGuidedPills({
      pillData: pillsData
    });
    thunk1(firstDispatch, getState);
  });

  test('deselectGuidedPills action creator will collect missing twins and send for deselect', function(assert) {
    assert.expect(5);
    const done = assert.async();
    const state = new ReduxDataHelper()
      .pillsDataWithParens()
      .markSelected(['1', '3'])
      .build();

    const { pillsData } = state.investigate.queryNode;
    const getState = () => state;


    let calledOnce = false;
    const secondDispatch = (action) => {
      // 2nd call sends the pill
      if (calledOnce) {
        assert.equal(action.type, ACTION_TYPES.DESELECT_GUIDED_PILLS, 'action has the correct type');
        assert.deepEqual(action.payload.pillData, [pillsData[2]], 'action pillData has the right value');
        done();
      } else {
        // 1st call sends the twin of the pill
        calledOnce = true;
        assert.equal(action.type, ACTION_TYPES.DESELECT_GUIDED_PILLS, 'action has the correct type');
        assert.deepEqual(action.payload.pillData, [pillsData[0]], 'action pillData has the right value');
        assert.deepEqual(action.payload.shouldIgnoreFocus, true, 'should be ignoring focus on twin dispatch');
      }
    };
    const firstDispatch = (thunk2) => {
      thunk2(secondDispatch, getState);
    };
    const thunk1 = pillCreators.deselectGuidedPills({
      // just pull out the selected 2nd twin and send it alone
      // would expect two dispatches, one to collect the missing twin
      pillData: [pillsData[2]]
    });
    thunk1(firstDispatch, getState);
  });

  test('deselectAllGuidedPills action creator returns proper type and payload', function(assert) {
    const done = assert.async();

    const getState = () => {
      return new ReduxDataHelper().pillsDataPopulated().markSelected(['1', '2', '3']).build();
    };
    const { pillsData } = getState().investigate.queryNode;

    const thirdDispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.DESELECT_GUIDED_PILLS, 'action has the correct type');
      assert.deepEqual(action.payload.pillData, pillsData, 'action pillData has the right value');
      done();
    };
    const secondDispatch = (thunk3) => {
      thunk3(thirdDispatch, getState);
    };
    const firstDispatch = (thunk2) => {
      thunk2(secondDispatch, getState);
    };

    const thunk1 = pillCreators.deselectAllGuidedPills();
    thunk1(firstDispatch, getState);
  });

  test('selectGuidedPills action creator returns proper type and payload', function(assert) {
    const done = assert.async();
    const state = new ReduxDataHelper().pillsDataPopulated().build();
    const { pillsData } = state.investigate.queryNode;
    const getState = () => state;

    const secondDispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.SELECT_GUIDED_PILLS, 'action has the correct type');
      assert.deepEqual(action.payload.pillData, pillsData, 'action pillData has the right value');
      done();
    };
    const firstDispatch = (thunk2) => {
      thunk2(secondDispatch, getState);
    };

    const thunk1 = pillCreators.selectGuidedPills({
      pillData: pillsData
    });
    thunk1(firstDispatch, getState);
  });

  test('selectGuidedPills action creator will collect missing twins and send for deselect', function(assert) {
    assert.expect(5);
    const done = assert.async();
    const state = new ReduxDataHelper()
      .pillsDataWithParens()
      .build();

    const { pillsData } = state.investigate.queryNode;
    const getState = () => state;

    let calledOnce = false;
    const secondDispatch = (action) => {
      // 2nd call sends the pill
      if (calledOnce) {
        assert.equal(action.type, ACTION_TYPES.SELECT_GUIDED_PILLS, 'action has the correct type');
        assert.deepEqual(action.payload.pillData, [pillsData[2]], 'action pillData has the right value');
        done();
      } else {
        // 1st call sends the twin of the pill
        calledOnce = true;
        assert.equal(action.type, ACTION_TYPES.SELECT_GUIDED_PILLS, 'action has the correct type');
        assert.deepEqual(action.payload.pillData, [pillsData[0]], 'action pillData has the right value');
        assert.deepEqual(action.payload.shouldIgnoreFocus, true, 'should be ignoring focus on twin dispatch');
      }
    };
    const firstDispatch = (thunk2) => {
      thunk2(secondDispatch, getState);
    };
    const thunk1 = pillCreators.selectGuidedPills({
      // just pull out the  2nd twin and send it alone
      // would expect two dispatches, one to collect the missing twin
      pillData: [pillsData[2]]
    });
    thunk1(firstDispatch, getState);
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
    const { investigate: { queryNode: { pillsData } } } = getState();

    const thirdDispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.SELECT_GUIDED_PILLS, 'action has the correct type');
      assert.deepEqual(action.payload.pillData, pillsData, 'action pillData will contain the pills that will need to be selected');
      done();
    };
    const secondDispatch = (thunk3) => {
      thunk3(thirdDispatch, getState);
    };
    const firstDispatch = (thunk2) => {
      thunk2(secondDispatch, getState);
    };

    const thunk1 = pillCreators.selectAllPillsTowardsDirection(position, direction);
    thunk1(firstDispatch, getState);
  });

  test('selectAllPillsTowardsDirection -> left dispatches the proper events', function(assert) {
    assert.expect(2);
    const done = assert.async();

    const position = 2;
    const direction = 'left';

    const getState = () => {
      return new ReduxDataHelper()
        .language()
        .pillsDataPopulated()
        .markSelected(['3'])
        .build();
    };

    const { investigate: { queryNode: { pillsData } } } = getState();

    const thirdDispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.SELECT_GUIDED_PILLS, 'action has the correct type');
      assert.deepEqual(action.payload.pillData, pillsData, 'action pillData will contain the pills that will need to be selected');
      done();
    };

    const secondDispatch = (thunk3) => {
      thunk3(thirdDispatch, getState);
    };
    const firstDispatch = (thunk2) => {
      thunk2(secondDispatch, getState);
    };

    const thunk1 = pillCreators.selectAllPillsTowardsDirection(position, direction);
    thunk1(firstDispatch, getState);
  });
});
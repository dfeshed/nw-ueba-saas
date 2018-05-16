import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { blur, click, findAll, focus, render, settled } from '@ember/test-helpers';
import sinon from 'sinon';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Immutable from 'seamless-immutable';
import { patchReducer } from '../../../../helpers/vnext-patch';
import * as groupCreators from 'admin-source-management/actions/creators/group-creators';

const initialState = {
  group: {
    id: null,
    name: null,
    description: null,
    createdBy: null,
    createdOn: null,
    lastModifiedBy: null,
    lastModifiedOn: null
  },

  groupSaveStatus: null // wait, complete, error
};

const saveGroupData = Immutable.from({
  'id': 'group_001',
  'name': 'Zebra 001',
  'description': 'Zebra 001 of group group_001',
  'createdBy': 'local',
  'createdOn': 1523655354337,
  'lastModifiedBy': 'local',
  'lastModifiedOn': 1523655354337
});

let setState;

module('Integration | Component | Group', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      const fullState = { usm: { group: state } };
      patchReducer(this, Immutable.from(fullState));
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('The component appears in the DOM', async function(assert) {
    setState({ ...initialState });
    await render(hbs`{{usm-groups/group}}`);
    assert.equal(findAll('.usm-group').length, 1, 'The component appears in the DOM');
  });

  test('Save button is disabled when there is no group name', async function(assert) {
    setState({ ...initialState });
    await render(hbs`{{usm-groups/group}}`);
    assert.equal(findAll('.confirm-button.is-disabled').length, 1, 'The Save button is disabled when there is no group name');
  });

  test('Save button is enabled when there is a group name', async function(assert) {
    setState({ ...initialState, group: { ...initialState.group, name: 'Group 001' } });
    await render(hbs`{{usm-groups/group}}`);
    assert.equal(findAll('.confirm-button:not(.is-disabled)').length, 1, 'The Save button is enabled when there is a group name');
  });

  test('A loading spinner is displayed if the groupSaveStatus property is "wait"', async function(assert) {
    setState({ ...initialState, groupSaveStatus: 'wait' });
    await render(hbs`{{usm-groups/group}}`);
    assert.equal(findAll('.loading-overlay .rsa-loader').length, 1, 'A loading spinner appears in the dom');
  });

  test('Typing in the group name control dispatches the editGroup action creator (EDIT_GROUP)', async function(assert) {
    const actionSpy = sinon.spy(groupCreators, 'editGroup');
    setState({ ...initialState });
    await render(hbs`{{usm-groups/group}}`);
    const el = findAll('input')[0]; // eslint-disable-line ember-suave/prefer-destructuring
    await focus(el);
    el.value = 'test name';
    // await triggerKeyEvent(el, 'keyup', 'e');  // might go back to this with debounce
    await blur(el);
    return settled().then(() => {
      assert.ok(actionSpy.calledOnce, 'The editGroup action (EDIT_GROUP) was called once');
      assert.ok(actionSpy.calledWith('group.name', 'test name'));
      actionSpy.restore();
    });
  });

  test('Typing in the group description control dispatches the editGroup action creator (EDIT_GROUP)', async function(assert) {
    const actionSpy = sinon.spy(groupCreators, 'editGroup');
    setState({ ...initialState });
    await render(hbs`{{usm-groups/group}}`);
    const el = findAll('textarea')[0]; // eslint-disable-line ember-suave/prefer-destructuring
    await focus(el);
    el.value = 'test description';
    // await triggerKeyEvent(el, 'keyup', 'n'); // might go back to this with debounce
    await blur(el);
    return settled().then(() => {
      assert.ok(actionSpy.calledOnce, 'The editGroup action (EDIT_GROUP) was called once');
      assert.ok(actionSpy.calledWith('group.description', 'test description'));
      actionSpy.restore();
    });
  });

  test('Clicking the save button dispatches the saveGroup action creator (SAVE_GROUP)', async function(assert) {
    const actionSpy = sinon.spy(groupCreators, 'saveGroup');
    setState({ ...initialState, group: saveGroupData });
    await render(hbs`{{usm-groups/group}}`);
    const el = findAll('.confirm-button:not(.is-disabled) button')[0]; // eslint-disable-line ember-suave/prefer-destructuring
    await click(el);
    return settled().then(() => {
      assert.ok(actionSpy.calledOnce, 'The saveGroup action (SAVE_GROUP) was called once');
      // only checking first arg as second arg will be a Function that lives in the Component
      assert.equal(actionSpy.getCall(0).args[0], saveGroupData);
      actionSpy.restore();
    });
  });

});

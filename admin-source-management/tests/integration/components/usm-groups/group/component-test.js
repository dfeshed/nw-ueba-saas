import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { blur, click, findAll, focus, render, settled } from '@ember/test-helpers';
import sinon from 'sinon';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Immutable from 'seamless-immutable';
import { clickTrigger, selectChoose } from '../../../../helpers/ember-power-select';
import { patchReducer } from '../../../../helpers/vnext-patch';
import * as groupCreators from 'admin-source-management/actions/data-creators/group-creators';
import { initialState as _initialState } from 'admin-source-management/reducers/usm/group-reducers';
import policiesData from '../../../../../tests/data/subscriptions/policy/findAll/data';

const initialState = {
  ..._initialState
};

const saveGroupData = Immutable.from({
  id: 'group_001',
  name: 'Zebra 001',
  description: 'Zebra 001 of group group_001',
  createdBy: 'local',
  createdOn: 1523655354337,
  lastModifiedBy: 'local',
  lastModifiedOn: 1523655354337,
  osTypes: [],
  osDescriptions: [],
  ipRangeStart: '192.168.10.1',
  ipRangeEnd: '192.168.10.10',
  policy: null // map of { 'type': 'policyID' }  ( ex. { 'edrPolicy': 'id_abc123' } )
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

  test('A loading spinner is displayed if the initGroupFetchPoliciesStatus property is "wait"', async function(assert) {
    setState({ ...initialState, initGroupFetchPoliciesStatus: 'wait' });
    await render(hbs`{{usm-groups/group}}`);
    assert.equal(findAll('.loading-overlay .rsa-loader').length, 1, 'A loading spinner appears in the dom');
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
    const el = findAll('.group-name input')[0]; // eslint-disable-line ember-suave/prefer-destructuring
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
    const el = findAll('.group-description textarea')[0]; // eslint-disable-line ember-suave/prefer-destructuring
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

  test('Typing in the IP Range Start control dispatches the editGroup action creator (EDIT_GROUP)', async function(assert) {
    const actionSpy = sinon.spy(groupCreators, 'editGroup');
    setState({ ...initialState });
    await render(hbs`{{usm-groups/group}}`);
    const el = findAll('.ip-range-start input')[0]; // eslint-disable-line ember-suave/prefer-destructuring
    await focus(el);
    el.value = '192.168.10.1';
    // await triggerKeyEvent(el, 'keyup', 'e');  // might go back to this with debounce
    await blur(el);
    return settled().then(() => {
      assert.ok(actionSpy.calledOnce, 'The editGroup action (EDIT_GROUP) was called once');
      assert.ok(actionSpy.calledWith('group.ipRangeStart', '192.168.10.1'));
      actionSpy.restore();
    });
  });

  test('Typing in the IP Range End control dispatches the editGroup action creator (EDIT_GROUP)', async function(assert) {
    const actionSpy = sinon.spy(groupCreators, 'editGroup');
    setState({ ...initialState });
    await render(hbs`{{usm-groups/group}}`);
    const el = findAll('.ip-range-end input')[0]; // eslint-disable-line ember-suave/prefer-destructuring
    await focus(el);
    el.value = '192.168.10.10';
    // await triggerKeyEvent(el, 'keyup', 'e');  // might go back to this with debounce
    await blur(el);
    return settled().then(() => {
      assert.ok(actionSpy.calledOnce, 'The editGroup action (EDIT_GROUP) was called once');
      assert.ok(actionSpy.calledWith('group.ipRangeEnd', '192.168.10.10'));
      actionSpy.restore();
    });
  });

  test('Changing the OS Type select control dispatches the editGroup action creator (EDIT_GROUP)', async function(assert) {
    const actionSpy = sinon.spy(groupCreators, 'editGroup');
    setState({ ...initialState });
    await render(hbs`{{usm-groups/group}}`);
    clickTrigger('.control .os-type');
    selectChoose('.control .os-type', 'Windows');
    return settled().then(() => {
      // should be called twice because we also trigger it for osDescriptions
      assert.ok(actionSpy.calledTwice, 'The editGroup action (EDIT_GROUP) was called twice');
      assert.ok(actionSpy.calledWith('group.osTypes', ['Windows'])); // should be called with array of ID's
      assert.ok(actionSpy.calledWith('group.osDescriptions', [])); // should be called with empty array of ID's
      actionSpy.restore();
    });
  });

  test('Changing the OS Description select control dispatches the editGroup action creator (EDIT_GROUP)', async function(assert) {
    const actionSpy = sinon.spy(groupCreators, 'editGroup');
    // pre-select an osType so the osDescription select has options available
    setState({ ...initialState, group: { ...initialState.group, osTypes: ['Windows'] } });
    await render(hbs`{{usm-groups/group}}`);
    clickTrigger('.control .os-description');
    selectChoose('.control .os-description', 'Windows Vista');
    return settled().then(() => {
      assert.ok(actionSpy.calledOnce, 'The editGroup action (EDIT_GROUP) was called once');
      assert.ok(actionSpy.calledWith('group.osDescriptions', ['Windows Vista']));
      actionSpy.restore();
    });
  });

  test('Changing the Policy select control dispatches the editGroup action creator (EDIT_GROUP)', async function(assert) {
    const actionSpy = sinon.spy(groupCreators, 'editGroup');
    setState({ ...initialState, policies: [...policiesData] });
    await render(hbs`{{usm-groups/group}}`);
    // power-select does not seem to render added class(es) like power-select-multiple,
    // so .policy was added to the control wrapper div
    clickTrigger('.control.policy');
    selectChoose('.control.policy', 'EMC 001');
    return settled().then(() => {
      assert.ok(actionSpy.calledOnce, 'The editGroup action (EDIT_GROUP) was called once');
      assert.ok(actionSpy.calledWith('group.policy', { edrPolicy: 'policy_001' })); // should be called with type:ID map
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

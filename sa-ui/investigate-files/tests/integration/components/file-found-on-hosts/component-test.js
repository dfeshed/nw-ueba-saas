import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, click, waitUntil, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import { patchReducer } from '../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import sinon from 'sinon';

let initState;

const hosts = {
  data: [
    { 'agentId': '0C0454BB-A0D9-1B2A-73A6-5E8CCBF88DAC', 'hostname': 'windows', 'score': 0 },
    { 'agentId': '0C0454BB-A0D9-1B2A-73A6-5E8CCBF88DAB', 'hostname': 'mac', 'score': 0 },
    { 'agentId': '0C0454BB-A0D9-1B2A-73A6-5E8CCBF88DAD', 'hostname': 'linux', 'score': 0 },
    { 'agentId': '0C0454BB-A0D9-1B2A-73A6-5E8CCBF88DAF', 'hostname': 'windows-1', 'score': 0 }
  ]
};
module('Integration | Component | file found on machines', function(hooks) {

  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-files')
  });


  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    this.owner.lookup('service:timezone').set('selected', { zoneId: 'UTC' });
    initState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
  });

  test('it should render the machine list panel', async function(assert) {
    await render(hbs`{{file-found-on-hosts}}`);
    assert.equal(findAll('.files-host-list').length, 1, 'Panel for hosts list has rendered.');
  });

  test('host list being rendered', async function(assert) {
    new ReduxDataHelper(initState)
      .hostNameList(hosts)
      .hostListCount(4)
      .build();
    await render(hbs`{{file-found-on-hosts}}`);
    assert.equal(findAll('.host_details_link').length, 4, '4 Machines are listed.');
    assert.equal(findAll('.pivot-to-investigate .rsa-form-button').length, 4, 'Analyze Events button appears with each machine.');
  });

  test('on click of a machine, details open up', async function(assert) {
    new ReduxDataHelper(initState)
      .hostNameList(hosts)
      .hostListCount(4)
      .build();
    const actionSpy = sinon.spy(window, 'open');
    await render(hbs`{{file-found-on-hosts}}`);
    await click(findAll('.host_details_link a')[0]);
    await waitUntil(() => !this.owner.lookup('service:redux').getState().files.fileList.fetchMetaValueLoading, { timeout: Infinity });
    assert.ok(actionSpy.calledOnce, 'Window.open is called');
    actionSpy.resetHistory();
    actionSpy.restore();
  });

  test('when file, is not active on any host', async function(assert) {
    await render(hbs`{{file-found-on-hosts}}`);
    assert.equal(findAll('.files-host-list .rsa-panel-message .message')[0].textContent.trim(), 'This file is not associated with any host', 'No Host result message, when file is not found on any host.');
  });

  test('loader icon when list is loading', async function(assert) {
    new ReduxDataHelper(initState)
      .fetchMetaValueLoading(true)
      .hostNameList(hosts)
      .hostListCount(4)
      .build();
    await render(hbs`{{file-found-on-hosts}}`);
    assert.equal(findAll('.rsa-loader').length, 1, 'loader icon is present.');
  });


  test('host info label is displayed', async function(assert) {
    new ReduxDataHelper(initState)
      .hostNameList(hosts)
      .hostListCount(4)
      .build();
    await render(hbs`{{file-found-on-hosts}}`);
    assert.equal(findAll('.host_details_link').length, 4, '4 Machines are listed.');
    assert.equal(findAll('.count-info').length, 0, 'No message displayed for host count less than 100.');
  });

  test('info icon when host list is more than 100', async function(assert) {
    new ReduxDataHelper(initState)
      .hostNameList({ data: new Array(100) })
      .hostListCount(110)
      .build();
    await render(hbs`{{file-found-on-hosts}}`);
    assert.equal(findAll('.info-icon').length, 1, 'info icon is present.');
  });

  test('host info label is displayed correctly', async function(assert) {
    new ReduxDataHelper(initState).hostNameList({ data: new Array(100) })
      .hostListCount(110)
      .build();
    await render(hbs`{{file-found-on-hosts}}`);
    assert.equal(find('.count-info').textContent.trim(), 'Top 100 hosts with high risk scores are listed', 'Message displayed for host count More than 100.');
  });

  test('it should opens events page', async function(assert) {
    new ReduxDataHelper(initState)
      .hostNameList(hosts)
      .coreServerId('123123123')
      .hostListCount(4)
      .build();

    const actionSpy = sinon.spy(window, 'open');

    await render(hbs`{{file-found-on-hosts}}`);
    await click(findAll('.pivot-to-investigate button')[0]);
    assert.ok(actionSpy.calledOnce, 'window open called');
    assert.ok(actionSpy.args[0][0].includes('/investigate/events'));
    actionSpy.resetHistory();
    actionSpy.restore();
  });
});

import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, click, waitUntil } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import { patchReducer } from '../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import sinon from 'sinon';

let initState;

const hosts = [
  {
    value: 'Machine1'
  },
  {
    value: 'Machine2'
  },
  {
    value: 'Machine3'
  },
  {
    value: 'Machine4'
  }
];

module('Integration | Component | file found on machines', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-files')
  });


  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
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
      .build();
    await render(hbs`{{file-found-on-hosts}}`);
    assert.equal(findAll('.host_details_link').length, 4, '4 Machines are listed.');
    assert.equal(findAll('.pivot-to-investigate-button .rsa-form-button').length, 4, 'Analyze Events button appears with each machine.');
    assert.equal(findAll('.rsa-form-button .rsa-icon-download-2-filled').length, 4, 'Download button for each machine is rendered.');
  });

  test('on click of a machine, details open up', async function(assert) {
    new ReduxDataHelper(initState)
      .hostNameList(hosts)
      .build();
    const actionSpy = sinon.spy(window, 'open');
    await render(hbs`{{file-found-on-hosts}}`);
    await click(findAll('.host_details_link a')[0]);
    await waitUntil(() => !this.owner.lookup('service:redux').getState().files.fileList.fetchMetaValueLoading, { timeout: Infinity });
    assert.ok(actionSpy.calledOnce, 'Window.open is called');
  });

  test('when file, is not active on any host', async function(assert) {
    await render(hbs`{{file-found-on-hosts}}`);
    assert.equal(findAll('.files-host-list .rsa-panel-message .message')[0].textContent.trim(), 'No results found', 'No result message, when file is not found on any host.');
  });

  test('loader icon when list is loading', async function(assert) {
    new ReduxDataHelper(initState)
      .fetchMetaValueLoading(true)
      .hostNameList(hosts)
      .build();
    await render(hbs`{{file-found-on-hosts}}`);
    assert.equal(findAll('.rsa-loader').length, 1, 'loader icon is present.');
  });
});

import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { findAll, render, click, settled, find, triggerEvent } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { revertPatch } from '../../../helpers/patch-reducer';
import { patchReducer } from '../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Immutable from 'seamless-immutable';
import sinon from 'sinon';

let initState;

const selectors = {
  consoleEvents: '[test-id=consoleEvents]',
  networkEvents: '[test-id=networkEvents]',
  fileEvents: '[test-id=fileEvents]',
  processEvents: '[test-id=processEvents]',
  registryEvents: '[test-id=registryEvents]'
};

const e = {
  clientX: 5,
  clientY: 2,
  view: {
    window: {
      innerWidth: 100,
      innerHeight: 100
    }
  }
};
const callback = () => {};
const wormhole = 'wormhole-context-menu';
const items = [
  {
    'thumbprint': 'afdd80c4ebf2f61d3943f18bb566d6aa6f6e5033',
    'friendlyName': 'Microsoft Windows',
    'subject': 'C=US, S=Washington, L=Redmond, O=Microsoft Corporation, CN=Microsoft Windows',
    'subjectKey': '111c89583fbec5662adaff8661edeca33a83c952',
    'serial': '33000001066ec325c431c9180e000000000106',
    'issuer': 'C=US, S=Washington, L=Redmond, O=Microsoft Corporation, CN=Microsoft Windows Production PCA 2011',
    'authorityKey': 'a92902398e16c49778cd90f99e4f9ae17c55af53',
    'notValidBeforeUtcDate': '2016-10-11T20:39:31.000+0000',
    'notValidAfterUtcDate': '2018-01-11T20:39:31.000+0000',
    'features': [
      'rootMicrosoft'
    ],
    'crl': [
      'http://www.microsoft.com/pkiops/crl/MicWinProPCA2011_2011-10-19.crl'
    ]
  },
  {
    'thumbprint': '8020c37b16f17d212d0af1f23e5a85bb55ad91fc',
    'friendlyName': 'Microsoft Windows Publisher',
    'subject': 'C=US, S=Washington, L=Redmond, O=Microsoft Corporation, CN=Microsoft Windows Publisher',
    'subjectKey': '27b9d3d2a23eff0c9a3543acb43351d22d6baf4e',
    'serial': '33000001125a147470a9987d6e000000000112',
    'issuer': 'C=US, S=Washington, L=Redmond, O=Microsoft Corporation, CN=Microsoft Windows Production PCA 2011',
    'authorityKey': 'a92902398e16c49778cd90f99e4f9ae17c55af53',
    'notValidBeforeUtcDate': '2017-02-15T19:10:50.000+0000',
    'notValidAfterUtcDate': '2018-05-09T19:10:50.000+0000',
    'features': [
      'rootMicrosoft'
    ],
    'crl': [
      'http://www.microsoft.com/pkiops/crl/MicWinProPCA2011_2011-10-19.crl'
    ]
  }
];

module('Integration | Component | certificates-container', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-files')
  });
  hooks.beforeEach(function() {
    const wormholeDiv = document.createElement('div');
    wormholeDiv.id = wormhole;
    document.querySelector('#ember-testing').appendChild(wormholeDiv);
    document.addEventListener('contextmenu', callback);

    initState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    this.dateFormat = this.owner.lookup('service:dateFormat');
    this.timeFormat = this.owner.lookup('service:timeFormat');
    this.timezone = this.owner.lookup('service:timezone');
    this.owner.lookup('service:timezone').set('selected', { zoneId: 'UTC' });
    this.set('dateFormat.selected', 'MM/dd/yyyy', 'MM/dd/yyyy');
    this.set('timeFormat.selected', 'HR24', 'HR24');
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  hooks.afterEach(function() {
    revertPatch();
  });

  test('container for certificates render', async function(assert) {
    await render(hbs`{{certificates-container}}`);
    assert.equal(findAll('.certificates-container').length, 1, 'certificates container has rendered.');
  });

  test('action bar is rendered', async function(assert) {
    await render(hbs`{{certificates-container}}`);
    assert.equal(findAll('.certificates-action-bar').length, 1, 'certificates action bar has rendered.');
  });

  test('certificates body is rendered', async function(assert) {
    await render(hbs`{{certificates-container}}`);
    assert.equal(findAll('.certificates-body').length, 1, 'certificates body has rendered.');
  });

  test('certificates footer is rendered', async function(assert) {
    await render(hbs`{{certificates-container}}`);
    assert.equal(findAll('.certificates-footer').length, 1, 'certificates footer has rendered.');
  });
  test('certificates filter panel rendered', async function(assert) {
    await render(hbs`{{certificates-container}}`);
    assert.equal(findAll('.filter-wrapper').length, 1, 'certificates filter panel has rendered.');
  });
  test('certificates close icon should rendered', async function(assert) {
    new ReduxDataHelper(initState)
      .isCertificateView(true)
      .build();
    await render(hbs`{{certificates-container}}`);
    assert.equal(findAll('.close-certificate-view-button').length, 1, 'certificates close icon should rendered.');
    await click('.close-certificate-view-button button');
    return settled().then(() => {
      const state = this.owner.lookup('service:redux').getState();
      assert.equal(state.certificate.list.isCertificateView, false, 'Certificate view is closed');
    });
  });

  test('event analysis button rendered and disabled', async function(assert) {
    new ReduxDataHelper(initState)
      .isCertificateView(true)
      .build();
    await render(hbs`{{certificates-container}}`);
    assert.equal(findAll('.certificates-container .pivot-to-event-analysis').length, 1, 'Event analysis button rendered');
    assert.equal(find('.certificates-container .event-analysis').classList.contains('is-disabled'), true, 'Event analysis button disabled');
  });

  test('event analysis button enabled and on click opens another window', async function(assert) {
    new ReduxDataHelper(initState)
      .isCertificateView(true)
      .certificatesItems(items)
      .loadMoreCertificateStatus('stopped')
      .selectedCertificatesList([])
      .coreServerId('serverId')
      .build();
    const actionSpy = sinon.spy(window, 'open');
    await render(hbs`{{certificates-container}}`);
    await click(findAll('.rsa-data-table-body-row')[0]);
    assert.equal(findAll('.certificates-container .pivot-to-event-analysis').length, 1, 'Event analysis button rendered');
    assert.equal(find('.certificates-container .pivot-to-event-analysis').classList.contains('is-disabled'), false, 'Event analysis button enabled');

    await click(find('.event-analysis button'));

    assert.ok(actionSpy.callCount, 1, 'Window.open is called');
    actionSpy.restore();
  });

  test('go back to files view', async function(assert) {
    new ReduxDataHelper(initState)
      .isCertificateView(true)
      .certificatesItems(items)
      .loadMoreCertificateStatus('stopped')
      .selectedCertificatesList([])
      .coreServerId('serverId')
      .build();
    await render(hbs`{{certificates-container}}`);
    await click(find('.back-to-file'));
    return settled().then(() => {
      const state = this.owner.lookup('service:redux').getState();
      assert.equal(state.certificate.list.isCertificateView, false, 'Certificate view is closed');
    });
  });

  test('certificates close button, when clicked will change the contextual topic back to investigate files', async function(assert) {
    new ReduxDataHelper(initState)
      .isCertificateView(true)
      .build();
    await render(hbs`{{certificates-container}}`);
    const contextualHelp = this.owner.lookup('service:contextualHelp');
    assert.equal(findAll('.close-certificate-view-button').length, 1, 'certificates close icon should rendered.');
    await click('.close-certificate-view-button button');
    return settled().then(() => {
      assert.equal(contextualHelp.topic, 'files', 'When navigating back to files view, contextual help topic is changed.');
    });
  });

  test('testing analyze network events', async function(assert) {
    new ReduxDataHelper(initState)
      .isCertificateView(true)
      .certificatesItems(items)
      .loadMoreCertificateStatus('stopped')
      .selectedCertificatesList([])
      .certificateStatusData({})
      .coreServerId('serverId')
      .build();
    const actionSpy = sinon.spy(window, 'open');

    await render(hbs`{{certificates-container}}{{context-menu}}`);
    triggerEvent('.content-context-menu', 'contextmenu', e);

    return settled().then(async() => {
      const selector = '.context-menu';
      const menuItems = findAll(`${selector} > .context-menu__item`);
      await triggerEvent(`#${menuItems[1].id}`, 'mouseover');
      await click(findAll(selectors.networkEvents)[0]);
      assert.ok(actionSpy.callCount, 1, 'Window.open is called');
      actionSpy.restore();
    });
  });

  test('testing analyze file events', async function(assert) {
    new ReduxDataHelper(initState)
      .isCertificateView(true)
      .certificatesItems(items)
      .loadMoreCertificateStatus('stopped')
      .selectedCertificatesList([])
      .certificateStatusData({})
      .coreServerId('serverId')
      .build();
    const actionSpy = sinon.spy(window, 'open');

    await render(hbs`{{certificates-container}}{{context-menu}}`);
    triggerEvent('.content-context-menu', 'contextmenu', e);

    return settled().then(async() => {
      const selector = '.context-menu';
      const menuItems = findAll(`${selector} > .context-menu__item`);
      await triggerEvent(`#${menuItems[1].id}`, 'mouseover');
      await click(findAll(selectors.fileEvents)[0]);
      assert.ok(actionSpy.callCount, 1, 'Window.open is called');
      actionSpy.restore();
    });
  });

  test('testing analyze process events', async function(assert) {
    new ReduxDataHelper(initState)
      .isCertificateView(true)
      .certificatesItems(items)
      .loadMoreCertificateStatus('stopped')
      .selectedCertificatesList([])
      .certificateStatusData({})
      .coreServerId('serverId')
      .build();
    const actionSpy = sinon.spy(window, 'open');

    await render(hbs`{{certificates-container}}{{context-menu}}`);
    triggerEvent('.content-context-menu', 'contextmenu', e);

    return settled().then(async() => {
      const selector = '.context-menu';
      const menuItems = findAll(`${selector} > .context-menu__item`);
      await triggerEvent(`#${menuItems[1].id}`, 'mouseover');
      await click(findAll(selectors.processEvents)[0]);
      assert.ok(actionSpy.callCount, 1, 'Window.open is called');
      actionSpy.restore();
    });
  });

  test('testing analyze registry events', async function(assert) {
    new ReduxDataHelper(initState)
      .certificatesItems(items)
      .loadMoreCertificateStatus('stopped')
      .selectedCertificatesList([])
      .certificateStatusData({})
      .coreServerId('serverId')
      .build();
    const actionSpy = sinon.spy(window, 'open');

    await render(hbs`{{certificates-container}}{{context-menu}}`);
    triggerEvent('.content-context-menu', 'contextmenu', e);

    return settled().then(async() => {
      const selector = '.context-menu';
      const menuItems = findAll(`${selector} > .context-menu__item`);
      await triggerEvent(`#${menuItems[1].id}`, 'mouseover');
      await click(findAll(selectors.registryEvents)[0]);
      assert.ok(actionSpy.callCount, 1, 'Window.open is called');
      actionSpy.restore();
    });
  });
});

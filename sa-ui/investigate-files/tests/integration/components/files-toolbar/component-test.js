import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, settled, findAll, find, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { applyPatch, revertPatch } from '../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import * as serverActions from 'investigate-shared/actions/data-creators/endpoint-server-creators';
import Service from '@ember/service';
import sinon from 'sinon';

let setState;
let transitions = [];

const item = [
  {
    checksumSha256: '365a393f3a34bf13f49306868b',
    id: '365',
    signature: { thumbprint: '', features: '' }
  }
];

module('Integration | Component | Files toolbar', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-files')
  });

  hooks.beforeEach(function() {
    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'files.certificates',
      generateURL: () => {
        return;
      },
      transitionTo: (name, args, queryParams) => {
        transitions.push({ name, queryParams });
      }
    }));
    initialize(this.owner);
    setState = (state) => {
      applyPatch(state);
    };
  });

  hooks.afterEach(function() {
    transitions = [];
    revertPatch();
  });

  test('Investigate files toolbar', async function(assert) {
    const done = assert.async();
    const actionStub = sinon.stub(serverActions, 'getEndpointServers');
    actionStub.returns(() => () => {});
    new ReduxDataHelper(setState).totalItems(3).setSelectedFileList(item).build();
    await render(hbs`{{files-toolbar}}`);
    assert.equal(findAll('.title-header').length, 1, 'Files toolbar present');
    assert.equal(findAll('.export-button').length, 1, 'Export button present');
    assert.equal(findAll('.view-certificate-button').length, 1, 'View certificate button present');
    assert.equal(find('.rsa-loader').classList.contains('is-small'), true, 'certificate rsa loader displayed');
    return settled().then(() => {
      actionStub.restore();
      done();
    });
  });
  test('On changing the service selection it calls the close risk panel actions', async function(assert) {
    assert.expect(2);
    const services = {
      serviceData: [{ id: '1', displayName: 'TEST', name: 'TEST', version: '11.1.0.0' }],
      summaryData: { startTime: 0 },
      isSummaryRetrieveError: false
    };

    new ReduxDataHelper(setState)
      .totalItems(3)
      .services(services)
      .setSelectedFileList(item)
      .build();
    this.set('closeRiskPanel', function() {
      assert.ok(true);
    });
    await render(hbs`{{files-toolbar closeRiskPanel=closeRiskPanel}}`);
    assert.equal(findAll('.rsa-investigate-query-container__service-selector').length, 1, 'Expected to render service selector');
    await click('.rsa-content-tethered-panel-trigger');
    await click('.service-selector-panel li');
  });
  test('Certificate view button disabled on selection more than 1 file', async function(assert) {
    const services = {
      serviceData: [{ id: '1', displayName: 'TEST', name: 'TEST', version: '11.1.0.0' }],
      summaryData: { startTime: 0 },
      isSummaryRetrieveError: false
    };
    const selectedFileList = new Array(11)
      .join().split(',')
      .map(function(item, index) {
        return { id: ++index, checksumSha256: index, signature: { features: '', thumbprint: ++index } };
      });
    new ReduxDataHelper(setState)
      .totalItems(3)
      .services(services)
      .setSelectedFileList(selectedFileList)
      .build();
    this.set('closeRiskPanel', function() {
      assert.ok(true);
    });
    await render(hbs`{{files-toolbar closeRiskPanel=closeRiskPanel}}`);
    assert.equal(find('.view-certificate-button').classList.contains('is-disabled'), true, 'View certificate button disabled');
    assert.equal(find('.view-certificate-button').title, 'Select a maximum of 1 files to view.', 'tooltip added to disabled button');
  });
  test('Certificate view button to navigate certificates view', async function(assert) {
    const services = {
      serviceData: [{ id: '1', displayName: 'TEST', name: 'TEST', version: '11.1.0.0' }],
      summaryData: { startTime: 0 },
      isSummaryRetrieveError: false
    };
    const selectedFileList = new Array(1)
      .join().split(',')
      .map(function(item, index) {
        return { id: ++index, checksumSha256: index, signature: { features: '', thumbprint: ++index } };
      });
    new ReduxDataHelper(setState)
      .totalItems(3)
      .services(services)
      .setSelectedFileList(selectedFileList)
      .build();
    this.set('closeRiskPanel', function() {
      assert.ok(true);
    });
    await render(hbs`{{files-toolbar closeRiskPanel=closeRiskPanel}}`);
    await click('.view-certificate-button');
    assert.deepEqual(transitions, [{
      name: 'files.certificates',
      queryParams: {}
    }]);
  });

  test('Certificate view button disabled on selection of unsigned files', async function(assert) {
    const services = {
      serviceData: [{ id: '1', displayName: 'TEST', name: 'TEST', version: '11.1.0.0' }],
      summaryData: { startTime: 0 },
      isSummaryRetrieveError: false
    };
    new ReduxDataHelper(setState)
      .totalItems(3)
      .services(services)
      .setSelectedFileList([{ id: 0 }])
      .build();
    this.set('closeRiskPanel', function() {
      assert.ok(true);
    });
    await render(hbs`{{files-toolbar closeRiskPanel=closeRiskPanel}}`);
    assert.equal(find('.view-certificate-button').classList.contains('is-disabled'), true, 'View certificate button disabled');
    assert.equal(find('.view-certificate-button').title, 'No certificates available for the selected files.', 'tooltip added to disabled button');
  });

  test('Certificate view button is disabled when endpoint server is offline', async function(assert) {
    const services = {
      serviceData: [{ id: '1', displayName: 'TEST', name: 'TEST', version: '11.1.0.0' }],
      summaryData: { startTime: 0 },
      isSummaryRetrieveError: true
    };
    new ReduxDataHelper(setState)
      .totalItems(3)
      .services(services)
      .setSelectedFileList([{ id: 0 }])
      .setEndpointServiceId('1')
      .build();

    this.set('closeRiskPanel', function() {
      assert.ok(true);
    });
    await render(hbs`{{files-toolbar closeRiskPanel=closeRiskPanel}}`);
    assert.equal(find('.view-certificate-button').classList.contains('is-disabled'), true, 'View certificate button disabled');
    assert.equal(find('.view-certificate-button').title, 'Endpoint server is offline');
  });
});

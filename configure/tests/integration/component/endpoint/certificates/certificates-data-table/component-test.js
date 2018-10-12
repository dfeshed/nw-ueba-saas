import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render, click } from '@ember/test-helpers';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Immutable from 'seamless-immutable';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';

let setState;
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
module('Integration | Component | certificates-data-table', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('configure')
  });
  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('data table rendering', async function(assert) {
    await render(hbs`{{endpoint/certificates-data-table}}`);
    assert.equal(findAll('.rsa-data-table').length, 1, 'data table has rendered.');
  });

  test('data table rendering the data', async function(assert) {
    new ReduxDataHelper(setState)
      .certificatesItems(items)
      .selectedCertificatesList([])
      .certificateStatusData({})
      .build();
    await render(hbs`{{endpoint/certificates-data-table}}`);
    assert.equal(findAll('.rsa-data-table').length, 1, 'table has rendered.');
    assert.equal(findAll('.rsa-data-table-body-row').length, 2, '2 rows rendered');
  });

  test('loader appears when certificates are loading', async function(assert) {
    new ReduxDataHelper(setState)
      .certificatesItems(items)
      .certificatesLoadingStatus('wait')
      .selectedCertificatesList([])
      .certificateStatusData({})
      .build();
    await render(hbs`{{endpoint/certificates-data-table}}`);
    assert.equal(findAll('.rsa-loader').length, 1, 'loader icon appears when certificates are still rendering.');
  });

  test('testing the load more functionality', async function(assert) {
    new ReduxDataHelper(setState)
      .certificatesItems(new Array(200))
      .loadMoreStatus('stopped')
      .selectedCertificatesList([])
      .certificateStatusData({})
      .build();
    await render(hbs`{{endpoint/certificates-data-table}}`);
    assert.equal(findAll('.rsa-data-table-load-more .rsa-form-button').length, 1, 'Load more button appears for certificates more than 100.');
    await click('.rsa-data-table-load-more');
    assert.equal(findAll('.rsa-data-table-body-row').length, 200, 'more certificates added after clicking load more');
  });

  test('testing no presence of load more button', async function(assert) {
    new ReduxDataHelper(setState)
      .certificatesItems(new Array(100))
      .loadMoreStatus('completed')
      .selectedCertificatesList([])
      .certificateStatusData({})
      .build();
    await render(hbs`{{endpoint/certificates-data-table}}`);
    assert.equal(findAll('.rsa-data-table-load-more .rsa-form-button-wrapper').length, 0, 'Load more button disappears when all certificates are loaded.');
  });

  test('no result message for no certificated', async function(assert) {
    new ReduxDataHelper(setState)
      .certificatesItems([])
      .selectedCertificatesList([])
      .certificateStatusData({})
      .build();
    await render(hbs`{{endpoint/certificates-data-table}}`);
    assert.equal(findAll('.rsa-data-table-body .rsa-panel-message .message')[0].textContent.trim(), 'No certificates were found.', 'No certificate results message is displayed.');
  });
  test('testing the certificate table row click', async function(assert) {
    new ReduxDataHelper(setState)
      .certificatesItems(items)
      .loadMoreStatus('stopped')
      .selectedCertificatesList([])
      .certificateStatusData({})
      .build();
    await render(hbs`{{endpoint/certificates-data-table}}`);
    await click(findAll('.rsa-data-table-body-row')[0]);
    assert.equal(findAll('.rsa-data-table-body-row')[0].classList.contains('is-selected'), true, 'certificate row selected');
  });

});

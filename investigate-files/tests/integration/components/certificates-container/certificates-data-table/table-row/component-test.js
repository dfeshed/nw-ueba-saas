import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { revertPatch } from '../../../../../helpers/patch-reducer';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

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

module('Integration | Component | certificates-container/certificates-data-table/table-row', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-files')
  });
  hooks.beforeEach(function() {
    this.dateFormat = this.owner.lookup('service:dateFormat');
    this.timeFormat = this.owner.lookup('service:timeFormat');
    this.timezone = this.owner.lookup('service:timezone');
    this.set('dateFormat.selected', 'MM/dd/yyyy', 'MM/dd/yyyy');
    this.set('timeFormat.selected', 'HR24', 'HR24');
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  hooks.afterEach(function() {
    revertPatch();
  });

  test('data table row rendering', async function(assert) {
    this.set('item', items[0]);
    this.set('beforeContextMenuShow', function() {
      assert.ok(true);
    });
    await render(hbs`{{certificates-container/certificates-data-table/table-row item=item beforeContextMenuShow=beforeContextMenuShow}}`);
    assert.equal(findAll('.rsa-data-table-body-row').length, 1, 'data table body row has rendered.');
    assert.equal(findAll('.content-context-menu').length, 1, 'Context menu added to row');
  });
});
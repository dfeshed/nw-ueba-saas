import { module, test, setupRenderingTest } from 'ember-qunit';

import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, click, render, triggerEvent, settled, waitUntil } from '@ember/test-helpers';
import { patchSocket } from '../../../../../helpers/patch-socket';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import drivers from '../../../state/driver.state';
import Immutable from 'seamless-immutable';

const config = [
  {
    'dataType': 'checkbox',
    'width': 20,
    'class': 'rsa-form-row-checkbox',
    'componentClass': 'rsa-form-checkbox',
    'visible': true,
    'disableSort': true,
    'headerComponentClass': 'rsa-form-checkbox'
  },
  {
    field: 'fileName',
    title: 'File Name'
  },
  {
    field: 'timeModified',
    title: 'LAST MODIFIED TIME',
    format: 'DATE'
  },
  {
    field: 'signature.features',
    title: 'Signature',
    format: 'SIGNATURE'
  }
];
const wormhole = 'wormhole-context-menu';

let setState;

const callback = () => {};
const e = {
  clientX: 20,
  clientY: 20,
  view: {
    window: {
      innerWidth: 100,
      innerHeight: 100
    }
  }
};

const fileContextSelections = [
  {
    id: 'drivers_13',
    fileName: 'acpi.sys',
    checksumSha1: '79a1a29d267d6480138d2768041c46430f77bcf5',
    checksumSha256: 'ae69c142dc2210a4ae657c23cea4a6e7cb32c4f4eba039414123cac52157509b',
    checksumMd5: 'cea80c80bed809aa0da6febc04733349',
    signature: {
      timeStamp: '2010-11-20T12:29:16.000+0000',
      thumbprint: '02eceea9d5e0a9f3e39b6f4ec3f7131ed4e352c4',
      features: [
        'microsoft',
        'signed',
        'valid'
      ],
      signer: 'Microsoft Windows'
    },
    size: 274304,
    machineOsType: 'windows',
    downloadInfo: { 'status': 'Error' },
    features: [
      'file.arch64',
      'file.subsystemNative',
      'file.versionInfoPresent',
      'file.resourceDirectoryPresent',
      'file.relocationDirectoryPresent',
      'file.debugDirectoryPresent',
      'file.richSignaturePresent',
      'file.codeSectionWritable',
      'file.companyNameContainsText',
      'file.descriptionContainsText',
      'file.versionContainsText',
      'file.internalNameContainsText',
      'file.legalCopyrightContainsText',
      'file.originalFilenameContainsText',
      'file.productNameContainsText',
      'file.productVersionContainsText',
      'file.standardVersionMetaPresent'
    ],
    format: 'pe'
  },
  {
    id: 'drivers_73',
    fileName: 'afd.sys',
    checksumSha1: '96c00157276e982c7d883bec5478eb1fb242cf1f',
    checksumSha256: '673c2b498744c7eb846f6bd4fdc852b0a9722377d75fd694f7f78e727adf4563',
    checksumMd5: '1151fd4fb0216cfed887bfde29ebd516',
    signature: {
      timeStamp: '2010-11-20T15:32:51.000+0000',
      thumbprint: '02eceea9d5e0a9f3e39b6f4ec3f7131ed4e352c4',
      features: [
        'microsoft',
        'signed',
        'valid',
        'catalog'
      ],
      signer: 'Microsoft Windows'
    },
    size: 338944,
    machineOsType: 'windows',
    downloadInfo: { 'status': 'Downloaded' },
    features: [
      'file.arch64',
      'file.subsystemNative',
      'file.versionInfoPresent',
      'file.resourceDirectoryPresent',
      'file.relocationDirectoryPresent',
      'file.debugDirectoryPresent',
      'file.richSignaturePresent',
      'file.codeSectionWritable',
      'file.companyNameContainsText',
      'file.descriptionContainsText',
      'file.versionContainsText',
      'file.internalNameContainsText',
      'file.legalCopyrightContainsText',
      'file.originalFilenameContainsText',
      'file.productNameContainsText',
      'file.productVersionContainsText',
      'file.standardVersionMetaPresent'
    ],
    format: 'pe'
  }
];

module('Integration | Component | host-detail/utils/file-context-wrapper', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    this.set('storeName', 'drivers');
    this.set('tabName', 'DRIVER');
    this.set('columnConfig', config);

    // Right click setup
    const wormholeDiv = document.createElement('div');
    wormholeDiv.id = wormhole;
    document.querySelector('#ember-testing').appendChild(wormholeDiv);
    document.addEventListener('contextmenu', callback);
  });

  hooks.afterEach(function() {
    const wormholeElement = document.querySelector('#wormhole-context-menu');
    if (wormholeElement) {
      document.querySelector('#ember-testing').removeChild(wormholeElement);
    }
  });

  test('it should render the action bar, table and property details', async function(assert) {
    this.set('propertyConfig', [{ fields: [] }]);
    new ReduxDataHelper(setState).drivers(drivers).fileContextSelections(fileContextSelections).build();
    await render(hbs`
     <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{host-detail/utils/file-context-wrapper propertyConfig=propertyConfig storeName=storeName tabName=tabName columnsConfig=columnConfig}}
    `);
    assert.equal(findAll('.rsa-data-table').length, 1, 'Table is rendered');
    assert.equal(findAll('.file-actionbar').length, 1, 'action bar rendered');
    await click('.rsa-data-table-body-row:nth-child(2)');
    assert.equal(findAll('.rsa-nav-tab').length, 2, '2 tabs are rendered in detail property');
    assert.equal(findAll('.rsa-nav-tab.is-active')[0].textContent.trim(), 'File Details', 'Default tab is file details');
    assert.equal(findAll('.host-property-panel').length, 1, 'Property panel is rendered');
    await click(findAll('.rsa-nav-tab')[1]);
    assert.equal(findAll('.rsa-nav-tab.is-active')[0].textContent.trim(), 'Local Risk Details', 'Risk details tab is selected');
    assert.equal(findAll('.risk-properties').length, 1, 'Risk properties is rendered');
  });

  test('property panel not rendered', async function(assert) {
    await render(hbs`{{host-detail/utils/file-context-wrapper storeName=storeName tabName=tabName columnsConfig=columnConfig}}`);
    assert.equal(findAll('.rsa-data-table').length, 1, 'Table is rendered');
    assert.equal(findAll('.host-property-panel').length, 0, 'No property panel');
    assert.equal(findAll('.file-actionbar').length, 1, 'action bar rendered');

  });

  test('Download to server websocket called', async function(assert) {

    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('endpointCanManageFiles', true);
    const hostDetails = {
      machineIdentity: {
        agentMode: 'Advanced'
      }
    };
    const fileContextSelections = [
      {
        id: 'drivers_13',
        fileName: 'acpi.sys',
        checksumSha1: '79a1a29d267d6480138d2768041c46430f77bcf5',
        checksumSha256: 'ae69c142dc2210a4ae657c23cea4a6e7cb32c4f4eba039414123cac52157509b',
        checksumMd5: 'cea80c80bed809aa0da6febc04733349',
        signature: {
          timeStamp: '2010-11-20T12:29:16.000+0000',
          thumbprint: '02eceea9d5e0a9f3e39b6f4ec3f7131ed4e352c4',
          features: [
            'microsoft',
            'signed',
            'valid'
          ],
          signer: 'Microsoft Windows'
        },
        size: 274304,
        machineOsType: 'windows',
        downloadInfo: { 'status': 'Error' },
        features: [
          'file.arch64',
          'file.subsystemNative',
          'file.versionInfoPresent',
          'file.resourceDirectoryPresent',
          'file.relocationDirectoryPresent',
          'file.debugDirectoryPresent',
          'file.richSignaturePresent',
          'file.codeSectionWritable',
          'file.companyNameContainsText',
          'file.descriptionContainsText',
          'file.versionContainsText',
          'file.internalNameContainsText',
          'file.legalCopyrightContainsText',
          'file.originalFilenameContainsText',
          'file.productNameContainsText',
          'file.productVersionContainsText',
          'file.standardVersionMetaPresent'
        ],
        format: 'pe'
      },
      {
        id: 'drivers_73',
        fileName: 'afd.sys',
        checksumSha1: '96c00157276e982c7d883bec5478eb1fb242cf1f',
        checksumSha256: '673c2b498744c7eb846f6bd4fdc852b0a9722377d75fd694f7f78e727adf4563',
        checksumMd5: '1151fd4fb0216cfed887bfde29ebd516',
        signature: {
          timeStamp: '2010-11-20T15:32:51.000+0000',
          thumbprint: '02eceea9d5e0a9f3e39b6f4ec3f7131ed4e352c4',
          features: [
            'microsoft',
            'signed',
            'valid',
            'catalog'
          ],
          signer: 'Microsoft Windows'
        },
        size: 338944,
        machineOsType: 'windows',
        downloadInfo: { 'status': 'Downloaded' },
        features: [
          'file.arch64',
          'file.subsystemNative',
          'file.versionInfoPresent',
          'file.resourceDirectoryPresent',
          'file.relocationDirectoryPresent',
          'file.debugDirectoryPresent',
          'file.richSignaturePresent',
          'file.codeSectionWritable',
          'file.companyNameContainsText',
          'file.descriptionContainsText',
          'file.versionContainsText',
          'file.internalNameContainsText',
          'file.legalCopyrightContainsText',
          'file.originalFilenameContainsText',
          'file.productNameContainsText',
          'file.productVersionContainsText',
          'file.standardVersionMetaPresent'
        ],
        format: 'pe'
      }
    ];
    const selectedHostList = [{
      id: 1,
      version: '4.3.0.0',
      managed: true
    }];
    new ReduxDataHelper(setState)
      .drivers(drivers)
      .host(hostDetails)
      .fileContextSelections(fileContextSelections)
      .selectedHostList(selectedHostList)
      .build();
    await render(hbs`{{host-detail/utils/file-context-wrapper storeName=storeName tabName=tabName columnsConfig=columnConfig}}`);

    assert.expect(3);

    patchSocket((method, modelName, query) => {
      assert.equal(method, 'downloadFileToServer');
      assert.equal(modelName, 'agent');
      assert.deepEqual(query, {
        data: {
          agentId: null,
          files: [
            {
              fileName: 'acpi.sys',
              hash: 'ae69c142dc2210a4ae657c23cea4a6e7cb32c4f4eba039414123cac52157509b',
              path: undefined
            },
            {
              fileName: 'afd.sys',
              hash: '673c2b498744c7eb846f6bd4fdc852b0a9722377d75fd694f7f78e727adf4563',
              path: undefined
            }
          ]
        }
      });
    });

    await click('.more-action-button');
    await click('.rsa-dropdown-action-list .panel3');
  });

  test('Reset Risk Score Action is not present', async function(assert) {
    const hostDetails = {
      machineIdentity: {
        agentMode: 'Advanced'
      }
    };
    const fileContextSelections = [
      {
        id: 'drivers_13',
        fileName: 'acpi.sys',
        checksumSha1: '79a1a29d267d6480138d2768041c46430f77bcf5',
        checksumSha256: 'ae69c142dc2210a4ae657c23cea4a6e7cb32c4f4eba039414123cac52157509b',
        checksumMd5: 'cea80c80bed809aa0da6febc04733349',
        signature: {
          timeStamp: '2010-11-20T12:29:16.000+0000',
          thumbprint: '02eceea9d5e0a9f3e39b6f4ec3f7131ed4e352c4',
          features: [
            'microsoft',
            'signed',
            'valid'
          ],
          signer: 'Microsoft Windows'
        },
        size: 274304,
        machineOsType: 'windows',
        downloadInfo: { 'status': 'Error' },
        features: [
          'file.arch64',
          'file.subsystemNative',
          'file.versionInfoPresent',
          'file.resourceDirectoryPresent',
          'file.relocationDirectoryPresent',
          'file.debugDirectoryPresent',
          'file.richSignaturePresent',
          'file.codeSectionWritable',
          'file.companyNameContainsText',
          'file.descriptionContainsText',
          'file.versionContainsText',
          'file.internalNameContainsText',
          'file.legalCopyrightContainsText',
          'file.originalFilenameContainsText',
          'file.productNameContainsText',
          'file.productVersionContainsText',
          'file.standardVersionMetaPresent'
        ],
        format: 'pe'
      },
      {
        id: 'drivers_73',
        fileName: 'afd.sys',
        checksumSha1: '96c00157276e982c7d883bec5478eb1fb242cf1f',
        checksumSha256: '673c2b498744c7eb846f6bd4fdc852b0a9722377d75fd694f7f78e727adf4563',
        checksumMd5: '1151fd4fb0216cfed887bfde29ebd516',
        signature: {
          timeStamp: '2010-11-20T15:32:51.000+0000',
          thumbprint: '02eceea9d5e0a9f3e39b6f4ec3f7131ed4e352c4',
          features: [
            'microsoft',
            'signed',
            'valid',
            'catalog'
          ],
          signer: 'Microsoft Windows'
        },
        size: 338944,
        machineOsType: 'windows',
        downloadInfo: { 'status': 'Downloaded' },
        features: [
          'file.arch64',
          'file.subsystemNative',
          'file.versionInfoPresent',
          'file.resourceDirectoryPresent',
          'file.relocationDirectoryPresent',
          'file.debugDirectoryPresent',
          'file.richSignaturePresent',
          'file.codeSectionWritable',
          'file.companyNameContainsText',
          'file.descriptionContainsText',
          'file.versionContainsText',
          'file.internalNameContainsText',
          'file.legalCopyrightContainsText',
          'file.originalFilenameContainsText',
          'file.productNameContainsText',
          'file.productVersionContainsText',
          'file.standardVersionMetaPresent'
        ],
        format: 'pe'
      }
    ];
    const selectedHostList = [{
      id: 1,
      version: '4.3.0.0',
      managed: true
    }];

    new ReduxDataHelper(setState)
      .drivers(drivers)
      .host(hostDetails)
      .fileContextSelections(fileContextSelections)
      .selectedHostList(selectedHostList)
      .build();
    await render(hbs`{{host-detail/utils/file-context-wrapper accessControl=accessControl storeName=storeName tabName=tabName columnsConfig=columnConfig}}`);
    await click('.more-action-button');
    assert.equal(findAll('.rsa-dropdown-action-list .panel6').length, 0, 'Reset Risk Score Action is not present');
  });

  test('Context menu rendered', async function(assert) {

    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('endpointCanManageFiles', true);
    const hostDetails = {
      machineIdentity: {
        agentMode: 'Advanced'
      }
    };

    new ReduxDataHelper(setState).drivers(drivers).host(hostDetails).fileContextSelections(fileContextSelections).build();
    await render(hbs`
    <style>
        box, section {
          min-height: 2000px
        }
      </style>
      {{host-detail/utils/file-context-wrapper 
      accessControl=accessControl 
      storeName=storeName 
      tabName=tabName 
      columnsConfig=columnConfig}}
      {{context-menu}}`);

    triggerEvent(findAll('.rsa-data-table-body-rows .rsa-form-checkbox-label')[0], 'contextmenu', e);
    return settled().then(() => {
      const selector = '.context-menu';
      const items = findAll(`${selector} > .context-menu__item`);
      assert.equal(items.length, 7, 'Context menu rendered');
    });
  });

  test('Save a Local copy sets download link', async function(assert) {
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('endpointCanManageFiles', true);
    const hostDetails = {
      machineIdentity: {
        agentMode: 'Advanced'
      }
    };
    const endpointQuery = {
      serverId: 'serverId'
    };
    const selectedHostList = [{
      id: 1,
      version: '4.3.0.0',
      managed: true
    }];
    new ReduxDataHelper(setState)
      .drivers(drivers)
      .host(hostDetails)
      .fileContextSelections(fileContextSelections)
      .endpointQuery(endpointQuery)
      .selectedHostList(selectedHostList)
      .build();
    await render(hbs`
    <style>
        box, section {
          min-height: 2000px
        }
      </style>
      {{host-detail/utils/file-context-wrapper 
      accessControl=accessControl 
      storeName=storeName 
      tabName=tabName 
      columnsConfig=columnConfig}}
      {{context-menu}}`);
    triggerEvent(findAll('.rsa-data-table-body-rows .rsa-form-checkbox-label')[1], 'contextmenu', e);
    return settled().then(async() => {
      const selector = '.context-menu';
      await click(findAll(`${selector} > .context-menu__item`)[5]);
      const redux = this.owner.lookup('service:redux');
      return waitUntil(() => redux.getState().endpoint.detailsInput.downloadLink !== null, { timeout: 6000 })
        .then(() => {
          assert.equal(redux.getState().endpoint.detailsInput.downloadLink,
            '/rsa/endpoint/serverId/file/download?id=123&filename=halmacpi.dll.zip', 'download link prepared');
        });
    });

  });

  test('areAllFilesNotDownloadedToServer flag set to false as at least one of the files is not already downloaded', async function(assert) {

    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('endpointCanManageFiles', true);
    const hostDetails = {
      machineIdentity: {
        agentMode: 'Advanced'
      }
    };
    const fileContextSelections = [
      {
        id: 'drivers_13',
        fileName: 'acpi.sys',
        checksumSha1: '79a1a29d267d6480138d2768041c46430f77bcf5',
        checksumSha256: 'ae69c142dc2210a4ae657c23cea4a6e7cb32c4f4eba039414123cac52157509b',
        checksumMd5: 'cea80c80bed809aa0da6febc04733349',
        signature: {
          timeStamp: '2010-11-20T12:29:16.000+0000',
          thumbprint: '02eceea9d5e0a9f3e39b6f4ec3f7131ed4e352c4',
          features: [
            'microsoft',
            'signed',
            'valid'
          ],
          signer: 'Microsoft Windows'
        },
        size: 274304,
        machineOsType: 'windows',
        downloadInfo: { 'status': 'Error' },
        features: [
          'file.arch64',
          'file.subsystemNative',
          'file.versionInfoPresent',
          'file.resourceDirectoryPresent',
          'file.relocationDirectoryPresent',
          'file.debugDirectoryPresent',
          'file.richSignaturePresent',
          'file.codeSectionWritable',
          'file.companyNameContainsText',
          'file.descriptionContainsText',
          'file.versionContainsText',
          'file.internalNameContainsText',
          'file.legalCopyrightContainsText',
          'file.originalFilenameContainsText',
          'file.productNameContainsText',
          'file.productVersionContainsText',
          'file.standardVersionMetaPresent'
        ],
        format: 'pe'
      },
      {
        id: 'drivers_73',
        fileName: 'afd.sys',
        checksumSha1: '96c00157276e982c7d883bec5478eb1fb242cf1f',
        checksumSha256: '673c2b498744c7eb846f6bd4fdc852b0a9722377d75fd694f7f78e727adf4563',
        checksumMd5: '1151fd4fb0216cfed887bfde29ebd516',
        signature: {
          timeStamp: '2010-11-20T15:32:51.000+0000',
          thumbprint: '02eceea9d5e0a9f3e39b6f4ec3f7131ed4e352c4',
          features: [
            'microsoft',
            'signed',
            'valid',
            'catalog'
          ],
          signer: 'Microsoft Windows'
        },
        size: 338944,
        machineOsType: 'windows',
        downloadInfo: { 'status': 'Downloaded' },
        features: [
          'file.arch64',
          'file.subsystemNative',
          'file.versionInfoPresent',
          'file.resourceDirectoryPresent',
          'file.relocationDirectoryPresent',
          'file.debugDirectoryPresent',
          'file.richSignaturePresent',
          'file.codeSectionWritable',
          'file.companyNameContainsText',
          'file.descriptionContainsText',
          'file.versionContainsText',
          'file.internalNameContainsText',
          'file.legalCopyrightContainsText',
          'file.originalFilenameContainsText',
          'file.productNameContainsText',
          'file.productVersionContainsText',
          'file.standardVersionMetaPresent'
        ],
        format: 'pe'
      }
    ];
    const selectedHostList = [{
      id: 1,
      version: '4.3.0.0',
      managed: true
    }];
    new ReduxDataHelper(setState)
      .drivers(drivers)
      .host(hostDetails)
      .fileContextSelections(fileContextSelections)
      .selectedHostList(selectedHostList)
      .build();
    await render(hbs`{{host-detail/utils/file-context-wrapper accessControl=accessControl storeName=storeName tabName=tabName columnsConfig=columnConfig}}`);

    await click('.more-action-button');

    assert.equal(findAll('.rsa-dropdown-action-list .panel3.disabled').length, 0, 'download to server is enabled');
  });

  test('areAllFilesNotDownloadedToServer flag set to false as at least one of the files does not have downloadInfo', async function(assert) {

    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('endpointCanManageFiles', true);
    const hostDetails = {
      machineIdentity: {
        agentMode: 'Advanced'
      }
    };
    const fileContextSelections = [
      {
        id: 'drivers_13',
        fileName: 'acpi.sys',
        checksumSha1: '79a1a29d267d6480138d2768041c46430f77bcf5',
        checksumSha256: 'ae69c142dc2210a4ae657c23cea4a6e7cb32c4f4eba039414123cac52157509b',
        checksumMd5: 'cea80c80bed809aa0da6febc04733349',
        signature: {
          timeStamp: '2010-11-20T12:29:16.000+0000',
          thumbprint: '02eceea9d5e0a9f3e39b6f4ec3f7131ed4e352c4',
          features: [
            'microsoft',
            'signed',
            'valid'
          ],
          signer: 'Microsoft Windows'
        },
        size: 274304,
        machineOsType: 'windows',
        downloadInfo: { 'status': 'Downloaded' },
        features: [
          'file.arch64',
          'file.subsystemNative',
          'file.versionInfoPresent',
          'file.resourceDirectoryPresent',
          'file.relocationDirectoryPresent',
          'file.debugDirectoryPresent',
          'file.richSignaturePresent',
          'file.codeSectionWritable',
          'file.companyNameContainsText',
          'file.descriptionContainsText',
          'file.versionContainsText',
          'file.internalNameContainsText',
          'file.legalCopyrightContainsText',
          'file.originalFilenameContainsText',
          'file.productNameContainsText',
          'file.productVersionContainsText',
          'file.standardVersionMetaPresent'
        ],
        format: 'pe'
      },
      {
        id: 'drivers_73',
        fileName: 'afd.sys',
        checksumSha1: '96c00157276e982c7d883bec5478eb1fb242cf1f',
        checksumSha256: '673c2b498744c7eb846f6bd4fdc852b0a9722377d75fd694f7f78e727adf4563',
        checksumMd5: '1151fd4fb0216cfed887bfde29ebd516',
        signature: {
          timeStamp: '2010-11-20T15:32:51.000+0000',
          thumbprint: '02eceea9d5e0a9f3e39b6f4ec3f7131ed4e352c4',
          features: [
            'microsoft',
            'signed',
            'valid',
            'catalog'
          ],
          signer: 'Microsoft Windows'
        },
        size: 338944,
        machineOsType: 'windows',
        features: [
          'file.arch64',
          'file.subsystemNative',
          'file.versionInfoPresent',
          'file.resourceDirectoryPresent',
          'file.relocationDirectoryPresent',
          'file.debugDirectoryPresent',
          'file.richSignaturePresent',
          'file.codeSectionWritable',
          'file.companyNameContainsText',
          'file.descriptionContainsText',
          'file.versionContainsText',
          'file.internalNameContainsText',
          'file.legalCopyrightContainsText',
          'file.originalFilenameContainsText',
          'file.productNameContainsText',
          'file.productVersionContainsText',
          'file.standardVersionMetaPresent'
        ],
        format: 'pe'
      }
    ];
    const selectedHostList = [{
      id: 1,
      version: '4.3.0.0',
      managed: true
    }];
    new ReduxDataHelper(setState)
      .drivers(drivers)
      .host(hostDetails)
      .fileContextSelections(fileContextSelections)
      .selectedHostList(selectedHostList)
      .build();
    await render(hbs`{{host-detail/utils/file-context-wrapper accessControl=accessControl storeName=storeName tabName=tabName columnsConfig=columnConfig}}`);

    await click('.more-action-button');

    assert.equal(findAll('.rsa-dropdown-action-list .panel3.disabled').length, 0, 'download to server is enabled');
  });

  test('areAllFilesNotDownloadedToServer flag to true as file is already downloaded', async function(assert) {

    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('endpointCanManageFiles', true);
    const hostDetails = {
      machineIdentity: {
        agentMode: 'Advanced'
      }
    };
    const fileContextSelections = [
      {
        id: 'drivers_73',
        fileName: 'afd.sys',
        checksumSha1: '96c00157276e982c7d883bec5478eb1fb242cf1f',
        checksumSha256: '673c2b498744c7eb846f6bd4fdc852b0a9722377d75fd694f7f78e727adf4563',
        checksumMd5: '1151fd4fb0216cfed887bfde29ebd516',
        signature: {
          timeStamp: '2010-11-20T15:32:51.000+0000',
          thumbprint: '02eceea9d5e0a9f3e39b6f4ec3f7131ed4e352c4',
          features: [
            'microsoft',
            'signed',
            'valid',
            'catalog'
          ],
          signer: 'Microsoft Windows'
        },
        size: 338944,
        machineOsType: 'windows',
        downloadInfo: { 'status': 'Downloaded' },
        features: [
          'file.arch64',
          'file.subsystemNative',
          'file.versionInfoPresent',
          'file.resourceDirectoryPresent',
          'file.relocationDirectoryPresent',
          'file.debugDirectoryPresent',
          'file.richSignaturePresent',
          'file.codeSectionWritable',
          'file.companyNameContainsText',
          'file.descriptionContainsText',
          'file.versionContainsText',
          'file.internalNameContainsText',
          'file.legalCopyrightContainsText',
          'file.originalFilenameContainsText',
          'file.productNameContainsText',
          'file.productVersionContainsText',
          'file.standardVersionMetaPresent'
        ],
        format: 'pe'
      }
    ];
    new ReduxDataHelper(setState).drivers(drivers).host(hostDetails).fileContextSelections(fileContextSelections).build();
    await render(hbs`{{host-detail/utils/file-context-wrapper accessControl=accessControl storeName=storeName tabName=tabName columnsConfig=columnConfig}}`);

    await click('.more-action-button');

    assert.equal(findAll('.rsa-dropdown-action-list .panel3.disabled').length, 1, 'download to server is disabled');
  });
});

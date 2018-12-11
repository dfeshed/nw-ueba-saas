import { module, test, setupRenderingTest } from 'ember-qunit';

import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, click, render } from '@ember/test-helpers';
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

let setState;

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
  });


  test('it should render the action bar, table and property panel', async function(assert) {
    this.set('propertyConfig', [{}]);
    await render(hbs`{{host-detail/utils/file-context-wrapper propertyConfig=propertyConfig storeName=storeName tabName=tabName columnsConfig=columnConfig}}`);
    assert.equal(findAll('.rsa-data-table').length, 1, 'Table is rendered');
    assert.equal(findAll('.host-property-panel').length, 1, 'Property panel rendered');
    assert.equal(findAll('.file-actionbar').length, 1, 'action bar rendered');
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
    new ReduxDataHelper(setState).drivers(drivers).host(hostDetails).fileContextSelections(fileContextSelections).build();
    await render(hbs`{{host-detail/utils/file-context-wrapper accessControl=accessControl storeName=storeName tabName=tabName columnsConfig=columnConfig}}`);

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
    new ReduxDataHelper(setState).drivers(drivers).host(hostDetails).fileContextSelections(fileContextSelections).build();
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
    new ReduxDataHelper(setState).drivers(drivers).host(hostDetails).fileContextSelections(fileContextSelections).build();
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

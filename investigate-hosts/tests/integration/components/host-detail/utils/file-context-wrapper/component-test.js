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

    new ReduxDataHelper(setState).drivers(drivers).build();
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
});

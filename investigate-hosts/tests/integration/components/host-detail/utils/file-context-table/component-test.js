import { module, test, setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, findAll, find, render, waitUntil, triggerEvent, settled } from '@ember/test-helpers';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import { patchSocket } from '../../../../../helpers/patch-socket';

const fileContext = {
  1: {
    id: 1,
    fileName: 'test',
    timeModified: 12313221,
    fileProperties: {
      checksumSha256: 'test',
      checksumSha1: 'test',
      checksumMd5: 'test',
      signature: {
        thumbprint: 1
      }
    },
    signature: {
      features: ['microsoft', 'valid']
    }
  },
  2: {
    id: 2,
    fileName: 'test1',
    timeModified: 12313221,
    fileProperties: {
      checksumSha256: 'test',
      checksumSha1: 'test',
      checksumMd5: 'test',
      signature: {
        thumbprint: 1
      }
    },
    signature: {
      features: ['microsoft', 'valid']
    }
  },
  3: {
    id: 3,
    fileName: 'test2',
    timeModified: 12313221,
    fileProperties: {
      checksumSha256: 'test',
      checksumSha1: 'test',
      checksumMd5: 'test',
      signature: {
        thumbprint: 1
      }
    },
    signature: {
      features: ['microsoft', 'valid']
    }
  }
};

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
    title: 'File Name',
    format: 'FILENAME'
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
  },
  {
    field: 'machineCount',
    title: 'Machine Count',
    format: 'MACHINECOUNT'
  },
  {
    field: 'machineFileScore',
    title: 'Local Risk Score',
    width: '8vw'
  }
];

let initState;

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
const wormhole = 'wormhole-context-menu';


module('Integration | Component | host-detail/utils/file-context-table', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    initState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    const wormholeDiv = document.createElement('div');
    wormholeDiv.id = wormhole;
    document.querySelector('#ember-testing').appendChild(wormholeDiv);
    document.addEventListener('contextmenu', callback);
    this.set('storeName', 'drivers');
    this.set('tabName', 'DRIVER');
    this.set('columnConfig', config);
  });

  hooks.afterEach(function() {
    const wormholeElement = document.querySelector('#wormhole-context-menu');
    if (wormholeElement) {
      document.querySelector('#ember-testing').removeChild(wormholeElement);
    }
  });

  test('should show loading indicator', async function(assert) {
    initState({
      endpoint: {
        drivers: {
          contextLoadingStatus: 'wait'
        }
      }
    });
    await render(hbs`{{host-detail/utils/file-context-table storeName=storeName tabName=tabName columnsConfig=columnConfig}}`);
    assert.equal(findAll('.rsa-loader.is-larger').length, 1, 'Rsa loader displayed');
  });

  test('Should return the length of items in the file context table', async function(assert) {
    initState({
      endpoint: {
        drivers: {
          fileContext,
          contextLoadingStatus: 'completed'
        }
      }
    });
    this.set('storeName', 'drivers');
    this.set('tabName', 'DRIVER');
    this.set('columnConfig', config);
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{host-detail/utils/file-context-table storeName=storeName tabName=tabName columnsConfig=columnConfig}}`);
    return waitUntil(() => findAll('.rsa-data-table-body-row').length > 0, { timeout: 6000 }).then(() => {
      assert.equal(findAll('.rsa-data-table-body-row').length, 3, 'Returned the number of rows/length of the table');
    });
  });


  test('Check that no results message rendered if no data items', async function(assert) {
    initState({
      endpoint: {
        drivers: {
          fileContext: {},
          contextLoadingStatus: 'completed'
        }
      }
    });
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{host-detail/utils/file-context-table storeName=storeName tabName=tabName columnsConfig=columnConfig}}`);
    await waitUntil(() => findAll('.rsa-data-table-body').length > 0, { timeout: 6000 });
    assert.equal(find('.rsa-data-table-body').textContent.trim(), 'No Results Found.', 'No results message rendered for no data items');
  });

  test('row click action select the row', async function(assert) {
    initState({
      endpoint: {
        drivers: {
          fileContext,
          contextLoadingStatus: 'completed'
        }
      }
    });
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{host-detail/utils/file-context-table storeName=storeName tabName=tabName columnsConfig=columnConfig}}`);
    await waitUntil(() => findAll('.rsa-data-table-body-row').length > 0, { timeout: 6000 });
    await click('.rsa-data-table-body-row:nth-child(2)');
    assert.equal(findAll('.rsa-data-table-body-row:nth-child(2).is-selected').length, 1, 'Second row is selected');
  });


  test('Check that sort action is called', async function(assert) {
    assert.expect(2);
    initState({
      endpoint: {
        drivers: {
          fileContext,
          contextLoadingStatus: 'completed'
        }
      }
    });
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{host-detail/utils/file-context-table storeName=storeName tabName=tabName columnsConfig=columnConfig}}`);
    return waitUntil(() => findAll('.rsa-data-table-body-row').length > 0, { timeout: 6000 }).then(async() => {
      assert.equal(findAll('.rsa-data-table-header-cell:nth-child(2) i.rsa-icon-arrow-up-7-filled').length, 1, 'rsa arrow-up icon before sorting');
      await click('.rsa-data-table-header-cell:nth-child(2) .rsa-icon');
      assert.equal(findAll('.rsa-data-table-header-cell:nth-child(2) i.rsa-icon-arrow-down-7-filled').length, 1, 'rsa arrow-down icon after sorting');
    });

  });

  test('External action is called on clicking the sor', async function(assert) {
    assert.expect(1);
    initState({
      endpoint: {
        drivers: {
          fileContext,
          contextLoadingStatus: 'completed'
        }
      }
    });
    this.set('closePropertyPanel', () => {
      assert.ok(true);
    });
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{host-detail/utils/file-context-table storeName=storeName tabName=tabName closePropertyPanel=closePropertyPanel columnsConfig=columnConfig}}`);
    return waitUntil(() => findAll('.rsa-data-table-body-row').length > 0, { timeout: 6000 }).then(async() => {
      await click('.rsa-data-table-header-cell:nth-child(2) .rsa-icon');
    });
  });


  test('Load More is shown for paged items', async function(assert) {
    this.set('isPaginated', true);
    initState({
      endpoint: {
        drivers: {
          fileContext,
          contextLoadingStatus: 'completed'
        }
      }
    });
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{host-detail/utils/file-context-table isPaginated=isPaginated storeName=storeName tabName=tabName columnsConfig=columnConfig}}`);
    await waitUntil(() => findAll('.rsa-data-table-body-row').length > 0, { timeout: 6000 });
    assert.equal(findAll('.rsa-data-table-load-more').length, 1, 'Load more button is present');
  });

  test('clicking the checkbox will update the state', async function(assert) {
    initState({
      endpoint: {
        drivers: {
          fileContext,
          fileContextSelections: [],
          contextLoadingStatus: 'completed'
        }
      }
    });
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{host-detail/utils/file-context-table isPaginated=isPaginated storeName=storeName tabName=tabName columnsConfig=columnConfig}}`);
    await waitUntil(() => findAll('.rsa-data-table-body-row').length > 0, { timeout: 6000 });
    await click(document.querySelector('.rsa-data-table-body-row .rsa-form-checkbox-label'));
    assert.equal(findAll('.rsa-data-table-body-rows .rsa-form-checkbox-label.checked').length, 1, 'checkbox is selected');
    await click(document.querySelector('.rsa-data-table-body-row .rsa-form-checkbox-label'));
    assert.equal(findAll('.rsa-data-table-body-rows .rsa-form-checkbox-label.checked').length, 0, 'checkbox is selected');
  });

  test('footer is displayed with count', async function(assert) {
    initState({
      endpoint: {
        drivers: {
          fileContext,
          totalItems: 3,
          fileContextSelections: [],
          contextLoadingStatus: 'completed'
        }
      }
    });
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{host-detail/utils/file-context-table isPaginated=isPaginated storeName=storeName tabName=tabName columnsConfig=columnConfig}}`);
    await waitUntil(() => findAll('.rsa-data-table-body-row').length > 0, { timeout: 6000 });
    assert.ok(find('.file-info').textContent.trim().includes('3 out of 3'));
  });

  test('it opens the service list modal', async function(assert) {
    initState({
      endpoint: {
        drivers: {
          fileContext,
          fileContextSelections: [],
          contextLoadingStatus: 'completed'
        }
      }
    });
    await render(hbs`
      <div id='modalDestination'></div>
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{host-detail/utils/file-context-table showServiceModal=true isPaginated=isPaginated storeName=storeName tabName=tabName columnsConfig=columnConfig}}
    `);
    await waitUntil(() => findAll('.rsa-data-table-body-row').length > 0, { timeout: 6000 });
    assert.equal(document.querySelectorAll('#modalDestination .service-modal').length, 1);
  });


  test('it opens edit status modal', async function(assert) {
    initState({
      endpoint: {
        drivers: {
          fileContext,
          fileContextSelections: [],
          contextLoadingStatus: 'completed'
        }
      }
    });
    await render(hbs`
      <div id='modalDestination'></div>
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{host-detail/utils/file-context-table showFileStatusModal=true isPaginated=isPaginated storeName=storeName tabName=tabName columnsConfig=columnConfig}}
    `);
    await waitUntil(() => findAll('.rsa-data-table-body-row').length > 0, { timeout: 6000 });
    assert.equal(document.querySelectorAll('#modalDestination .file-status-modal').length, 1);
  });

  test('File name is an anchor tag', async function(assert) {
    initState({
      endpoint: {
        drivers: {
          fileContext,
          fileContextSelections: [],
          contextLoadingStatus: 'completed'
        }
      }
    });
    await render(hbs`
      <div id='modalDestination'></div>
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{host-detail/utils/file-context-table showFileStatusModal=true isPaginated=isPaginated storeName=storeName tabName=tabName columnsConfig=columnConfig}}
    `);
    await waitUntil(() => findAll('.rsa-data-table-body-row').length > 0, { timeout: 6000 });
    assert.equal(findAll('a.file-name-link').length, 3);
    assert.equal(find('a.file-name-link').href.search('/investigate/files/file'), 21);
  });

  test('Reset risk score confirmation dialog is opened', async function(assert) {
    initState({
      endpoint: {
        drivers: {
          fileContext,
          fileContextSelections: [],
          contextLoadingStatus: 'completed'
        }
      }
    });
    await render(hbs`
      <div id='modalDestination'></div>
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{host-detail/utils/file-context-table showResetScoreModal=true isPaginated=isPaginated storeName=storeName tabName=tabName columnsConfig=columnConfig}}
    `);
    await waitUntil(() => findAll('.rsa-data-table-body-row').length > 0, { timeout: 6000 });
    assert.equal(findAll('#modalDestination .modal-content.reset-risk-score').length, 1, 'reset risk score confirmation dialog is opened');
  });

  test('Reset risk score confirmation dialog is closed on click of cancel', async function(assert) {
    initState({
      endpoint: {
        drivers: {
          fileContext,
          fileContextSelections: [],
          contextLoadingStatus: 'completed'
        }
      }
    });
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      <div id='modalDestination'></div>
      {{host-detail/utils/file-context-table showResetScoreModal=true isPaginated=isPaginated storeName=storeName tabName=tabName columnsConfig=columnConfig}}
    `);
    await waitUntil(() => findAll('.rsa-data-table-body-row').length > 0, { timeout: 6000 });
    assert.equal(findAll('#modalDestination .modal-content.reset-risk-score').length, 1, 'reset risk score confirmation dialog is opened');
    await click('.closeReset');
    assert.equal(findAll('.modal-content.reset-risk-score').length, 0, 'Reset confirmation dialog is closed');
  });

  test('Machine count component is loaded for host-count column', async function(assert) {
    initState({
      endpoint: {
        drivers: {
          fileContext,
          contextLoadingStatus: 'completed'
        }
      }
    });
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{host-detail/utils/file-context-table storeName=storeName tabName=tabName columnsConfig=columnConfig}}`);
    return waitUntil(() => findAll('.rsa-data-table-body-row').length > 0, { timeout: 6000 }).then(() => {
      assert.equal(findAll('.machine-count').length, 3, 'Machine count is displayed, the three items in the table');
    });
  });

  test('insight agent N/A is displayed', async function(assert) {
    initState({
      endpoint: {
        drivers: {
          fileContext,
          contextLoadingStatus: 'completed'
        },
        machines: {
          focusedHost: {
            machineIdentity: {
              agentMode: 'insights'
            }
          }
        }
      }
    });
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{host-detail/utils/file-context-table storeName=storeName tabName=tabName columnsConfig=columnConfig}}`);
    return waitUntil(() => findAll('.rsa-data-table-body-row').length > 0, { timeout: 6000 }).then(() => {
      assert.equal(findAll('.insights-host').length, 3, 'Local risk score is displayed three items in the table');
    });
  });

  test('Sorting is disabled for active on column', async function(assert) {
    assert.expect(1);
    initState({
      endpoint: {
        drivers: {
          fileContext,
          contextLoadingStatus: 'completed'
        }
      }
    });
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{host-detail/utils/file-context-table storeName=storeName tabName=tabName columnsConfig=columnConfig}}`);
    return waitUntil(() => findAll('.rsa-data-table-body-row').length > 0, { timeout: 6000 }).then(async() => {
      assert.equal(findAll('.rsa-data-table-header-cell:nth-child(3) .column-sort').length, 0, 'Sorting is disabled for the column');
    });
  });

  test('It calls the external function when data is loading ', async function(assert) {
    assert.expect(1);
    initState({
      endpoint: {
        drivers: {
          fileContext,
          contextLoadingStatus: 'completed'
        }
      }
    });
    this.set('closePropertyPanel', () => {
      assert.ok(true);
    });
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{host-detail/utils/file-context-table storeName=storeName tabName=tabName columnsConfig=columnConfig closePropertyPanel=closePropertyPanel}}`);
    return waitUntil(() => findAll('.rsa-loader__wheel').length > 0, { timeout: 6000 }).then(() => {
      const redux = this.owner.lookup('service:redux');
      redux.dispatch({ type: ACTION_TYPES.RESET_CONTEXT_DATA, meta: { belongsTo: 'DRIVER' } });
    });
  });

  test('Right clicking already selected row, will keep row highlighted', async function(assert) {
    this.set('closePropertyPanel', () => {});
    this.set('openPropertyPanel', () => {});
    initState({
      endpoint: {
        drivers: {
          fileContext,
          contextLoadingStatus: 'completed'
        }
      }
    });
    await render(hbs`
    <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{host-detail/utils/file-context-table storeName=storeName tabName=tabName columnsConfig=columnConfig closePropertyPanel=closePropertyPanel openPropertyPanel=openPropertyPanel}}{{context-menu}}`);
    await click(findAll('.rsa-data-table-body-row')[1]);
    assert.equal(findAll('.rsa-data-table-body-row.is-row-checked').length, 1, '1 row is selected');
    assert.equal(findAll('.rsa-data-table-body-row.is-selected').length, 1, 'One row highlighted');
    const redux = this.owner.lookup('service:redux');
    const { selectedRowId } = redux.getState().endpoint.drivers;
    assert.equal(selectedRowId, 2, 'Focused host set as first row');
    triggerEvent(findAll('.rsa-data-table-body-row')[1], 'contextmenu', e);
    return settled().then(async() => {
      const newSelectedRowId = redux.getState().endpoint.drivers.selectedRowId;
      assert.equal(newSelectedRowId, 2, 'Focused host remains unchanged');
    });
  });

  test('Right clicking non-highlighted row, will remove highlight from that row', async function(assert) {
    this.set('closePropertyPanel', function() {
      assert.ok('close property panel is called.');
    });
    this.set('openPropertyPanel', () => {});
    initState({
      endpoint: {
        drivers: {
          fileContext,
          contextLoadingStatus: 'completed'
        }
      }
    });
    await render(hbs`
    <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{host-detail/utils/file-context-table storeName=storeName tabName=tabName columnsConfig=columnConfig closePropertyPanel=closePropertyPanel openPropertyPanel=openPropertyPanel}}{{context-menu}}`);

    await click(findAll('.rsa-data-table-body-row')[1]);
    assert.equal(findAll('.rsa-data-table-body-row.is-row-checked').length, 1, 'One row is selected');
    assert.equal(findAll('.rsa-data-table-body-row.is-selected').length, 1, 'One row highlighted');
    const redux = this.owner.lookup('service:redux');
    const { selectedRowId } = redux.getState().endpoint.drivers;
    assert.equal(selectedRowId, 2, 'Focus set on first row');
    triggerEvent(findAll('.machine-count')[2], 'contextmenu', e);
    return settled().then(async() => {
      const newSelectedRowIndex = redux.getState().endpoint.drivers.selectedRowIndex;
      assert.equal(newSelectedRowIndex, null, 'Focus on previous row is removed.');
    });
  });

  test('selecting an already check-boxed row, opens the right panel', async function(assert) {
    this.set('closePropertyPanel', () => {});
    this.set('openPropertyPanel', function() {
      assert.ok('open property panel is called.');
    });
    initState({
      endpoint: {
        drivers: {
          fileContext,
          contextLoadingStatus: 'completed',
          fileContextSelections: [
            {
              checksumMd5: 'test',
              checksumSha1: 'test',
              checksumSha256: 'test',
              fileName: 'test',
              id: 1
            }
          ]
        }
      }
    });
    await render(hbs`
    <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{host-detail/utils/file-context-table storeName=storeName tabName=tabName columnsConfig=columnConfig closePropertyPanel=closePropertyPanel openPropertyPanel=openPropertyPanel}}{{context-menu}}`);
    assert.equal(findAll('.rsa-data-table-body-row.is-selected').length, 0, 'No highlighted rows.');
    await click(findAll('.rsa-data-table-body-row')[1]);
    return settled().then(() => {
      assert.equal(findAll('.rsa-data-table-body-row.is-selected').length, 1, 'Selected row is highlighted');
    });
  });

  test('clicking on a non check-boxed row, will remove checkbox selection from other rows', async function(assert) {
    this.set('closePropertyPanel', () => {});
    this.set('openPropertyPanel', function() {
      assert.ok('open property panel is called.');
    });
    initState({
      endpoint: {
        drivers: {
          fileContext,
          contextLoadingStatus: 'completed',
          fileContextSelections: [
            {
              checksumMd5: 'test',
              checksumSha1: 'test',
              checksumSha256: 'test',
              fileName: 'test',
              id: 1
            },
            {
              id: 2,
              fileName: 'test1',
              timeModified: 12313221,
              fileProperties: {
                checksumSha256: 'test',
                checksumSha1: 'test',
                checksumMd5: 'test',
                signature: {
                  thumbprint: 1
                }
              },
              signature: {
                features: ['microsoft', 'valid']
              }
            }
          ]
        }
      }
    });
    await render(hbs`
    <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{host-detail/utils/file-context-table storeName=storeName tabName=tabName columnsConfig=columnConfig closePropertyPanel=closePropertyPanel openPropertyPanel=openPropertyPanel}}{{context-menu}}`);
    const redux = this.owner.lookup('service:redux');
    await click(findAll('.rsa-data-table-body-row')[0]);
    return settled().then(() => {
      const { fileContextSelections } = redux.getState().endpoint.drivers;
      const { selectedRowId } = redux.getState().endpoint.drivers;
      assert.equal(fileContextSelections.length, 1, 'Checkbox is removed from previous selctions and one row is selected.');
      assert.equal(selectedRowId, 3, 'row with id 3 is focused after the click.');
    });
  });

  test('saveConfig is getting called on re-size', async function(assert) {
    assert.expect(3);
    initState({
      endpoint: {
        drivers: {
          fileContext,
          contextLoadingStatus: 'completed'
        }
      }
    });
    this.set('closePropertyPanel', () => {
      assert.ok(true);
    });
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{host-detail/utils/file-context-table storeName=storeName tabName=tabName closePropertyPanel=closePropertyPanel columnsConfig=columnConfig}}`);
    const [, , draggedItem] = document.querySelectorAll('.rsa-data-table-header-cell-resizer.left');
    let done = false;
    patchSocket((method, modelName) => {
      done = true;
      assert.equal(method, 'getPreferences');
      assert.equal(modelName, 'endpoint-preferences');
    });
    await triggerEvent(draggedItem, 'mousedown', { clientX: draggedItem.offsetLeft, clientY: draggedItem.offsetTop, which: 3 });
    await triggerEvent(draggedItem, 'mousemove', { clientX: draggedItem.offsetLeft - 10, clientY: draggedItem.offsetTop, which: 3 });
    await triggerEvent(draggedItem, 'mouseup', { clientX: 510, clientY: draggedItem.top, which: 3 });

    return waitUntil(() => done, { timeout: 10000 }).then(() => {
      assert.ok(true);
    });
  });
});

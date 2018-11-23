import { module, test, setupRenderingTest } from 'ember-qunit';

import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, findAll, find, render } from '@ember/test-helpers';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';

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
  }
];

let initState;

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
    this.set('storeName', 'drivers');
    this.set('tabName', 'DRIVER');
    this.set('columnConfig', config);
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
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{host-detail/utils/file-context-table storeName=storeName tabName=tabName columnsConfig=columnConfig}}`);
    assert.equal(findAll('.rsa-data-table-body-row').length, 3, 'Returned the number of rows/length of the table');

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
    assert.equal(findAll('.rsa-data-table-body-row:nth-child(1).is-selected').length, 0, 'Row is not selected');
    await click('.rsa-data-table-body-row:nth-child(1)');
    assert.equal(findAll('.rsa-data-table-body-row:nth-child(1).is-selected').length, 1, 'Row is not selected');
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
    assert.equal(findAll('.rsa-data-table-header-cell:nth-child(2) i.rsa-icon-arrow-up-7-filled').length, 1, 'rsa arrow-up icon before sorting');
    await click('.rsa-data-table-header-cell:nth-child(2) .rsa-icon');
    assert.equal(findAll('.rsa-data-table-header-cell:nth-child(2) i.rsa-icon-arrow-down-7-filled').length, 1, 'rsa arrow-down icon after sorting');
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
    assert.ok(find('.file-info').textContent.trim().includes('3 of 3'));
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
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{host-detail/utils/file-context-table showServiceModal=true isPaginated=isPaginated storeName=storeName tabName=tabName columnsConfig=columnConfig}}`);
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
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{host-detail/utils/file-context-table showFileStatusModal=true isPaginated=isPaginated storeName=storeName tabName=tabName columnsConfig=columnConfig}}`);
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
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{host-detail/utils/file-context-table showFileStatusModal=true isPaginated=isPaginated storeName=storeName tabName=tabName columnsConfig=columnConfig}}`);

    assert.equal(findAll('a.file-name-link').length, 3);
    assert.equal(find('a.file-name-link').href.search('/investigate/files/file'), 21);
  });
});

import { module, test, setupRenderingTest } from 'ember-qunit';

import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, find, render } from '@ember/test-helpers';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';

let setState;

module('Integration | Component | file-details/file-analysis-wrapper', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-files')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  const sampleData = [{
    firstFileName: 'XXX Test',
    entropy: 1,
    size: 1024,
    format: 'pe',
    signature: {
      features: 'XXX unsigned',
      thumbprint: '',
      signer: ''
    }
  }];

  test('it should render the file-analysis-wrapper', async function(assert) {
    const fileAnalysis = {
      'fileData': [{
        text: 'OHE3',
        offset: '0x00017d66',
        unicode: false
      },
      {
        text: 'E;uht',
        offset: '0x00017a66',
        unicode: false
      }],
      'filePropertiesData': { format: 'pe' }
    };

    new ReduxDataHelper(setState)
      .activeDataSourceTab('FILE_DETAILS')
      .selectedDetailFile(sampleData)
      .isFilePropertyPanelVisible(true)
      .fileAnalysis(fileAnalysis)
      .build();

    await render(hbs`{{file-details/file-analysis-wrapper}}`);
    assert.equal(findAll('.global-files').length, 1, 'File analysis rendered');
    assert.equal(findAll('.file-analysis-header').length, 1, 'File analysis rendered');
    assert.equal(findAll('.file-analysis-header .view-type').length, 1, 'view type title rendered');
    assert.equal(findAll('.string-view').length, 1, 'String view rendered');
    assert.equal(findAll('.string-filter-wrapper .rsa-form-input').length, 1, 'String filter present');
  });

  test('It should not render the file-analysis-wrapper when filePropertiesData is null', async function(assert) {
    const fileAnalysis = {
      'fileData': null,
      'filePropertiesData': null,
      'isFileAnalysisView': true
    };

    new ReduxDataHelper(setState)
      .activeDataSourceTab('FILE_DETAILS')
      .selectedDetailFile(sampleData)
      .isFilePropertyPanelVisible(true)
      .fileAnalysis(fileAnalysis)
      .build();

    await render(hbs`{{file-details/file-analysis-wrapper}}`);

    assert.equal(findAll('.file-analysis-header').length, 0, 'File analysis not rendered');
    assert.equal(findAll('.file-analysis-header button').length, 0, 'File analysis back button not rendered');
    assert.equal(findAll('.file-analysis-header .view-type').length, 0, 'view type title not rendered');
    assert.equal(findAll('.string-view').length, 0, 'String view not rendered');
    assert.equal(findAll('.text-view').length, 0, 'text view not rendered');
    assert.equal(findAll('.string-filter-wrapper .rsa-form-input').length, 0, 'String filter not rendered');
  });

  test('It should render String search when format is string', async function(assert) {
    const fileAnalysis = {
      'fileData': [{
        text: 'OHE3',
        offset: '0x00017d66',
        unicode: false
      },
      {
        text: 'E;uht',
        offset: '0x00017a66',
        unicode: false
      }],
      'filePropertiesData': { format: 'pe' },
      'isFileAnalysisView': true
    };

    new ReduxDataHelper(setState)
      .activeDataSourceTab('FILE_DETAILS')
      .selectedDetailFile(sampleData)
      .isFilePropertyPanelVisible(true)
      .fileAnalysis(fileAnalysis)
      .build();

    await render(hbs`{{file-details/file-analysis-wrapper}}`);

    assert.equal(findAll('.string-filter-wrapper').length, 1, 'String filter rendered');
  });

  test('Loader should load when the fileDataLoadingStatus status is loading', async function(assert) {
    const fileAnalysis = {
      'fileData': null,
      'filePropertiesData': { format: 'script' },
      'isFileAnalysisView': true,
      'fileDataLoadingStatus': 'loading'
    };

    new ReduxDataHelper(setState)
      .activeDataSourceTab('FILE_DETAILS')
      .selectedDetailFile(sampleData)
      .isFilePropertyPanelVisible(true)
      .fileAnalysis(fileAnalysis)
      .build();

    await render(hbs`{{file-details/file-analysis-wrapper}}`);

    assert.equal(findAll('.rsa-loader').length, 1, 'loader present');
  });

  test('Loader should load when the fileDataLoadingStatus status is completed', async function(assert) {
    const fileAnalysis = {
      'fileData': null,
      'filePropertiesData': { format: 'script' },
      'isFileAnalysisView': true,
      'fileDataLoadingStatus': 'completed'
    };

    new ReduxDataHelper(setState)
      .activeDataSourceTab('FILE_DETAILS')
      .selectedDetailFile(sampleData)
      .isFilePropertyPanelVisible(true)
      .fileAnalysis(fileAnalysis)
      .build();

    await render(hbs`{{file-details/file-analysis-wrapper}}`);

    assert.equal(findAll('.rsa-loader').length, 0, 'loader not present');
  });

  test('Error message should load when the fileDataLoadingStatus status is failed', async function(assert) {
    const fileAnalysis = {
      'fileData': null,
      'filePropertiesData': null,
      'isFileAnalysisView': true,
      'fileDataLoadingStatus': 'failed'
    };

    new ReduxDataHelper(setState)
      .activeDataSourceTab('FILE_DETAILS')
      .selectedDetailFile(sampleData)
      .isFilePropertyPanelVisible(true)
      .fileAnalysis(fileAnalysis)
      .build();

    await render(hbs`{{file-details/file-analysis-wrapper}}`);

    assert.equal(find('.fileNotDownloadedError span').textContent.trim(), 'Download the file to server to analyze.', 'Error message present');
  });

});

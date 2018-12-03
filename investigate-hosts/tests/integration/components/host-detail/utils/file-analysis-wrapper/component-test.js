import { module, test, setupRenderingTest } from 'ember-qunit';

import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render } from '@ember/test-helpers';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';

let setState;

module('Integration | Component | host-detail/utils/file-analysis-wrapper', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });


  test('it should render the file-analysis-wrapper', async function(assert) {
    const fileAnalysis = {
      'fileData': { test: '1' },
      'filePropertiesData': { format: 'pe' },
      'isFileAnalysisView': true
    };

    new ReduxDataHelper(setState)
      .hostDetailsLoading(false)
      .isSnapshotsAvailable(true)
      .selectedTabComponent('FILES')
      .fileAnalysis(fileAnalysis)
      .build();

    await render(hbs`{{host-detail/utils/file-analysis-wrapper}}`);

    assert.equal(findAll('.file-analysis-header').length, 1, 'File analysis rendered');
    assert.equal(findAll('.file-analysis-header button').length, 1, 'File analysis back button rendered');
    assert.equal(findAll('.file-analysis-header .view-type').length, 1, 'view type title rendered');
    assert.equal(findAll('.string-view').length, 1, 'String view rendered');
  });

  test('It should not render the file-analysis-wrapper when filePropertiesData is null', async function(assert) {
    const fileAnalysis = {
      'fileData': null,
      'filePropertiesData': null,
      'isFileAnalysisView': true
    };

    new ReduxDataHelper(setState)
      .hostDetailsLoading(false)
      .isSnapshotsAvailable(true)
      .selectedTabComponent('FILES')
      .fileAnalysis(fileAnalysis)
      .build();

    await render(hbs`{{host-detail/utils/file-analysis-wrapper}}`);

    assert.equal(findAll('.file-analysis-header').length, 0, 'File analysis not rendered');
    assert.equal(findAll('.file-analysis-header button').length, 0, 'File analysis back button not rendered');
    assert.equal(findAll('.file-analysis-header .view-type').length, 0, 'view type title not rendered');
    assert.equal(findAll('.string-view').length, 0, 'String view not rendered');
    assert.equal(findAll('.text-view').length, 0, 'text view not rendered');
  });

});

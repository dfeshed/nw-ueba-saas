import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import Immutable from 'seamless-immutable';

const selectors = {
  summary: '.file-summary',
  scoreField: 'svg.rsa-risk-score',
  summaryFields: '.rsa-content-definition'
};

module('Integration | Component | file-details/header/summary', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-files')
  });
  const state = {
    files: {
      fileList: {
        selectedDetailFile: {
          score: 100,
          size: 14000,
          fileStatus: 'Neutral',
          machineOsType: 'windows',
          hostCount: 3,
          machineCount: 10,
          firstFileName: 'dtf.exe'
        }
      }
    }
  };
  test('it renders', async function(assert) {
    this.owner.inject('component', 'i18n', 'service:i18n');
    patchReducer(this, Immutable.from(state));

    await render(hbs`{{file-details/header/summary}}`);
    assert.equal(findAll(selectors.summaryFields).length, 4, '4 summary fields present');

    // if signature field is not available, should show as 'unsigned'.
    assert.equal(findAll('.rsa-content-definition .value')[1].textContent.trim(), 'unsigned');
  });


  test('host count is available in file summary', async function(assert) {
    this.owner.inject('component', 'i18n', 'service:i18n');
    patchReducer(this, Immutable.from(state));

    await render(hbs`{{file-details/header/summary}}`);
    assert.equal(findAll(selectors.summary).length, 1, 'summary is present');
    assert.equal(findAll('.rsa-content-definition .value')[0].textContent.trim(), '3');

  });
});

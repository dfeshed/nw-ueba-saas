import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | endpoint/file-analysis-properties/file-analysis-accordion-list', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('Accordion list content rendered', async function(assert) {
    const data = {
      value: {
        importedDlls1: [
          '.interp01',
          '.interp02'
        ],
        importedDlls2: [
          '.interp01',
          '.interp02'
        ]
      },
      parentTitle: 'investigateShared.endpoint.fileAnalysis.importedDlls.parentTitle',
      childTitle: 'investigateShared.endpoint.fileAnalysis.importedDlls.childTitle'
    };
    this.set('data', data);
    await render(hbs`{{endpoint/file-analysis-properties/file-analysis-accordion-list data=data}}`);
    assert.equal(findAll('.properties__accordion__item').length, 3);
  });

  test('Accordion list when no data present', async function(assert) {
    const data = {};
    this.set('data', data);
    await render(hbs`{{endpoint/file-analysis-properties/file-analysis-accordion-list data=data}}`);
    assert.equal(findAll('.properties__accordion__item').length, 0);
  });

});

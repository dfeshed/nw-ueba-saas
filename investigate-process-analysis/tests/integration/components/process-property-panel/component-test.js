import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import Immutable from 'seamless-immutable';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import { patchReducer } from '../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let setState;

module('Integration | Component | process-property-panel', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-process-analysis')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  const defaultConfig = [
    {
      sectionName: 'File.General',
      fields: [
        {
          field: 'firstFileName'
        },
        {
          field: 'entropy'
        },
        {
          field: 'size',
          format: 'SIZE'
        },
        {
          field: 'format'
        },
        {
          field: 'checksumSha256'
        }
      ]
    }
  ];

  const processProperties = [
    {
      firstFileName: 'services.exe',
      entropy: 6.462693785416757,
      checksumSha256: 'xyz'
    }
  ];

  test('Process property panel renders', async function(assert) {
    new ReduxDataHelper(setState).processProperties(processProperties).build();
    this.set('defaultConfig', defaultConfig);
    this.set('data', processProperties[0]);
    this.set('currentConfig', { name: 'File.General', isExpanded: false });
    await render(hbs`{{process-property-panel
      title=(t 'investigateProcessAnalysis.property.title')
      data=data
      config=defaultConfig
      localeNameSpace='investigateProcessAnalysis.property.file'
      currentConfig=currentConfig}}`);
    assert.equal(findAll('.content-section__property').length, 0, 'Expected to render 0 file properties when hidden');
    await click('.content-section__section-name');
    assert.equal(findAll('.content-section__property').length, 5, 'Expected to render 5 file properties when shown');
  });

  test('it renders the title for property panel', async function(assert) {
    this.set('title', 'Test Panel');
    await render(hbs`{{process-property-panel  title=title}}`);
    assert.equal(this.element.querySelector('.header-section .header-section__title h2').textContent.trim(), 'Test Panel');
  });


});

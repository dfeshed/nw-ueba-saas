import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | process-property-panel', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-process-analysis')
  });

  hooks.beforeEach(function() {
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
        }
      ]
    }
  ];

  const fileProperties = [
    {
      firstFileName: 'services.exe',
      entropy: 6.462693785416757
    }
  ];

  test('Process property panel renders', async function(assert) {
    this.set('defaultConfig', defaultConfig);
    this.set('data', fileProperties[0]);
    await render(hbs`{{process-property-panel
      title=(t 'investigateProcessAnalysis.property.title')
      data=data
      config=defaultConfig
      localeNameSpace='investigateProcessAnalysis.property.file'}}`);
    assert.equal(findAll('.content-section__property').length, 4, 'Expected to render 4 file properties');
  });

  test('it renders the title for property panel', async function(assert) {
    this.set('title', 'Test Panel');
    await render(hbs`{{process-property-panel  title=title}}`);
    assert.equal(this.element.querySelector('.header-section .header-section__title h2').textContent.trim(), 'Test Panel');
  });


});

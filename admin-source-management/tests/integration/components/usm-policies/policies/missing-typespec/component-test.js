import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | usm-policies/policies/missing-typespec', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  const sourceNoError = [
    {
      fileType: 'apache',
      enabled: false,
      startOfEvents: false,
      fileEncoding: 'UTF-8 / ASCII',
      paths: ['/c/apache_path-hint-1/*.log', '/c/Program Files/Apache Group/Apache[2-9]/*.log', 'apache_path-hint-2'],
      sourceName: 'Meta-Source-Name',
      exclusionFilters: ['exclude-string-1', 'exclude-string-2', 'exclude-string-3']
    }
  ];

  const sourceWithError = [
    {
      fileType: 'apache',
      enabled: false,
      startOfEvents: false,
      fileEncoding: 'UTF-8 / ASCII',
      paths: ['/c/apache_path-hint-1/*.log', '/c/Program Files/Apache Group/Apache[2-9]/*.log', 'apache_path-hint-2'],
      sourceName: 'Meta-Source-Name',
      exclusionFilters: ['exclude-string-1', 'exclude-string-2', 'exclude-string-3'],
      errorState: {
        state: 1,
        errors: ['MISSING_TYPE_SPECIFICATION']
      }
    }
  ];

  test('The component appears in the DOM no errors', async function(assert) {
    this.set('sources', sourceNoError);
    await render(hbs`{{usm-policies/policies/missing-typespec sources=sources}}`);
    assert.equal(findAll('.missing-typespec').length, 1, 'The component appears in the DOM');
    assert.equal(findAll('.missing-typespec i').length, 0, 'NO error appears in the DOM');
  });

  test('The component appears in the DOM with error', async function(assert) {
    this.set('sources', sourceWithError);
    await render(hbs`{{usm-policies/policies/missing-typespec sources=sources}}`);
    assert.equal(findAll('.missing-typespec i').length, 1, 'Error appears in the DOM');
  });
});


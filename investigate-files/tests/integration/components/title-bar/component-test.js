import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

const DATASOURCE_TABS = [
  {
    label: 'investigateFiles.tabs.fileDetails',
    name: 'FILE_DETAILS'
  },
  {
    label: 'investigateFiles.tabs.riskDetails',
    name: 'RISK_PROPERTIES'
  },
  {
    label: 'investigateFiles.tabs.hosts',
    name: 'HOSTS'
  }
];

module('Integration | Component | title-bar', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-files')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('title-bar click sends message', async function(assert) {
    assert.expect(1);
    this.set('tabs', DATASOURCE_TABS);
    this.set('setDataSourceTab', () => {
      assert.ok(true);
    });

    await render(hbs`
      {{#title-bar tabs=tabs defaultAction=setDataSourceTab as |tab|}}
        <div>{{t tab.label}}</div>
      {{/title-bar}}
    `);
    await click(findAll('.rsa-nav-tab')[0]);
  });
});

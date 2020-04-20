import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | process-details/process-property-panel', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-process-analysis')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('Process property panel renders', async function(assert) {
    const processProperties = {
      firstFileName: 'services.exe',
      entropy: 6.462693785416757,
      checksumSha256: 'xyz'
    };
    const config = [
      {
        fields: [
          {
            field: 'firstFileName'
          },
          {
            field: 'entropy'
          },
          {
            field: 'checksumSha256'
          }
        ]
      }
    ];
    this.set('config', config);
    this.set('data', processProperties);
    this.set('hasProperties', true);
    await render(hbs`{{process-details/process-property-panel hasProperties=hasProperties config=config data=data}}`);
    assert.equal(findAll('.content-section__property').length, 3, 'Expected to render 6 file properties when hidden');
  });
});

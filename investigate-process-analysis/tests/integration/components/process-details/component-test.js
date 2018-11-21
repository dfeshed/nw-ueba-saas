import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import Immutable from 'seamless-immutable';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import { patchReducer } from '../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let setState;

module('Integration | Component | process-details', function(hooks) {
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

  const processProperties = [
    {
      firstFileName: 'services.exe',
      entropy: 6.462693785416757,
      checksumSha256: 'xyz'
    }
  ];
  const queryInput = {
    osType: 'windows'
  };

  test('Process details panel renders', async function(assert) {
    new ReduxDataHelper(setState)
      .processProperties(processProperties)
      .queryInput(queryInput)
      .build();
    // Set any properties with this.set('myProperty', 'value');
    // Handle any actions with this.set('myAction', function(val) { ... });

    await render(hbs`{{process-details}}`);
    assert.equal(findAll('.rsa-nav-tab').length, 2, 'Expected to 2 tabs');

  });
});

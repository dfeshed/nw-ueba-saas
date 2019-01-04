import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';

module('Integration | Component | process-details/process-property-panel', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-process-analysis')
  });
  let setState;
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

  test('it renders the property panel', async function(assert) {
    await render(hbs`{{process-details/property-container}}`);
    assert.equal(findAll('.process-property-panel').length, 2, 'Expected to render Two sections');
  });
  test('it renders the empty process execution details panel with message', async function(assert) {
    new ReduxDataHelper(setState)
      .processProperties(processProperties)
      .selectedProcess({
        processId: 1
      })
      .queryInput(queryInput)
      .build();
    await render(hbs`{{process-details/property-container}}`);
    assert.equal(find('.message').textContent.trim(), 'No matching results', 'No matching results message displayed');
  });
  test('process execution details panel should render details', async function(assert) {
    new ReduxDataHelper(setState)
      .processProperties(processProperties)
      .selectedProcess({
        processId: 1,
        checksumDst: 1
      })
      .queryInput(queryInput)
      .build();
    await render(hbs`{{process-details/property-container}}`);
    assert.equal(findAll('.content-section__property').length, 30, 'Expected details render');
  });
});

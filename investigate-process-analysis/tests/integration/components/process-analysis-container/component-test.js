import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import { patchReducer } from '../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';

let setState;

module('Integration | Component | process-analysis-container', function(hooks) {
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

  test('it render the process-analysis component', async function(assert) {
    new ReduxDataHelper(setState).detailsTabSelected('Properties').build();
    await render(hbs`{{process-analysis-container}}`);
    assert.equal(findAll('.process-analysis-container').length, 1, 'Component rendered');
  });

  test('it sets the proper class to component', async function(assert) {
    setState({
      processAnalysis: {
        processVisuals: {
          isProcessDetailsVisible: true,
          detailsTabSelected: {
            name: 'events'
          }
        }
      }
    });
    await render(hbs`{{process-analysis-container}}`);
    assert.equal(findAll('.process-analysis-container.show-process-details').length, 1, 'details visible');
    assert.equal(findAll('.process-analysis-container.hide-process-details').length, 0);
  });

  test('it sets the proper class to component', async function(assert) {
    setState({
      processAnalysis: {
        processVisuals: {
          isProcessDetailsVisible: false,
          detailsTabSelected: {
            name: 'events'
          }
        }
      }
    });
    await render(hbs`{{process-analysis-container}}`);
    assert.equal(findAll('.process-analysis-container.show-process-details').length, 0);
    assert.equal(findAll('.process-analysis-container.hide-process-details').length, 1, 'details hidden');
  });

});

import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, waitUntil, find, click } from '@ember/test-helpers';
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

  test('Events table is rendered with multiple filenameSrc field', async function(assert) {
    const queryInputs = {
      sid: '1',
      vid: '3',
      pn: 'test',
      st: 1231233,
      et: 13123,
      osType: 'windows',
      checksum: '07d15ddf2eb7be486d01bcabab7ad8df35b7942f25f5261e3c92cd7a8931190a',
      aid: '51687D32-BB0F-A424-1D64-A8B94C957BD2'
    };
    setState({
      processAnalysis: {
        processVisuals: {
          isProcessInfoVisible: true,
          detailsTabSelected: {
            name: 'events'
          }
        },
        processTree: {
          queryInput: queryInputs,
          error: null,
          path: [ '0' ]
        }
      }
    });
    const timezone = this.owner.lookup('service:timezone');
    const timeFormat = this.owner.lookup('service:timeFormat');
    const dateFormat = this.owner.lookup('service:dateFormat');

    timezone.set('_selected', { zoneId: 'UTC' });
    timeFormat.set('_selected', { format: 'hh:mm:ss' });
    dateFormat.set('_selected', { format: 'YYYY-MM-DD' });

    await render(hbs`{{process-analysis-container}}`);
    await waitUntil(() => find('.rsa-nav-tab.is-active .label').textContent.trim() === 'Events (4)', { timeout: Infinity });
    await click('.rsa-nav-tab.is-active .label');

    assert.equal(findAll('.rsa-data-table-body-rows').length, 1, 'table row is displayed');
    assert.equal(find('.rsa-data-table-body-cell.filenameSrc').textContent.trim(), 'Root,cmd.exe', 'multiple filename is displayed');
    assert.equal(findAll('.process-analysis-container').length, 1, 'properties panel appearing');
  });
});

import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { patchReducer } from '../../../../../helpers/vnext-patch';

import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { eventsData } from './data';
import { selectors } from '../../../../../integration/components/events-list/selectors';
import Immutable from 'seamless-immutable';

module('Integration | Component | file-details/overview/events-list-container', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-files')
  });

  test('renders endpoint row templates', async function(assert) {
    const state = {
      files: {
        fileDetail: {
          eventsData,
          expandedId: null,
          eventsLoadingStatus: 'completed'
        }
      }
    };
    patchReducer(this, Immutable.from(state));
    await render(hbs`{{file-details/overview/events-list-container}}`);

    assert.equal(findAll(selectors.list).length, 1);
    assert.equal(findAll(selectors.row).length, 2);
    assert.equal(findAll(selectors.endpointHeader).length, 2);
    assert.equal(findAll(selectors.loader).length, 0);
    assert.equal(find(selectors.count).textContent.trim(), '2');
    assert.equal(find(selectors.label).textContent.trim(), 'events');
  });

  test('onclick the table row main will expand the event showing any details for the given type', async function(assert) {
    const state = {
      files: {
        fileDetail: {
          eventsData,
          expandedId: null,
          eventsLoadingStatus: 'completed'
        }
      }
    };
    patchReducer(this, Immutable.from(state));
    await render(hbs`{{file-details/overview/events-list-container}}`);
    assert.equal(findAll(selectors.row).length, 2);
    assert.equal(findAll(selectors.endpointDetail).length, 0);

    await click(`${selectors.row}:nth-of-type(1) ${selectors.endpointHeader}`);
    assert.equal(findAll(selectors.endpointDetail).length, 1);

    await click(`${selectors.row}:nth-of-type(1) ${selectors.endpointHeader}`);
    assert.equal(findAll(selectors.endpointDetail).length, 0);
  });

  test('loading spinner present when storyline event status not completed', async function(assert) {
    const state = {
      files: {
        fileDetail: {
          eventsData,
          expandedId: null,
          eventsLoadingStatus: 'loading'
        }
      }
    };
    patchReducer(this, Immutable.from(state));
    await render(hbs`{{file-details/overview/events-list-container}}`);

    assert.equal(findAll(selectors.row).length, 2);
    assert.equal(findAll(selectors.loader).length, 1);
  });
});

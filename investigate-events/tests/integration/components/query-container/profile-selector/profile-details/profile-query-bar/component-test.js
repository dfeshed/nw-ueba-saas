import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { findAll, render } from '@ember/test-helpers';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';

import PILL_SELECTORS from '../../../pill-selectors';
import { DEFAULT_PILLS_DATA } from '../../../../../../helpers/redux-data-helper';

module('Integration | Component | Profile query bar', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('it renders profile query bar', async function(assert) {
    await render(hbs`{{query-container/profile-selector/profile-details/profile-query-bar}}`);
    assert.equal(findAll(PILL_SELECTORS.allPills).length, 1, 'There should only be one query-pill.');
  });

  test('it renders pills when there are some', async function(assert) {
    patchReducer(this, Immutable.from({
      investigate: {
        queryNode: {
          pillsData: DEFAULT_PILLS_DATA,
          metaFilter: [],
          previouslySelectedTimeRanges: {},
          serviceId: '1',
          queryView: 'guided'
        }
      },
      listManagers: {
        profiles: {
          isExpanded: true,
          viewName: 'edit-view'
        }
      }
    }));
    await render(hbs`{{query-container/profile-selector/profile-details/profile-query-bar}}`);
    assert.equal(findAll(PILL_SELECTORS.queryPillNotTemplate).length, 2, 'Did not find DEFAULT pills rendered.');
  });
});

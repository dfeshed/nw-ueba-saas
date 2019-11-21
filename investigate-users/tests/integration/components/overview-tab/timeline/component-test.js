import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../helpers/vnext-patch';
import alertByDayAndSeverity from '../../../../data/presidio/alert-by-day-and-severity';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Service from '@ember/service';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';

let setState;

const routerStub = Service.extend({
  transitionTo: (route) => {
    return route;
  }
});

module('Integration | Component | overview-tab/timeline', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-users')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    this.owner.register('service:-routing', routerStub);
  });

  test('it renders', async function(assert) {
    await render(hbs`{{overview-tab/timeline}}`);
    assert.equal(findAll('.user-overview-tab_alerts_timeline').length, 1);
    assert.equal(find('.center').textContent.trim(), 'Loading', 'Should show loading till data is not there');
  });

  test('it renders with timeline data', async function(assert) {
    new ReduxDataHelper(setState).alertTimeLine(alertByDayAndSeverity).build();
    await render(hbs`{{overview-tab/timeline}}`);
    const svgItem = find('svg');
    assert.equal(svgItem.childNodes.length, 3);
  });

  test('it renders loader till time line data is not there', async function(assert) {
    new ReduxDataHelper(setState).alertTimeLine([]).build();
    await render(hbs`{{overview-tab/timeline}}`);
    assert.equal(findAll('.rsa-loader').length, 1);
    assert.equal(findAll('.center').length, 1);
  });

  test('it renders error for any server issue', async function(assert) {
    new ReduxDataHelper(setState).alertTimeLine([]).alertsForTimelineError('Error').build();
    await render(hbs`{{overview-tab/timeline}}`);
    assert.equal(findAll('.rsa-loader').length, 0);
    assert.equal(findAll('.center').length, 1);
  });
});

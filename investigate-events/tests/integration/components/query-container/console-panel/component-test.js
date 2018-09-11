import moment from 'moment';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import hbs from 'htmlbars-inline-precompile';
import { find, findAll, render } from '@ember/test-helpers';
import { patchReducer } from '../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';

let setState;

module('Integration | Component | Console Panel', function(hooks) {

  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  test('renders the correct dom and data', async function(assert) {
    this.set('timeFormat', {
      selected: {
        format: 'hh:mma'
      }
    });

    this.set('dateFormat', {
      selected: {
        format: 'MM/DD/YYYY'
      }
    });

    const sDate = Date.now();
    const eDate = Date.now();
    const format = `${this.get('dateFormat.selected.format')} ${this.get('timeFormat.selected.format')}`;

    new ReduxDataHelper(setState).serviceId().startTime(sDate).endTime(eDate).pillsDataPopulated().queryStats().queryStatsIsOpen().build();

    await render(hbs`
      {{query-container/console-panel timeFormat=timeFormat dateFormat=dateFormat}}
    `);

    assert.equal(findAll('.console-panel .warnings .warning').length, 0);
    assert.equal(findAll('.console-panel .console-content').length, 1);
    assert.equal(findAll('.console-panel .console-content .service').length, 1);
    assert.equal(findAll('.console-panel .console-content .timerange').length, 1);
    assert.equal(find('.console-panel .console-content .timerange .value').textContent, `${moment(sDate).format(format)} - ${moment(eDate).format(format)}`);
    assert.equal(find('.console-panel .console-content .progress .value').textContent.trim(), 'foo');
  });

  test('renders the correct dom hasWarning', async function(assert) {
    new ReduxDataHelper(setState).queryStats().queryStatsHasWarning().build();
    await render(hbs`
      {{query-container/console-panel}}
    `);
    assert.equal(findAll('.console-panel.has-warning .console-content').length, 1);
    assert.equal(find('.console-panel .console-content .progress .value').textContent.trim(), 'warning');
  });

  test('renders the correct dom hasError', async function(assert) {
    new ReduxDataHelper(setState).queryStats().queryStatsHasError().build();
    await render(hbs`
      {{query-container/console-panel}}
    `);
    assert.equal(findAll('.console-panel.has-error .console-content').length, 1);
    assert.equal(find('.console-panel .console-content .progress .value').textContent.trim(), 'error');
  });

  test('renders warnings', async function(assert) {
    new ReduxDataHelper(setState).hasRequiredValuesToQuery(true).queryStats().queryStatsHasWarning().build();
    await render(hbs`
      {{query-container/console-panel}}
    `);
    assert.equal(findAll('.console-panel .warnings .warning').length, 1);
    assert.equal(find('.console-panel .console-content .progress .value').textContent.trim(), 'warning');
  });

  test('renders progress-bar', async function(assert) {
    new ReduxDataHelper(setState).queryStats().queryStatsIsOpen().build();
    await render(hbs`
      {{query-container/console-panel}}
    `);
    assert.equal(findAll('.console-panel .progress-bar').length, 1);
  });

  test('does not renders progress-bar when complete', async function(assert) {
    new ReduxDataHelper(setState).queryStats().queryStatsIsOpen().queryStatsIsComplete().build();
    await render(hbs`
      {{query-container/console-panel}}
    `);
    assert.equal(findAll('.console-panel .progress-bar').length, 0);
  });

});

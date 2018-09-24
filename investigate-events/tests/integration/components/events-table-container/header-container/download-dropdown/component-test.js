import { moduleForComponent, test } from 'ember-qunit';
import { applyPatch, revertPatch } from '../../../../../helpers/patch-reducer';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import hbs from 'htmlbars-inline-precompile';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

let setState;

moduleForComponent('events-table-container/header-container/download-dropdown', 'Integration | Component | download dropdown', {
  integration: true,
  resolver: engineResolverFor('investigate-events'),
  beforeEach() {
    initialize({ '__container__': this.container });
    this.inject.service('accessControl');
    this.set('accessControl.hasInvestigateContentExportAccess', true);
    setState = (state) => {
      applyPatch(state);
      this.inject.service('redux');
    };

  },
  afterEach() {
    revertPatch();
  }
});

const downloadSelector = '.rsa-investigate-events-table__header__downloadEvents';
const downloadTitle = '.rsa-investigate-events-table__header__downloadEvents span';

test('download option should be visible if user has permissions', function(assert) {
  new ReduxDataHelper(setState).allEventsSelected(false).withSelectedEventIds().build();
  this.render(hbs`{{events-table-container/header-container/download-dropdown}}`);
  assert.equal(this.$(downloadSelector)[0].childElementCount, 1, 'Download option present');
});

test('download dropdown should be hidden if missing permissions', function(assert) {
  this.set('accessControl.hasInvestigateContentExportAccess', false);
  new ReduxDataHelper(setState).allEventsSelected(false).withSelectedEventIds().build();
  this.render(hbs`{{events-table-container/header-container/download-dropdown}}`);
  assert.equal(this.$(downloadSelector)[0].childElementCount, 0, 'Download option not present');
});

test('download dropdown should read Download All if selectAll is checked', function(assert) {
  new ReduxDataHelper(setState).allEventsSelected(true).build();
  this.render(hbs`{{events-table-container/header-container/download-dropdown}}`);
  assert.equal(this.$(downloadTitle)[0].textContent.trim(), 'Download All', 'Download dropdown should read `Download All` if selectAll is checked');
});

test('download dropdown should read Download if selectAll is not checked', function(assert) {
  new ReduxDataHelper(setState).allEventsSelected(false).withSelectedEventIds().build();
  this.render(hbs`{{events-table-container/header-container/download-dropdown}}`);
  assert.equal(this.$(downloadTitle)[0].textContent.trim(), 'Download', 'Download dropdown should read `Download` if selectAll is not checked');
});

test('download dropdown should be enabled if all or 1+ events are selected ', function(assert) {
  new ReduxDataHelper(setState).allEventsSelected(false).withSelectedEventIds().build();
  this.render(hbs`{{events-table-container/header-container/download-dropdown}}`);
  assert.notOk(this.$(downloadSelector)[0].classList.contains('is-disabled'), 'Download is enabled');
});

test('download dropdown should be disabled if no events are selected ', function(assert) {
  new ReduxDataHelper(setState).allEventsSelected(false).build();
  this.render(hbs`{{events-table-container/header-container/download-dropdown}}`);
  assert.ok(this.$(downloadSelector)[0].classList.contains('is-disabled'), 'Download is disabled');
});

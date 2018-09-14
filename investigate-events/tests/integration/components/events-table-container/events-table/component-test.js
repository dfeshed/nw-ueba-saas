import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import EventColumnGroups from '../../../../data/subscriptions/investigate-columns/data';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { applyPatch, revertPatch } from '../../../../helpers/patch-reducer';

let setState;

moduleForComponent('events-table-container/events-table', 'Integration | Component | events table context menu', {
  integration: true,
  resolver: engineResolverFor('investigate-events'),
  beforeEach() {
    initialize({ '__container__': this.container });
    this.inject.service('accessControl');

    setState = (state) => {
      applyPatch(state);
      this.inject.service('redux');
    };

  },
  afterEach() {
    revertPatch();
  }
});

test('it renders', function(assert) {
  this.render(hbs`{{events-table-container/events-table contextItems=contextItems}}`);
  assert.equal(this.$('.content-context-menu').length, 1, 'Context menu trigger rendered');

});

test('it shows context menu on right click', function(assert) {
  this.render(hbs`{{events-table-container/events-table metaName=metaName metaValue=metaValue}}`);
  this.$('.js-move-handle:first').attr({ 'metaname': 'ip.src', 'metavalue': '1.1.1.1' });

  this.$('.js-move-handle:first').trigger({
    type: 'contextmenu',
    clientX: 100,
    clientY: 100
  });

  assert.equal(this.get('metaName'), 'ip.src', 'meta name extracted from event and set');
  assert.equal(this.get('metaValue'), '1.1.1.1', 'meta value extracted from event and set');

});

test('context menu is deactivated on right clicking outside the target', function(assert) {
  assert.expect(1);

  const contextMenuService = {
    isActive: true,
    deactivate: () => {
      assert.ok(true, 'deactivate called');
    }
  };
  this.set('contextMenuService', contextMenuService);
  this.render(hbs`{{events-table-container/events-table metaName=metaName metaValue=metaValue contextMenuService=contextMenuService}}`);

  this.$('.js-move-handle:first').attr({ 'metaName': 'ip.src', 'metaValue': '1.1.1.1' });
  this.$('.js-move-handle:first').trigger({
    type: 'contextmenu',
    clientX: 100,
    clientY: 100
  });

  // rt-click elsewhere
  this.$('.rsa-data-table-header').contextmenu();
});

test('renders event selection checkboxes only if permissions are present', function(assert) {

  this.set('accessControl.hasInvestigateContentExportAccess', true);
  new ReduxDataHelper(setState).getColumns('SUMMARY', EventColumnGroups).eventResults([]).build();
  this.render(hbs`{{events-table-container/events-table}}`);
  assert.equal(this.$('.rsa-form-checkbox-label').length, 1, 'Renders event selection checkboxes when permission is present');

});

test('does not render event selection checkboxes if permissions are not present', function(assert) {

  this.set('accessControl.hasInvestigateContentExportAccess', false);
  new ReduxDataHelper(setState).getColumns('SUMMARY', EventColumnGroups).eventResults([]).build();
  this.render(hbs`{{events-table-container/events-table}}`);
  assert.equal(this.$('.rsa-form-checkbox-label').length, 0, 'Does not render event selection checkboxes when permission is not present');

});

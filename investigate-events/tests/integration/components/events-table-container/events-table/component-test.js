import { module, test, skip } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import EventColumnGroups from '../../../../data/subscriptions/investigate-columns/data';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { find, findAll, render } from '@ember/test-helpers';

let setState;

module('Integration | Component | Events Table', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  test('it renders with Context menu trigger', async function(assert) {
    await render(hbs`
      {{events-table-container/events-table
        contextItems=contextItems
      }}
    `);
    assert.equal(findAll('.content-context-menu').length, 1, 'Context menu trigger rendered');
  });

  test('it shows context menu on right click', async function(assert) {
    await render(hbs`
      {{events-table-container/events-table
        metaName=metaName
        metaValue=metaValue
      }}
    `);
    await find('.js-move-handle').setAttribute('metaname', 'ip.src');
    await find('.js-move-handle').setAttribute('metavalue', '1.1.1.1');
    this.$('.js-move-handle:first').trigger({
      type: 'contextmenu',
      clientX: 100,
      clientY: 100
    });

    assert.equal(this.get('metaName'), 'ip.src', 'meta name extracted from event and set');
    assert.equal(this.get('metaValue'), '1.1.1.1', 'meta value extracted from event and set');

  });

  test('context menu is deactivated on right clicking outside the target', async function(assert) {
    assert.expect(0);
    const done = assert.async();
    const contextMenuService = {
      isActive: true,
      deactivate: () => {
        done();
      }
    };
    this.set('contextMenuService', contextMenuService);

    await render(hbs`
      {{events-table-container/events-table
        metaName=metaName
        metaValue=metaValue
        contextMenuService=contextMenuService
      }}
    `);

    await find('.js-move-handle').setAttribute('metaname', 'ip.src');
    await find('.js-move-handle').setAttribute('metavalue', '1.1.1.1');
    this.$('.js-move-handle:first').trigger({
      type: 'contextmenu',
      clientX: 100,
      clientY: 100
    });

    // rt-click elsewhere
    this.$('.rsa-data-table-header').contextmenu();
  });

  // TODO bring download back
  skip('renders event selection checkboxes only if permissions are present', async function(assert) {
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('hasInvestigateContentExportAccess', true);
    new ReduxDataHelper(setState)
      .getColumns('SUMMARY', EventColumnGroups)
      .eventResults([])
      .build();

    await render(hbs`{{events-table-container/events-table}}`);

    assert.equal(findAll('.rsa-form-checkbox-label').length, 1, 'Renders event selection checkboxes when permission is present');
  });

  skip('does not render event selection checkboxes if permissions are not present', async function(assert) {
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('hasInvestigateContentExportAccess', false);
    new ReduxDataHelper(setState)
      .getColumns('SUMMARY', EventColumnGroups)
      .eventResults([])
      .build();

    await render(hbs`{{events-table-container/events-table}}`);

    assert.equal(findAll('.rsa-form-checkbox-label').length, 0, 'Does not render event selection checkboxes when permission is not present');
  });
});

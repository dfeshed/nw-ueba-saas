import * as DATA from './data';
import { module, test } from 'qunit';
import Service from '@ember/service';
import { computed } from '@ember/object';
import Immutable from 'seamless-immutable';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { setupRenderingTest } from 'ember-qunit';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { click, render, findAll, find } from '@ember/test-helpers';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

let setState, hasPermission;

const alertsSelector = '[test-id=alertsTableSection]';
const eventAnalysisSelector = '[test-id=alertsTableReconVisualCue]';
const toggleEventsSelector = '[test-id=alertsTableToggleEvents]';
const toggleEnrichmentsSelector = '[test-id=alertsTableToggleEnrichments]';
const groupsSelector = '[test-id=groupTableItem]';
const alertsHeaderSelector = '[test-id=alertsTableHeader]';
const reconLinkSelector = '[test-id=respondReconLink]';

module('Integration | Component | rsa-incident/container', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
    hasPermission = true;
    initialize(this.owner);
    setState = (state) => {
      patchReducer(this, state);
    };
    this.owner.inject('component', 'i18n', 'service:i18n');
    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'incident',
      generateURL: () => {
        return;
      },
      transitionTo: () => {
      }
    }));
    this.owner.register('service:accessControl', Service.extend({
      hasReconAccess: computed(function() {
        return hasPermission;
      })
    }));
  });

  test('storyline with enrichments and events will pre select event when incident selection type event', async function(assert) {
    setState({
      respond: {
        recon: Immutable.from(DATA.generateRecon()),
        incident: Immutable.from(DATA.generateIncident({ withSelection: true })),
        storyline: DATA.generateStoryline({ withEnrichment: true })
      }
    });

    await render(hbs`{{rsa-incident/container}}`);

    assert.equal(findAll(alertsSelector).length, 8);

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector}`).length, 1);
    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${toggleEnrichmentsSelector}`).length, 1);
    assert.notOk(find(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector}`).classList.contains('active'));
    assert.ok(find(`${alertsSelector}:nth-of-type(2) ${toggleEnrichmentsSelector}`).classList.contains('active'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${groupsSelector}`).length, 1);
    assert.notOk(find(`${alertsSelector}:nth-of-type(2) ${groupsSelector}:nth-of-type(1)`).classList.contains('is-selected'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${alertsHeaderSelector}`).length, 1);
    assert.ok(find(`${alertsSelector}:nth-of-type(2) ${alertsHeaderSelector}`).classList.contains('is-open'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(3) ${toggleEventsSelector}`).length, 1);
    assert.equal(findAll(`${alertsSelector}:nth-of-type(3) ${toggleEnrichmentsSelector}`).length, 1);
    assert.ok(find(`${alertsSelector}:nth-of-type(3) ${toggleEventsSelector}`).classList.contains('active'));
    assert.notOk(find(`${alertsSelector}:nth-of-type(3) ${toggleEnrichmentsSelector}`).classList.contains('active'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(3) ${groupsSelector}`).length, 1);
    assert.ok(find(`${alertsSelector}:nth-of-type(3) ${groupsSelector}:nth-of-type(1)`).classList.contains('is-selected'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${alertsHeaderSelector}`).length, 1);
    assert.ok(find(`${alertsSelector}:nth-of-type(2) ${alertsHeaderSelector}`).classList.contains('is-open'));

    await click(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector}`);

    assert.equal(findAll(alertsSelector).length, 8);

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector}`).length, 1);
    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${toggleEnrichmentsSelector}`).length, 1);
    assert.ok(find(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector}`).classList.contains('active'));
    assert.notOk(find(`${alertsSelector}:nth-of-type(2) ${toggleEnrichmentsSelector}`).classList.contains('active'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${groupsSelector}`).length, 1);
    assert.notOk(find(`${alertsSelector}:nth-of-type(2) ${groupsSelector}:nth-of-type(1)`).classList.contains('is-selected'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${alertsHeaderSelector}`).length, 1);
    assert.ok(find(`${alertsSelector}:nth-of-type(2) ${alertsHeaderSelector}`).classList.contains('is-open'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(3) ${toggleEventsSelector}`).length, 1);
    assert.equal(findAll(`${alertsSelector}:nth-of-type(3) ${toggleEnrichmentsSelector}`).length, 1);
    assert.ok(find(`${alertsSelector}:nth-of-type(3) ${toggleEventsSelector}`).classList.contains('active'));
    assert.notOk(find(`${alertsSelector}:nth-of-type(3) ${toggleEnrichmentsSelector}`).classList.contains('active'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(3) ${groupsSelector}`).length, 1);
    assert.ok(find(`${alertsSelector}:nth-of-type(3) ${groupsSelector}:nth-of-type(1)`).classList.contains('is-selected'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${alertsHeaderSelector}`).length, 1);
    assert.ok(find(`${alertsSelector}:nth-of-type(2) ${alertsHeaderSelector}`).classList.contains('is-open'));

    await click(`${alertsSelector}:nth-of-type(2) ${reconLinkSelector}:nth-of-type(1) .recon-link-to`);

    assert.equal(findAll(alertsSelector).length, 8);

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector}`).length, 1);
    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${toggleEnrichmentsSelector}`).length, 1);
    assert.ok(find(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector}`).classList.contains('active'));
    assert.notOk(find(`${alertsSelector}:nth-of-type(2) ${toggleEnrichmentsSelector}`).classList.contains('active'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${groupsSelector}`).length, 1);
    assert.ok(find(`${alertsSelector}:nth-of-type(2) ${groupsSelector}:nth-of-type(1)`).classList.contains('is-selected'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${alertsHeaderSelector}`).length, 1);
    assert.ok(find(`${alertsSelector}:nth-of-type(2) ${alertsHeaderSelector}`).classList.contains('is-open'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(3) ${toggleEventsSelector}`).length, 1);
    assert.equal(findAll(`${alertsSelector}:nth-of-type(3) ${toggleEnrichmentsSelector}`).length, 1);
    assert.ok(find(`${alertsSelector}:nth-of-type(3) ${toggleEventsSelector}`).classList.contains('active'));
    assert.notOk(find(`${alertsSelector}:nth-of-type(3) ${toggleEnrichmentsSelector}`).classList.contains('active'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(3) ${groupsSelector}`).length, 1);
    assert.notOk(find(`${alertsSelector}:nth-of-type(3) ${groupsSelector}:nth-of-type(1)`).classList.contains('is-selected'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${alertsHeaderSelector}`).length, 1);
    assert.ok(find(`${alertsSelector}:nth-of-type(2) ${alertsHeaderSelector}`).classList.contains('is-open'));
  });

  test('storyline with only events will pre select event when incident selection type event', async function(assert) {
    setState({
      respond: {
        recon: Immutable.from(DATA.generateRecon()),
        incident: Immutable.from(DATA.generateIncident({ withSelection: true })),
        storyline: DATA.generateStoryline({ withEnrichment: false })
      }
    });

    await render(hbs`{{rsa-incident/container}}`);

    assert.equal(findAll(alertsSelector).length, 8);

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector}`).length, 1);
    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${toggleEnrichmentsSelector}`).length, 0);
    assert.notOk(find(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector}`).classList.contains('active'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${groupsSelector}`).length, 0);

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${alertsHeaderSelector}`).length, 1);
    assert.notOk(find(`${alertsSelector}:nth-of-type(2) ${alertsHeaderSelector}`).classList.contains('is-open'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(3) ${toggleEventsSelector}`).length, 1);
    assert.equal(findAll(`${alertsSelector}:nth-of-type(3) ${toggleEnrichmentsSelector}`).length, 0);
    assert.ok(find(`${alertsSelector}:nth-of-type(3) ${toggleEventsSelector}`).classList.contains('active'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(3) ${groupsSelector}`).length, 1);
    assert.ok(find(`${alertsSelector}:nth-of-type(3) ${groupsSelector}:nth-of-type(1)`).classList.contains('is-selected'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${alertsHeaderSelector}`).length, 1);
    assert.notOk(find(`${alertsSelector}:nth-of-type(2) ${alertsHeaderSelector}`).classList.contains('is-open'));

    await click(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector}`);

    assert.equal(findAll(alertsSelector).length, 8);

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector}`).length, 1);
    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${toggleEnrichmentsSelector}`).length, 0);
    assert.ok(find(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector}`).classList.contains('active'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${groupsSelector}`).length, 1);
    assert.notOk(find(`${alertsSelector}:nth-of-type(2) ${groupsSelector}:nth-of-type(1)`).classList.contains('is-selected'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${alertsHeaderSelector}`).length, 1);
    assert.ok(find(`${alertsSelector}:nth-of-type(2) ${alertsHeaderSelector}`).classList.contains('is-open'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(3) ${toggleEventsSelector}`).length, 1);
    assert.equal(findAll(`${alertsSelector}:nth-of-type(3) ${toggleEnrichmentsSelector}`).length, 0);
    assert.ok(find(`${alertsSelector}:nth-of-type(3) ${toggleEventsSelector}`).classList.contains('active'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(3) ${groupsSelector}`).length, 1);
    assert.ok(find(`${alertsSelector}:nth-of-type(3) ${groupsSelector}:nth-of-type(1)`).classList.contains('is-selected'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${alertsHeaderSelector}`).length, 1);
    assert.ok(find(`${alertsSelector}:nth-of-type(2) ${alertsHeaderSelector}`).classList.contains('is-open'));

    await click(`${alertsSelector}:nth-of-type(2) ${reconLinkSelector}:nth-of-type(1) .recon-link-to`);

    assert.equal(findAll(alertsSelector).length, 8);

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector}`).length, 1);
    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${toggleEnrichmentsSelector}`).length, 0);
    assert.ok(find(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector}`).classList.contains('active'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${groupsSelector}`).length, 1);
    assert.ok(find(`${alertsSelector}:nth-of-type(2) ${groupsSelector}:nth-of-type(1)`).classList.contains('is-selected'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${alertsHeaderSelector}`).length, 1);
    assert.ok(find(`${alertsSelector}:nth-of-type(2) ${alertsHeaderSelector}`).classList.contains('is-open'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(3) ${toggleEventsSelector}`).length, 1);
    assert.equal(findAll(`${alertsSelector}:nth-of-type(3) ${toggleEnrichmentsSelector}`).length, 0);
    assert.ok(find(`${alertsSelector}:nth-of-type(3) ${toggleEventsSelector}`).classList.contains('active'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(3) ${groupsSelector}`).length, 1);
    assert.notOk(find(`${alertsSelector}:nth-of-type(3) ${groupsSelector}:nth-of-type(1)`).classList.contains('is-selected'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${alertsHeaderSelector}`).length, 1);
    assert.ok(find(`${alertsSelector}:nth-of-type(2) ${alertsHeaderSelector}`).classList.contains('is-open'));
  });

  test('storyline with only events will not pre select event when no incident selected', async function(assert) {
    setState({
      respond: {
        recon: Immutable.from(DATA.generateRecon()),
        incident: Immutable.from(DATA.generateIncident({ withSelection: false })),
        storyline: DATA.generateStoryline({ withEnrichment: false })
      }
    });

    await render(hbs`{{rsa-incident/container}}`);

    assert.equal(findAll(alertsSelector).length, 8);

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector}`).length, 1);
    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${toggleEnrichmentsSelector}`).length, 0);
    assert.notOk(find(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector}`).classList.contains('active'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${groupsSelector}`).length, 0);

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${alertsHeaderSelector}`).length, 1);
    assert.notOk(find(`${alertsSelector}:nth-of-type(2) ${alertsHeaderSelector}`).classList.contains('is-open'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(3) ${toggleEventsSelector}`).length, 1);
    assert.equal(findAll(`${alertsSelector}:nth-of-type(3) ${toggleEnrichmentsSelector}`).length, 0);
    assert.notOk(find(`${alertsSelector}:nth-of-type(3) ${toggleEventsSelector}`).classList.contains('active'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(3) ${groupsSelector}`).length, 0);

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${alertsHeaderSelector}`).length, 1);
    assert.notOk(find(`${alertsSelector}:nth-of-type(2) ${alertsHeaderSelector}`).classList.contains('is-open'));

    await click(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector}`);

    assert.equal(findAll(alertsSelector).length, 8);

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector}`).length, 1);
    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${toggleEnrichmentsSelector}`).length, 0);
    assert.ok(find(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector}`).classList.contains('active'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${groupsSelector}`).length, 1);
    assert.notOk(find(`${alertsSelector}:nth-of-type(2) ${groupsSelector}:nth-of-type(1)`).classList.contains('is-selected'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${alertsHeaderSelector}`).length, 1);
    assert.ok(find(`${alertsSelector}:nth-of-type(2) ${alertsHeaderSelector}`).classList.contains('is-open'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(3) ${toggleEventsSelector}`).length, 1);
    assert.equal(findAll(`${alertsSelector}:nth-of-type(3) ${toggleEnrichmentsSelector}`).length, 0);
    assert.notOk(find(`${alertsSelector}:nth-of-type(3) ${toggleEventsSelector}`).classList.contains('active'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(3) ${groupsSelector}`).length, 0);

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${alertsHeaderSelector}`).length, 1);
    assert.ok(find(`${alertsSelector}:nth-of-type(2) ${alertsHeaderSelector}`).classList.contains('is-open'));

    await click(`${alertsSelector}:nth-of-type(3) ${toggleEventsSelector}`);

    assert.equal(findAll(alertsSelector).length, 8);

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector}`).length, 1);
    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${toggleEnrichmentsSelector}`).length, 0);
    assert.ok(find(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector}`).classList.contains('active'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${groupsSelector}`).length, 1);
    assert.notOk(find(`${alertsSelector}:nth-of-type(2) ${groupsSelector}:nth-of-type(1)`).classList.contains('is-selected'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${alertsHeaderSelector}`).length, 1);
    assert.ok(find(`${alertsSelector}:nth-of-type(2) ${alertsHeaderSelector}`).classList.contains('is-open'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(3) ${toggleEventsSelector}`).length, 1);
    assert.equal(findAll(`${alertsSelector}:nth-of-type(3) ${toggleEnrichmentsSelector}`).length, 0);
    assert.ok(find(`${alertsSelector}:nth-of-type(3) ${toggleEventsSelector}`).classList.contains('active'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(3) ${groupsSelector}`).length, 1);
    assert.notOk(find(`${alertsSelector}:nth-of-type(3) ${groupsSelector}:nth-of-type(1)`).classList.contains('is-selected'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${alertsHeaderSelector}`).length, 1);
    assert.ok(find(`${alertsSelector}:nth-of-type(2) ${alertsHeaderSelector}`).classList.contains('is-open'));

    await click(`${alertsSelector}:nth-of-type(3) ${reconLinkSelector}:nth-of-type(1) .recon-link-to`);

    assert.equal(findAll(alertsSelector).length, 8);

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector}`).length, 1);
    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${toggleEnrichmentsSelector}`).length, 0);
    assert.ok(find(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector}`).classList.contains('active'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${groupsSelector}`).length, 1);
    assert.notOk(find(`${alertsSelector}:nth-of-type(2) ${groupsSelector}:nth-of-type(1)`).classList.contains('is-selected'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${alertsHeaderSelector}`).length, 1);
    assert.ok(find(`${alertsSelector}:nth-of-type(2) ${alertsHeaderSelector}`).classList.contains('is-open'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(3) ${toggleEventsSelector}`).length, 1);
    assert.equal(findAll(`${alertsSelector}:nth-of-type(3) ${toggleEnrichmentsSelector}`).length, 0);
    assert.ok(find(`${alertsSelector}:nth-of-type(3) ${toggleEventsSelector}`).classList.contains('active'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(3) ${groupsSelector}`).length, 1);
    assert.ok(find(`${alertsSelector}:nth-of-type(3) ${groupsSelector}:nth-of-type(1)`).classList.contains('is-selected'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${alertsHeaderSelector}`).length, 1);
    assert.ok(find(`${alertsSelector}:nth-of-type(2) ${alertsHeaderSelector}`).classList.contains('is-open'));
  });

  test('storyline will explicitly mark alerts that support event analysis', async function(assert) {
    setState({
      respond: {
        recon: Immutable.from(DATA.generateRecon()),
        incident: Immutable.from(DATA.generateIncident({ withSelection: true })),
        storyline: DATA.generateStoryline({ withEnrichment: true })
      }
    });

    await render(hbs`{{rsa-incident/container}}`);

    assert.equal(findAll(alertsSelector).length, 8);

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector}`).length, 1);
    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector} > ${eventAnalysisSelector}`).length, 1);

    assert.equal(findAll(`${alertsSelector}:nth-of-type(3) ${toggleEventsSelector}`).length, 1);
    assert.equal(findAll(`${alertsSelector}:nth-of-type(3) ${toggleEventsSelector} > ${eventAnalysisSelector}`).length, 1);

    assert.equal(findAll(`${alertsSelector}:nth-of-type(5) ${toggleEventsSelector}`).length, 1);
    assert.equal(findAll(`${alertsSelector}:nth-of-type(5) ${toggleEventsSelector} > ${eventAnalysisSelector}`).length, 0);
  });

  test('storyline will not mark alerts for event analysis when user does not have permission', async function(assert) {
    hasPermission = false;

    setState({
      respond: {
        recon: Immutable.from(DATA.generateRecon()),
        incident: Immutable.from(DATA.generateIncident({ withSelection: true })),
        storyline: DATA.generateStoryline({ withEnrichment: true, withEventSourceId: false })
      }
    });

    await render(hbs`{{rsa-incident/container}}`);

    assert.equal(findAll(alertsSelector).length, 8);

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector}`).length, 1);
    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector} > ${eventAnalysisSelector}`).length, 0);

    assert.equal(findAll(`${alertsSelector}:nth-of-type(5) ${toggleEventsSelector}`).length, 1);
    assert.equal(findAll(`${alertsSelector}:nth-of-type(5) ${toggleEventsSelector} > ${eventAnalysisSelector}`).length, 0);
  });

  test('storyline will not mark alerts for event analysis when event does not have event source id value', async function(assert) {
    setState({
      respond: {
        recon: Immutable.from(DATA.generateRecon()),
        incident: Immutable.from(DATA.generateIncident({ withSelection: true })),
        storyline: DATA.generateStoryline({ withEnrichment: true, withEventSourceId: false })
      }
    });

    await render(hbs`{{rsa-incident/container}}`);

    assert.equal(findAll(alertsSelector).length, 8);

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector}`).length, 1);
    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector} > ${eventAnalysisSelector}`).length, 0);

    assert.equal(findAll(`${alertsSelector}:nth-of-type(5) ${toggleEventsSelector}`).length, 1);
    assert.equal(findAll(`${alertsSelector}:nth-of-type(5) ${toggleEventsSelector} > ${eventAnalysisSelector}`).length, 0);
  });

  test('storyline will not mark alerts for event analysis when no core devices are available', async function(assert) {
    setState({
      respond: {
        recon: Immutable.from({
          serviceData: undefined,
          isServicesLoading: undefined,
          isServicesRetrieveError: undefined
        }),
        incident: Immutable.from(DATA.generateIncident({ withSelection: true })),
        storyline: DATA.generateStoryline({ withEnrichment: true })
      }
    });

    await render(hbs`{{rsa-incident/container}}`);

    assert.equal(findAll(alertsSelector).length, 8);

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector}`).length, 1);
    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector} > ${eventAnalysisSelector}`).length, 0);

    assert.equal(findAll(`${alertsSelector}:nth-of-type(5) ${toggleEventsSelector}`).length, 1);
    assert.equal(findAll(`${alertsSelector}:nth-of-type(5) ${toggleEventsSelector} > ${eventAnalysisSelector}`).length, 0);
  });

  test('storyline will mark alerts for event analysis even without selection or enrichments', async function(assert) {
    setState({
      respond: {
        recon: Immutable.from(DATA.generateRecon()),
        incident: Immutable.from(DATA.generateIncident({ withSelection: false })),
        storyline: DATA.generateStoryline({ withEnrichment: false })
      }
    });

    await render(hbs`{{rsa-incident/container}}`);

    assert.equal(findAll(alertsSelector).length, 8);

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector}`).length, 1);
    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector} > ${eventAnalysisSelector}`).length, 1);

    assert.equal(findAll(`${alertsSelector}:nth-of-type(5) ${toggleEventsSelector}`).length, 1);
    assert.equal(findAll(`${alertsSelector}:nth-of-type(5) ${toggleEventsSelector} > ${eventAnalysisSelector}`).length, 0);
  });
});

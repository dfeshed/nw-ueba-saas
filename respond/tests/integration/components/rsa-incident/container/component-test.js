import * as DATA from './data';
import { module, test } from 'qunit';
import Service from '@ember/service';
import { computed } from '@ember/object';
import Immutable from 'seamless-immutable';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { setupRenderingTest } from 'ember-qunit';
import { recon } from 'respond/actions/api';
import { bindActionCreators } from 'redux';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { click, render, findAll, find } from '@ember/test-helpers';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { t } from './helper';

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
        recon: DATA.generateRecon(),
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
        recon: DATA.generateRecon(),
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
        recon: DATA.generateRecon(),
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
        recon: DATA.generateRecon(),
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
        recon: DATA.generateRecon(),
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
        recon: DATA.generateRecon(),
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
        recon: {
          serviceData: undefined,
          isServicesLoading: undefined,
          isServicesRetrieveError: undefined
        },
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
        recon: DATA.generateRecon(),
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

  test('storyline will mark alerts for event analysis when core devices use non ssl mode', async function(assert) {
    setState({
      respond: {
        recon: DATA.generateRecon(),
        incident: Immutable.from(DATA.generateIncident({ withSelection: false })),
        storyline: DATA.generateStoryline({ withEnrichment: false, withSslEventSource: false })
      }
    });

    await render(hbs`{{rsa-incident/container}}`);

    assert.equal(findAll(alertsSelector).length, 8);

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector}`).length, 1);
    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector} > ${eventAnalysisSelector}`).length, 1);

    assert.equal(findAll(`${alertsSelector}:nth-of-type(5) ${toggleEventsSelector}`).length, 1);
    assert.equal(findAll(`${alertsSelector}:nth-of-type(5) ${toggleEventsSelector} > ${eventAnalysisSelector}`).length, 0);
  });

  test('storyline will mark alerts for event analysis when core devices use non ssl mode and port is string type', async function(assert) {
    setState({
      respond: {
        recon: DATA.generateRecon('56005'),
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

  test('storyline will not blow up when attempting to match core devices using fallback and port is non numeric', async function(assert) {
    setState({
      respond: {
        recon: DATA.generateRecon('apple'),
        incident: Immutable.from(DATA.generateIncident({ withSelection: false })),
        storyline: DATA.generateStoryline({ withEnrichment: false })
      }
    });

    await render(hbs`{{rsa-incident/container}}`);

    assert.equal(findAll(alertsSelector).length, 8);

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector}`).length, 1);
    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector} > ${eventAnalysisSelector}`).length, 0);

    assert.equal(findAll(`${alertsSelector}:nth-of-type(5) ${toggleEventsSelector}`).length, 1);
    assert.equal(findAll(`${alertsSelector}:nth-of-type(5) ${toggleEventsSelector} > ${eventAnalysisSelector}`).length, 0);
  });

  test('storyline will mark alerts for event analysis when core devices become available after initial render', async function(assert) {
    assert.expect(12);

    const serviceState = Immutable.from({
      serviceData: undefined,
      isServicesLoading: undefined,
      isServicesRetrieveError: undefined
    });
    setState({
      respond: {
        recon: serviceState,
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

    const redux = this.owner.lookup('service:redux');
    const getServices = bindActionCreators(recon.getServices, redux.dispatch.bind(redux));

    await getServices();

    assert.equal(findAll(alertsSelector).length, 8);

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector}`).length, 1);
    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector} > ${eventAnalysisSelector}`).length, 1);
    assert.equal(find(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector} > ${eventAnalysisSelector}`).getAttribute('title'), null);
    assert.ok(find(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector} > ${eventAnalysisSelector}`).getAttribute('class').includes('on'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(5) ${toggleEventsSelector}`).length, 1);
    assert.equal(findAll(`${alertsSelector}:nth-of-type(5) ${toggleEventsSelector} > ${eventAnalysisSelector}`).length, 0);
  });

  test('storyline will add title to alerts with event analysis support when core devices do not match minVersion', async function(assert) {
    assert.expect(7);

    const serviceState = Immutable.from({
      serviceData: {
        '555d9a6fe4b0d37c827d402d': {
          id: '555d9a6fe4b0d37c827d402d',
          displayName: 'loki-concentrator',
          name: 'CONCENTRATOR',
          version: '11.1.0.0',
          host: '10.4.61.33',
          port: 56005
        }
      },
      isServicesLoading: false,
      isServicesRetrieveError: false
    });

    setState({
      respond: {
        recon: serviceState,
        incident: Immutable.from(DATA.generateIncident({ withSelection: true })),
        storyline: DATA.generateStoryline({ withEnrichment: true })
      }
    });

    await render(hbs`{{rsa-incident/container}}`);

    assert.equal(findAll(alertsSelector).length, 8);

    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector}`).length, 1);
    assert.equal(findAll(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector} > ${eventAnalysisSelector}`).length, 1);

    const title = t(this, 'investigate.services.coreServiceNotUpdated', { version: '11.1.0.0', minVersion: '11.2' });
    assert.equal(find(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector} > ${eventAnalysisSelector}`).getAttribute('title'), title);
    assert.ok(find(`${alertsSelector}:nth-of-type(2) ${toggleEventsSelector} > ${eventAnalysisSelector}`).getAttribute('class').includes('off'));

    assert.equal(findAll(`${alertsSelector}:nth-of-type(5) ${toggleEventsSelector}`).length, 1);
    assert.equal(findAll(`${alertsSelector}:nth-of-type(5) ${toggleEventsSelector} > ${eventAnalysisSelector}`).length, 0);
  });
});

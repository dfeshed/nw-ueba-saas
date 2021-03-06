import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { find, render } from '@ember/test-helpers';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { patchReducer } from '../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import { investigateEvent, malwareEvent } from './data';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { setupIntl } from 'ember-intl/test-support';

let setState, investigatePageService;

const services = [
  { 'id': '555d9a6fe4b0d37c827d402d', 'displayName': 'loki-concentrator', 'name': 'CONCENTRATOR', 'version': '11.4.0.0', 'host': '10.4.61.33', 'port': 56005 }
];

module('Integration | Component | rsa-alert/events-sheet', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });
  setupIntl(hooks, 'en-us');

  hooks.beforeEach(function() {
    initialize(this.owner);
    investigatePageService = this.owner.lookup('service:investigatePage');
    investigatePageService.set('legacyEventsEnabled', true);
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
  });

  test('show event analysis link in event details if legacy events flag is disabled', async function(assert) {
    investigatePageService.set('legacyEventsEnabled', false);
    setState({
      respond: {
        alert: {
          events: investigateEvent,
          info: {
            alert: {
              numEvents: 1
            }
          }
        },
        recon: {
          serviceData: services
        }
      }
    });

    await render(hbs`{{rsa-alert/events-sheet}}`);

    assert.ok(find('.rsa-event-details-body .related-link a').href.indexOf('/investigate/recon?eventId=150') > 0, 'event analysis link should be displayed');
  });

  test('show legacy events link in event details if legacy events flag is enabled', async function(assert) {
    setState({
      respond: {
        alert: {
          events: investigateEvent,
          info: {
            alert: {
              numEvents: 1
            }
          }
        },
        recon: {
          serviceData: services
        }
      }
    });

    await render(hbs`{{rsa-alert/events-sheet}}`);

    assert.ok(find('.rsa-event-details-body .related-link a').href.indexOf('/investigation/host/10.4.61.36:56005/navigate/event/AUTO/217948') > 0, 'legacy link should be displayed');
  });

  test('show investigate malware link with pivot to malware page irrespective of status of legacy events flag for malware event', async function(assert) {
    setState({
      respond: {
        alert: {
          events: malwareEvent,
          info: {
            alert: {
              numEvents: 1
            }
          }
        },
        recon: {
          serviceData: services
        }
      }
    });

    await render(hbs`{{rsa-alert/events-sheet}}`);

    assert.ok(find('.rsa-event-details-body .related-link a').href.indexOf('/investigation/undefined/malware/event/3328608') > 0,
      'investigate malware link with pivot to malware page should be displayed');

    investigatePageService.set('legacyEventsEnabled', false);

    await render(hbs`{{rsa-alert/events-sheet}}`);

    assert.ok(find('.rsa-event-details-body .related-link a').href.indexOf('/investigation/undefined/malware/event/3328608') > 0,
      'investigate malware link with pivot to malware page should be displayed');
  });

});

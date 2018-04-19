import { setupRenderingTest } from 'ember-qunit';
import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { findAll, render } from '@ember/test-helpers';
import engineResolverFor from '../../../../helpers/engine-resolver';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | Event Details', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('The component appears in the DOM', async function(assert) {
    await render(hbs`{{rsa-event-details/body}}`);
    assert.equal(findAll('.rsa-event-details-body').length, 1, 'The component appears in the DOM');
  });

  test('The related links appear as anchor tags created from the urls in the related link data', async function(assert) {
    const relatedLinks = [
      {
        type: 'investigate_original_event',
        url: '/investigation/host/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/event/AUTO/29589'
      },
      {
        type: 'investigate_destination_domain',
        url: '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/%2Fdate%2F2015-10-27T18%3A37%3A05.000Z%2F2015-10-27T18%3A47%3A05.000Z'
      }
    ];
    this.set('eventDetails', {
      related_links: relatedLinks
    });
    await render(hbs`{{rsa-event-details/body model=eventDetails}}`);
    const links = findAll('.rsa-property-tree tr.related-link a');
    const urlPartsRegex = /(http[s]?:\/\/)?([^\/\s]+)(\/.*)/;
    assert.equal(links.length, 2, 'Two anchor links appear, one for each found in the data');
    assert.equal(links[0].textContent.trim(), 'Investigate Original Event', 'The text for the first link matches the type (after split/capitalized)');
    assert.equal(links[0].href.match(urlPartsRegex)[3], relatedLinks[0].url, 'The url for the first link matches that found in the data');
    assert.equal(links[1].textContent.trim(), 'Investigate Destination Domain', 'The text for the second link matches the type (after split/capitalized)');
    assert.equal(links[1].href.match(urlPartsRegex)[3], relatedLinks[1].url, 'The url for the second link matches that found in the data');
  });
});



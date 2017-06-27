import Storypoint from 'respond/utils/storypoint/storypoint';
import { module, test } from 'qunit';

module('Unit | Utility | storypoint/storypoint');

const eventWithEnrichment = {
  id: 3,
  enrichment: {
    ctxhub: {
      'domain_is_whitelisted': false
    },
    domain: {
      'ua_ratio_score': 100,
      'ua_score': 100,
      'referer_score': 100
    },
    whois: {
      'age_score': 100,
      'validity_score': 100
    },
    smooth: {
      'smooth_beaconing_score': 100
    }
  }
};

const events = [
  { id: 1 },
  { id: 2 }
];

const indicator = {
  id: 'indicator1'
};

const subject = Storypoint.create({
  indicator,
  events
});

test('it uses its enrichments as its items by default', function(assert) {
  assert.ok(subject);
  assert.ok(subject.get('showEnrichmentsAsItems'), 'Expected the default to not use enrichments');
});

test('it computes an array of enrichments from it last events', function(assert) {
  const result = subject.get('enrichments');
  assert.notOk(result.length, 'Expected an empty enrichments array when no enrichment is in last event');

  events.pushObject(eventWithEnrichment);

  const result2 = subject.get('enrichments');
  assert.equal(result2.length, 6, 'Expected a non-empty enrichments array when last event has enrichment');
  result2.forEach(({ id, key, isEnrichment, allEnrichments }) => {
    assert.equal(
      id,
      eventWithEnrichment.id,
      'Expected each enrichment to have the same id as its corresponding event'
    );
    assert.ok(
      key,
      'Expected each enrichment to have a key'
    );
    assert.ok(
      isEnrichment,
      'Expected each enrichment to have an isEnrichment = true flag'
    );
    assert.equal(
      allEnrichments,
      eventWithEnrichment.enrichment,
      'Expected each enrichment to have a reference to the entire enrichment hash'
    );
  });
});

test('it uses its enrichments as its items when instructed to', function(assert) {
  subject.set('showEnrichmentsAsItems', true);
  assert.equal(subject.get('items'), subject.get('enrichments'), 'Expected items and enrichments to match');
});

test('it uses its events as its items when instructed to', function(assert) {
  subject.set('showEnrichmentsAsItems', false);
  assert.equal(subject.get('items'), subject.get('events'), 'Expected items and events to match');
});
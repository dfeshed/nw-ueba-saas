import { test } from 'qunit';
import moduleForAcceptance from '../helpers/module-for-acceptance';
import engineResolverFor from '../helpers/engine-resolver';

moduleForAcceptance('Acceptance | basic', {
  resolver: engineResolverFor('live-content')
});

test('visiting /live-content/live', function(assert) {
  visit('/live-content/live');

  andThen(function() {
    assert.equal(currentURL(), '/live-content/live');
  });
});
import { test } from 'qunit';
import moduleForAcceptance from '../helpers/module-for-acceptance';
import engineResolverFor from '../helpers/engine-resolver';

moduleForAcceptance('Acceptance | basic', {
  resolver: engineResolverFor('live-content')
});

test('visiting /live-content/index', function(assert) {
  visit('/live-content/index');

  andThen(function() {
    assert.equal(currentURL(), '/live-content/index');
  });
});
import { skip } from 'qunit';
import moduleForAcceptance from '../helpers/module-for-acceptance';
import engineResolverFor from '../helpers/engine-resolver';

moduleForAcceptance('Acceptance | basic', {
  resolver: engineResolverFor('endpoint')
});

skip('visiting /endpoint/hosts', function(assert) {
  visit('/endpoint/hosts');
  andThen(function() {
    assert.equal(currentURL(), '/endpoint/hosts');
  });
});

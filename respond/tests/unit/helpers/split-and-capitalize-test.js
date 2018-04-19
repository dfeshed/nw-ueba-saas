import { splitAndCapitalize } from 'respond/helpers/split-and-capitalize';
import { module, test } from 'qunit';

module('Unit | Helper | split-and-capitalize', function() {
  test('it splits the string using the default delimiter and capitalizes', function(assert) {
    assert.equal(splitAndCapitalize('frodo_lives'), 'Frodo Lives');
    assert.equal(splitAndCapitalize('frodo-lives'), 'Frodo Lives');
    assert.equal(splitAndCapitalize('frodo lives'), 'Frodo Lives');
  });
});


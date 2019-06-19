import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { removeQuotes, arrToString } from 'admin-source-management/utils/string-util';

module('Unit | Utils | utils/string-util', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('removeQuotes should return the string without quotes', function(assert) {
    assert.expect(4);
    /* eslint-disable no-useless-escape */
    let string = 'abc';
    let output = removeQuotes(string);
    assert.equal(output, 'abc', `removeQuotes returns ${output} when input is ${string}`);
    string = '\'abc\'';
    output = removeQuotes(string);
    assert.equal(output, 'abc', `removeQuotes returns ${output} when input is ${string}`);
    string = '\"abc\"';
    output = removeQuotes(string);
    assert.equal(output, 'abc', `removeQuotes returns ${output} when input is ${string}`);
    string = '\"abc\'';
    output = removeQuotes(string);
    assert.equal(output, 'abc', `removeQuotes returns ${output} when input is ${string}`);
  });

  test('arrToString should convert the array to string separated by a new line', function(assert) {
    assert.expect(3);
    /* eslint-disable quotes */
    let arr = ['abc', 'def'];
    let output = arrToString(arr);
    assert.equal(output, '"abc"\n"def"', 'arrToString returns the correct string');
    arr = ["abc", "def"];
    output = arrToString(arr);
    assert.equal(output, '"abc"\n"def"', 'arrToString returns the correct string');
    arr = ['abc', "def"];
    output = arrToString(arr);
    assert.equal(output, '"abc"\n"def"', 'arrToString returns the correct string');
  });
});

import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import _ from 'lodash';
import { descriptionsForDisplay } from 'admin-source-management/reducers/usm/util/selector-helpers';

module('Unit | Utils | reducers/usm/util/selector-helpers', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('descriptionsForDisplay() should properly handle a null description', function(assert) {
    const description = null;
    const expectedDescriptions = {
      truncated: null,
      truncatedWithEllipsis: null
    };
    const actualDescriptions = descriptionsForDisplay(description);
    assert.deepEqual(actualDescriptions, expectedDescriptions, 'description is null as expected');
  });

  test('descriptionsForDisplay() should return an object containing two truncated versions of a description <= 256 chars', function(assert) {
    const description = 'Short Description eh!';
    const expectedDescriptions = {
      truncated: description,
      truncatedWithEllipsis: description
    };
    const actualDescriptions = descriptionsForDisplay(description);
    assert.deepEqual(actualDescriptions, expectedDescriptions, `description is ${description} as expected`);
  });

  test('descriptionsForDisplay() should return an object containing two truncated versions of a description > 256 chars', function(assert) {
    const longDescription445 = 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.';
    const expectedDescriptions256 = {
      truncated: _.truncate(longDescription445, { length: 256, omission: '' }),
      truncatedWithEllipsis: _.truncate(longDescription445, { length: 256, omission: '...' })
    };
    const actualDescriptions256 = descriptionsForDisplay(longDescription445);
    assert.deepEqual(actualDescriptions256, expectedDescriptions256, 'description is truncated as expected');
  });

});

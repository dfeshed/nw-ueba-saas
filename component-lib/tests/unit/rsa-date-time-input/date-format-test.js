import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { format } from 'component-lib/components/rsa-date-time-input/util/date-format';

module('Unit | date-format', function(hooks) {

  setupTest(hooks);

  test('format() properly formats a numeric value into a formatted (display) value', function(assert) {
    assert.equal(format(null), '', 'Null values are returned as empty strings');
    assert.equal(format('ab'), '', 'Non-numbers are returned as empty strings');
    assert.equal(format(10, 'date'), '10');
    assert.equal(format(10, 'month'), '10');
    assert.equal(format(1976, 'year'), '1976');
    assert.equal(format(1, 'date'), '01', 'a single digit number is formatted with a padded zero');
    assert.equal(format(1, 'month'), '01');

    assert.equal(format(1, 'year'), '2001', 'Single-digit years are preceeded by 200');
    assert.equal(format(10, 'year'), '2010', 'Double-digit years are preceeded by 200');
    assert.equal(format(100, 'year'), '0100', 'Dates greater than or equal to 100 are treated as-is');
    assert.equal(format(50, 'date'), '50'); // We currently keep invalid days as-is
    assert.equal(format(50, 'month'), '50'); // We currently keep invalid months as-is
    assert.equal(format(-5, 'month'), '05'); // Negatives are converted to positives before evaluation
    assert.equal(format(.5, 'month'), '00'); // Fractional values are converted to 1 and returned as '01'

    assert.equal(format(15, 'hour'), '15');
    assert.equal(format(1, 'hour'), '01');
    assert.equal(format(59, 'minute'), '59');
    assert.equal(format(1, 'minute'), '01');
    assert.equal(format(59, 'second'), '59');
    assert.equal(format(1, 'second'), '01');
  });
});

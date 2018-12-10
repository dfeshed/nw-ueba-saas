import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { t } from '../../../../integration/component/respond/risk-scoring/helpers';

module('Unit | Validator | Respond | thresholdFormValidations', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('threshold form validations', async function(assert) {
    assert.expect(42);

    const thresholdForm = window.require('configure/validations/respond/risk-scoring/threshold-form');
    const validator = thresholdForm.default;

    const thresholdMessage = t(this, 'validations.threshold');
    const fileThresholdValidator = (value) => validator['file.threshold']('', value);
    assert.equal(fileThresholdValidator(undefined), thresholdMessage);
    assert.equal(fileThresholdValidator(null), thresholdMessage);
    assert.equal(fileThresholdValidator(0), true);
    assert.equal(fileThresholdValidator(1), true);
    assert.equal(fileThresholdValidator(100), true);
    assert.equal(fileThresholdValidator(101), thresholdMessage);
    assert.equal(fileThresholdValidator(9999), thresholdMessage);
    assert.equal(fileThresholdValidator('a'), thresholdMessage);

    const timeWindowMessage = t(this, 'validations.timeWindow');
    const fileTimeWindowValidator = (value) => validator['file.timeWindow']('', value);
    assert.equal(fileTimeWindowValidator(undefined), timeWindowMessage);
    assert.equal(fileTimeWindowValidator(null), timeWindowMessage);
    assert.equal(fileTimeWindowValidator(0), timeWindowMessage);
    assert.equal(fileTimeWindowValidator(1), true);
    assert.equal(fileTimeWindowValidator(24), true);
    assert.equal(fileTimeWindowValidator(25), timeWindowMessage);
    assert.equal(fileTimeWindowValidator(9999), timeWindowMessage);
    assert.equal(fileTimeWindowValidator('a'), timeWindowMessage);

    const timeWindowUnitMessage = t(this, 'validations.timeWindowUnit');
    const fileTimeWindowUnitValidator = (value) => validator['file.timeWindowUnit']('', value);
    assert.equal(fileTimeWindowUnitValidator(undefined), timeWindowUnitMessage);
    assert.equal(fileTimeWindowUnitValidator(null), timeWindowUnitMessage);
    assert.equal(fileTimeWindowUnitValidator([]), timeWindowUnitMessage);
    assert.equal(fileTimeWindowUnitValidator('d'), true);
    assert.equal(fileTimeWindowUnitValidator('h'), true);

    const hostThresholdValidator = (value) => validator['host.threshold']('', value);
    assert.equal(hostThresholdValidator(undefined), thresholdMessage);
    assert.equal(hostThresholdValidator(null), thresholdMessage);
    assert.equal(hostThresholdValidator(0), true);
    assert.equal(hostThresholdValidator(1), true);
    assert.equal(hostThresholdValidator(100), true);
    assert.equal(hostThresholdValidator(101), thresholdMessage);
    assert.equal(hostThresholdValidator(9999), thresholdMessage);
    assert.equal(hostThresholdValidator('a'), thresholdMessage);

    const hostTimeWindowValidator = (value) => validator['host.timeWindow']('', value);
    assert.equal(hostTimeWindowValidator(undefined), timeWindowMessage);
    assert.equal(hostTimeWindowValidator(null), timeWindowMessage);
    assert.equal(hostTimeWindowValidator(0), timeWindowMessage);
    assert.equal(hostTimeWindowValidator(1), true);
    assert.equal(hostTimeWindowValidator(24), true);
    assert.equal(hostTimeWindowValidator(25), timeWindowMessage);
    assert.equal(hostTimeWindowValidator(9999), timeWindowMessage);
    assert.equal(hostTimeWindowValidator('a'), timeWindowMessage);

    const hostTimeWindowUnitValidator = (value) => validator['host.timeWindowUnit']('', value);
    assert.equal(hostTimeWindowUnitValidator(undefined), timeWindowUnitMessage);
    assert.equal(hostTimeWindowUnitValidator(null), timeWindowUnitMessage);
    assert.equal(hostTimeWindowUnitValidator([]), timeWindowUnitMessage);
    assert.equal(hostTimeWindowUnitValidator('d'), true);
    assert.equal(hostTimeWindowUnitValidator('h'), true);
  });
});

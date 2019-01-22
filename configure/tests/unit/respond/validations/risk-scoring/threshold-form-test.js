import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { t } from '../../../../integration/component/respond/risk-scoring/helpers';

let thresholdForm, validator, thresholdMessage, timeWindowUnitMessage, timeWindowMessage;

module('Unit | Validator | Respond | thresholdFormValidations', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);

    thresholdForm = window.require('configure/validations/respond/risk-scoring/threshold-form');
    validator = thresholdForm.default;
    thresholdMessage = t(this, 'validations.threshold');
    timeWindowUnitMessage = t(this, 'validations.timeWindowUnit');
    timeWindowMessage = t(this, 'validations.timeWindow');
  });

  test('threshold form validations', async function(assert) {
    assert.expect(42);

    const fileThresholdValidator = (value) => validator['file.threshold']('', value, undefined, { 'file.enabled': true }, {});
    assert.equal(fileThresholdValidator(undefined), thresholdMessage);
    assert.equal(fileThresholdValidator(null), thresholdMessage);
    assert.equal(fileThresholdValidator(0), true);
    assert.equal(fileThresholdValidator(1), true);
    assert.equal(fileThresholdValidator(100), true);
    assert.equal(fileThresholdValidator(101), thresholdMessage);
    assert.equal(fileThresholdValidator(9999), thresholdMessage);
    assert.equal(fileThresholdValidator('a'), thresholdMessage);

    const fileTimeWindowValidator = (value) => validator['file.timeWindow']('', value, undefined, { 'file.enabled': true }, {});
    assert.equal(fileTimeWindowValidator(undefined), timeWindowMessage);
    assert.equal(fileTimeWindowValidator(null), timeWindowMessage);
    assert.equal(fileTimeWindowValidator(0), timeWindowMessage);
    assert.equal(fileTimeWindowValidator(1), true);
    assert.equal(fileTimeWindowValidator(24), true);
    assert.equal(fileTimeWindowValidator(25), timeWindowMessage);
    assert.equal(fileTimeWindowValidator(9999), timeWindowMessage);
    assert.equal(fileTimeWindowValidator('a'), timeWindowMessage);

    const fileTimeWindowUnitValidator = (value) => validator['file.timeWindowUnit']('', value, undefined, { 'file.enabled': true }, {});
    assert.equal(fileTimeWindowUnitValidator(undefined), timeWindowUnitMessage);
    assert.equal(fileTimeWindowUnitValidator(null), timeWindowUnitMessage);
    assert.equal(fileTimeWindowUnitValidator([]), timeWindowUnitMessage);
    assert.equal(fileTimeWindowUnitValidator('d'), true);
    assert.equal(fileTimeWindowUnitValidator('h'), true);

    const hostThresholdValidator = (value) => validator['host.threshold']('', value, undefined, { 'host.enabled': true }, {});
    assert.equal(hostThresholdValidator(undefined), thresholdMessage);
    assert.equal(hostThresholdValidator(null), thresholdMessage);
    assert.equal(hostThresholdValidator(0), true);
    assert.equal(hostThresholdValidator(1), true);
    assert.equal(hostThresholdValidator(100), true);
    assert.equal(hostThresholdValidator(101), thresholdMessage);
    assert.equal(hostThresholdValidator(9999), thresholdMessage);
    assert.equal(hostThresholdValidator('a'), thresholdMessage);

    const hostTimeWindowValidator = (value) => validator['host.timeWindow']('', value, undefined, { 'host.enabled': true }, {});
    assert.equal(hostTimeWindowValidator(undefined), timeWindowMessage);
    assert.equal(hostTimeWindowValidator(null), timeWindowMessage);
    assert.equal(hostTimeWindowValidator(0), timeWindowMessage);
    assert.equal(hostTimeWindowValidator(1), true);
    assert.equal(hostTimeWindowValidator(24), true);
    assert.equal(hostTimeWindowValidator(25), timeWindowMessage);
    assert.equal(hostTimeWindowValidator(9999), timeWindowMessage);
    assert.equal(hostTimeWindowValidator('a'), timeWindowMessage);

    const hostTimeWindowUnitValidator = (value) => validator['host.timeWindowUnit']('', value, undefined, { 'host.enabled': true }, {});
    assert.equal(hostTimeWindowUnitValidator(undefined), timeWindowUnitMessage);
    assert.equal(hostTimeWindowUnitValidator(null), timeWindowUnitMessage);
    assert.equal(hostTimeWindowUnitValidator([]), timeWindowUnitMessage);
    assert.equal(hostTimeWindowUnitValidator('d'), true);
    assert.equal(hostTimeWindowUnitValidator('h'), true);
  });

  test('when file.enabled no error message is returned for file validations', async function(assert) {
    assert.expect(3);

    const fileThresholdValidator = (value) => validator['file.threshold']('', value, undefined, { 'file.enabled': false }, {});
    assert.equal(fileThresholdValidator('a'), true);

    const fileTimeWindowValidator = (value) => validator['file.timeWindow']('', value, undefined, { 'file.enabled': false }, {});
    assert.equal(fileTimeWindowValidator('a'), true);

    const fileTimeWindowUnitValidator = (value) => validator['file.timeWindowUnit']('', value, undefined, { 'file.enabled': false }, {});
    assert.equal(fileTimeWindowUnitValidator(undefined), true);
  });

  test('when host.enabled no error message is returned for host validations', async function(assert) {
    assert.expect(3);

    const hostThresholdValidator = (value) => validator['host.threshold']('', value, undefined, { 'host.enabled': false }, {});
    assert.equal(hostThresholdValidator('a'), true);

    const hostTimeWindowValidator = (value) => validator['host.timeWindow']('', value, undefined, { 'host.enabled': false }, {});
    assert.equal(hostTimeWindowValidator('a'), true);

    const hostTimeWindowUnitValidator = (value) => validator['host.timeWindowUnit']('', value, undefined, { 'host.enabled': false }, {});
    assert.equal(hostTimeWindowUnitValidator(undefined), true);
  });

  test('file.enabled for delta takes priority over content', async function(assert) {
    assert.expect(12);

    let fileThresholdValidator = (value) => validator['file.threshold']('', value, undefined, {}, { 'file.enabled': true });
    assert.equal(fileThresholdValidator('a'), thresholdMessage);

    fileThresholdValidator = (value) => validator['file.threshold']('', value, undefined, {}, { 'file.enabled': false });
    assert.equal(fileThresholdValidator('a'), true);

    fileThresholdValidator = (value) => validator['file.threshold']('', value, undefined, { 'file.enabled': true }, { 'file.enabled': false });
    assert.equal(fileThresholdValidator('a'), thresholdMessage);

    fileThresholdValidator = (value) => validator['file.threshold']('', value, undefined, { 'file.enabled': false }, { 'file.enabled': true });
    assert.equal(fileThresholdValidator('a'), true);

    let fileTimeWindowValidator = (value) => validator['file.timeWindow']('', value, undefined, {}, { 'file.enabled': true });
    assert.equal(fileTimeWindowValidator('a'), timeWindowMessage);

    fileTimeWindowValidator = (value) => validator['file.timeWindow']('', value, undefined, {}, { 'file.enabled': false });
    assert.equal(fileTimeWindowValidator('a'), true);

    fileTimeWindowValidator = (value) => validator['file.timeWindow']('', value, undefined, { 'file.enabled': true }, { 'file.enabled': false });
    assert.equal(fileTimeWindowValidator('a'), timeWindowMessage);

    fileTimeWindowValidator = (value) => validator['file.timeWindow']('', value, undefined, { 'file.enabled': false }, { 'file.enabled': true });
    assert.equal(fileTimeWindowValidator('a'), true);

    let fileTimeWindowUnitValidator = (value) => validator['file.timeWindowUnit']('', value, undefined, {}, { 'file.enabled': true });
    assert.equal(fileTimeWindowUnitValidator(undefined), timeWindowUnitMessage);

    fileTimeWindowUnitValidator = (value) => validator['file.timeWindowUnit']('', value, undefined, {}, { 'file.enabled': false });
    assert.equal(fileTimeWindowUnitValidator(undefined), true);

    fileTimeWindowUnitValidator = (value) => validator['file.timeWindowUnit']('', value, undefined, { 'file.enabled': true }, { 'file.enabled': false });
    assert.equal(fileTimeWindowUnitValidator(undefined), timeWindowUnitMessage);

    fileTimeWindowUnitValidator = (value) => validator['file.timeWindowUnit']('', value, undefined, { 'file.enabled': false }, { 'file.enabled': true });
    assert.equal(fileTimeWindowUnitValidator(undefined), true);
  });

  test('host.enabled for delta takes priority over content', async function(assert) {
    assert.expect(12);

    let hostThresholdValidator = (value) => validator['host.threshold']('', value, undefined, {}, { 'host.enabled': true });
    assert.equal(hostThresholdValidator('a'), thresholdMessage);

    hostThresholdValidator = (value) => validator['host.threshold']('', value, undefined, {}, { 'host.enabled': false });
    assert.equal(hostThresholdValidator('a'), true);

    hostThresholdValidator = (value) => validator['host.threshold']('', value, undefined, { 'host.enabled': true }, { 'host.enabled': false });
    assert.equal(hostThresholdValidator('a'), thresholdMessage);

    hostThresholdValidator = (value) => validator['host.threshold']('', value, undefined, { 'host.enabled': false }, { 'host.enabled': true });
    assert.equal(hostThresholdValidator('a'), true);

    let hostTimeWindowValidator = (value) => validator['host.timeWindow']('', value, undefined, {}, { 'host.enabled': true });
    assert.equal(hostTimeWindowValidator('a'), timeWindowMessage);

    hostTimeWindowValidator = (value) => validator['host.timeWindow']('', value, undefined, {}, { 'host.enabled': false });
    assert.equal(hostTimeWindowValidator('a'), true);

    hostTimeWindowValidator = (value) => validator['host.timeWindow']('', value, undefined, { 'host.enabled': true }, { 'host.enabled': false });
    assert.equal(hostTimeWindowValidator('a'), timeWindowMessage);

    hostTimeWindowValidator = (value) => validator['host.timeWindow']('', value, undefined, { 'host.enabled': false }, { 'host.enabled': true });
    assert.equal(hostTimeWindowValidator('a'), true);

    let hostTimeWindowUnitValidator = (value) => validator['host.timeWindowUnit']('', value, undefined, {}, { 'host.enabled': true });
    assert.equal(hostTimeWindowUnitValidator(undefined), timeWindowUnitMessage);

    hostTimeWindowUnitValidator = (value) => validator['host.timeWindowUnit']('', value, undefined, {}, { 'host.enabled': false });
    assert.equal(hostTimeWindowUnitValidator(undefined), true);

    hostTimeWindowUnitValidator = (value) => validator['host.timeWindowUnit']('', value, undefined, { 'host.enabled': true }, { 'host.enabled': false });
    assert.equal(hostTimeWindowUnitValidator(undefined), timeWindowUnitMessage);

    hostTimeWindowUnitValidator = (value) => validator['host.timeWindowUnit']('', value, undefined, { 'host.enabled': false }, { 'host.enabled': true });
    assert.equal(hostTimeWindowUnitValidator(undefined), true);
  });

});

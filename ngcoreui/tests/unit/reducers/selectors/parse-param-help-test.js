import { test, module } from 'qunit';
import { setupTest } from 'ember-qunit';
import parseParamHelp from 'ngcoreui/reducers/selectors/parse-param-help';

const helpForString = '<string, optional, {char:abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ?}> The name of the message to retrieve detailed help about (aliases are \'m\' or \'message\')';
const helpForEnumOne = '<string, optional, {enum-one:default|xml|html}> The format of the response, default returns in a human friendly format';
const helpForEnumAny = '<string, optional, {enum-any:m|p}> The types of data to wipe, meta and/or packets, default is just packets';

module('Unit | Selectors | parseParamHelp', (hooks) => {

  setupTest(hooks);

  test('paramHelp correctly parses a string param', (assert) => {
    const param = parseParamHelp({}, helpForString);

    assert.strictEqual(param.description, 'The name of the message to retrieve detailed help about (aliases are \'m\' or \'message\')', 'should correctly strip description of identifiers');
  });

  test('paramHelp correctly parses an enum-one param', (assert) => {
    const param = parseParamHelp({}, helpForEnumOne);

    assert.strictEqual(param.description, 'The format of the response, default returns in a human friendly format', 'should correctly strip description of identifiers');
    assert.strictEqual(param.type, 'enum-one', 'should correctly label param type');
    assert.deepEqual(param.acceptableValues, ['default', 'xml', 'html'], 'should parse acceptable enum values');
  });

  test('paramHelp correctly parses an enum-any param', (assert) => {
    const param = parseParamHelp({}, helpForEnumAny);

    assert.strictEqual(param.description, 'The types of data to wipe, meta and/or packets, default is just packets', 'should correctly strip description of identifiers');
    assert.strictEqual(param.type, 'enum-any', 'should correctly label param type');
    assert.deepEqual(param.acceptableValues, [{ name: 'm', code: 'm' }, { name: 'p', code: 'p' }], 'should parse acceptable enum values');
  });

});

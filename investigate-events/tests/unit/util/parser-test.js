import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import Parser from 'investigate-events/util/parser';
import LEXEMES from 'investigate-events/constants/lexemes';
import GRAMMAR from 'investigate-events/constants/grammar';

module('Unit | Util | Parser', function(hooks) {
  setupTest(hooks);

  test('correctly parses a basic meta operator value set', function(assert) {
    const tokens = [
      { type: LEXEMES.META, text: 'medium' },
      { type: LEXEMES.OPERATOR, text: '=' },
      { type: LEXEMES.NUMBER, text: '3' }
    ];
    const p = new Parser(tokens);
    const result = p.parse();
    assert.strictEqual(result.type, GRAMMAR.CRITERIA, 'Top level is criteria');
    assert.deepEqual(result.meta, { type: LEXEMES.META, text: 'medium' }, 'Meta is "medium"');
    assert.deepEqual(result.operator, { type: LEXEMES.OPERATOR, text: '=' }, 'Operator is "="');
    assert.deepEqual(result.valueRanges[0], {
      type: GRAMMAR.META_VALUE,
      value: {
        type: LEXEMES.NUMBER,
        text: '3'
      }
    }, 'Left value is "3"');
  });

  test('correctly parses two meta and &&', function(assert) {
    const tokens = [
      { type: LEXEMES.META, text: 'medium' },
      { type: LEXEMES.OPERATOR, text: '=' },
      { type: LEXEMES.NUMBER, text: '3' },
      { type: LEXEMES.AND, text: '&&' },
      { type: LEXEMES.META, text: 'filename' },
      { type: LEXEMES.OPERATOR, text: '!=' },
      { type: LEXEMES.STRING, text: 'hyberfile.sys' }
    ];
    const p = new Parser(tokens);
    const result = p.parse();
    assert.strictEqual(result.type, GRAMMAR.AND_EXPRESSION, 'Top level is and expression');
    assert.ok(result.left, 'Left side of and exists');
    assert.ok(result.right, 'Right side of and exists');
    assert.deepEqual(result.left.meta, { type: LEXEMES.META, text: 'medium' }, 'Left meta is "medium"');
    assert.deepEqual(result.right.meta, { type: LEXEMES.META, text: 'filename' }, 'Right meta is "filename"');
    assert.deepEqual(result.left.operator, { type: LEXEMES.OPERATOR, text: '=' }, 'Left operator is "="');
    assert.deepEqual(result.right.operator, { type: LEXEMES.OPERATOR, text: '!=' }, 'Right operator is "!="');
    assert.deepEqual(result.left.valueRanges[0], {
      type: GRAMMAR.META_VALUE,
      value: {
        type: LEXEMES.NUMBER,
        text: '3'
      }
    }, 'Left value is "3"');
    assert.deepEqual(result.right.valueRanges[0], {
      type: GRAMMAR.META_VALUE,
      value: {
        type: LEXEMES.STRING,
        text: 'hyberfile.sys'
      }
    }, 'Right value is "hyberfile.sys"');
  });

  test('correctly parses a group (parenthesis)', function(assert) {
    const tokens = [
      { type: LEXEMES.LEFT_PAREN, text: '(' },
      { type: LEXEMES.META, text: 'b' },
      { type: LEXEMES.OPERATOR, text: '=' },
      { type: LEXEMES.STRING, text: 'netwitness' },
      { type: LEXEMES.RIGHT_PAREN, text: ')' }
    ];
    const p = new Parser(tokens);
    const result = p.parse();
    assert.strictEqual(result.type, GRAMMAR.GROUP, 'Top level is a group');
    assert.strictEqual(result.group.type, GRAMMAR.CRITERIA, 'Inside of group is a criteria');
    assert.deepEqual(result.group.meta, { type: LEXEMES.META, text: 'b' }, 'Meta is "b"');
    assert.deepEqual(result.group.operator, { type: LEXEMES.OPERATOR, text: '=' }, 'Operator is "="');
    assert.deepEqual(result.group.valueRanges[0], {
      type: GRAMMAR.META_VALUE,
      value: {
        type: LEXEMES.STRING,
        text: 'netwitness'
      }
    }, 'Left value is "netwitness"');
  });

  test('throws error for mismatched parentheses', function(assert) {
    const tokens = [
      { type: LEXEMES.LEFT_PAREN, text: '(' },
      { type: LEXEMES.META, text: 'medium' },
      { type: LEXEMES.OPERATOR, text: '=' },
      { type: LEXEMES.NUMBER, text: '3' },
      { type: LEXEMES.RIGHT_PAREN, text: ')' },
      { type: LEXEMES.RIGHT_PAREN, text: ')' }
    ];
    const p = new Parser(tokens);
    assert.throws(() => {
      p.parse();
    }, new Error('Unexpected token: RIGHT_PAREN())'));
  });

  test('throws an error for a meta without operator', function(assert) {
    const tokens = [
      { type: LEXEMES.META, text: 'b' },
      { type: LEXEMES.OPERATOR, text: '=' },
      { type: LEXEMES.STRING, text: 'netwitness' },
      { type: LEXEMES.AND, text: '&&' },
      { type: LEXEMES.META, text: 'medium' }
    ];
    const p = new Parser(tokens);
    assert.throws(() => {
      p.parse();
    }, new Error('Expected token of type OPERATOR but reached the end of the input'));
  });

  test('throws an error for unexpected tokens', function(assert) {
    const tokens = [
      { type: LEXEMES.META, text: 'b' },
      { type: LEXEMES.OPERATOR, text: '=' },
      { type: LEXEMES.STRING, text: 'netwitness' },
      { type: LEXEMES.META, text: 'medium' },
      { type: LEXEMES.OPERATOR, text: '=' }
    ];
    const p = new Parser(tokens);
    assert.throws(() => {
      p.parse();
    }, new Error('Unexpected tokens: META(medium) OPERATOR(=)'));
  });

  test('throws an error for a meta without value when operator requires one (string)', function(assert) {
    const tokens = [
      { type: LEXEMES.META, text: 'b' },
      { type: LEXEMES.OPERATOR, text: '=' },
      { type: LEXEMES.META, text: 'medium' }
    ];
    const p = new Parser(tokens);
    assert.throws(() => {
      p.parse();
    }, new Error('Expected token of type NUMBER,STRING,IPV4_ADDRESS,IPV6_ADDRESS,MAC_ADDRESS but got type META'));
  });

  test('throws an error for a meta without value when operator requires one (number)', function(assert) {
    const tokens = [
      { type: LEXEMES.META, text: 'medium' },
      { type: LEXEMES.OPERATOR, text: '=' },
      { type: LEXEMES.META, text: 'b' }
    ];
    const p = new Parser(tokens);
    assert.throws(() => {
      p.parse();
    }, new Error('Expected token of type NUMBER,STRING,IPV4_ADDRESS,IPV6_ADDRESS,MAC_ADDRESS but got type META'));
  });

  test('throws an error for a meta with value but having a unary operator', function(assert) {
    const tokens = [
      { type: LEXEMES.META, text: 'medium' },
      { type: LEXEMES.OPERATOR, text: 'exists' },
      { type: LEXEMES.NUMBER, text: '7' }
    ];
    const p = new Parser(tokens);
    assert.throws(() => {
      p.parse();
    }, new Error('Invalid value 7 after unary operator exists'));
  });

  test('does not throw an error for a unary operator without a value', function(assert) {
    const tokens = [
      { type: LEXEMES.META, text: 'medium' },
      { type: LEXEMES.OPERATOR, text: 'exists' }
    ];
    const p = new Parser(tokens);
    const result = p.parse();
    assert.strictEqual(result.type, GRAMMAR.CRITERIA);
    assert.deepEqual(result.meta, { type: LEXEMES.META, text: 'medium' });
    assert.deepEqual(result.operator, { type: LEXEMES.OPERATOR, text: 'exists' });
    assert.notOk(result.valueRanges);
  });

  test('correctly parses multiple complex filters', function(assert) {
    // ((b = text) || (medium != 44)) && (bytes.src >= 1)
    const tokens = [
      { type: LEXEMES.LEFT_PAREN, text: '(' },
      { type: LEXEMES.LEFT_PAREN, text: '(' },
      { type: LEXEMES.META, text: 'b' },
      { type: LEXEMES.OPERATOR, text: '=' },
      { type: LEXEMES.STRING, text: 'text' },
      { type: LEXEMES.RIGHT_PAREN, text: ')' },
      { type: LEXEMES.OR, text: '||' },
      { type: LEXEMES.LEFT_PAREN, text: '(' },
      { type: LEXEMES.META, text: 'medium' },
      { type: LEXEMES.OPERATOR, text: '!=' },
      { type: LEXEMES.NUMBER, text: '44' },
      { type: LEXEMES.RIGHT_PAREN, text: ')' },
      { type: LEXEMES.RIGHT_PAREN, text: ')' },
      { type: LEXEMES.AND, text: '&&' },
      { type: LEXEMES.LEFT_PAREN, text: '(' },
      { type: LEXEMES.META, text: 'bytes.src' },
      { type: LEXEMES.OPERATOR, text: '>=' },
      { type: LEXEMES.NUMBER, text: '1' },
      { type: LEXEMES.RIGHT_PAREN, text: ')' }
    ];
    const p = new Parser(tokens);
    const result = p.parse();
    assert.strictEqual(result.type, GRAMMAR.AND_EXPRESSION);
    assert.strictEqual(result.left.type, GRAMMAR.GROUP);
    assert.strictEqual(result.left.group.type, GRAMMAR.OR_EXPRESSION);
    assert.strictEqual(result.left.group.left.type, GRAMMAR.GROUP);
    assert.strictEqual(result.left.group.left.group.type, GRAMMAR.CRITERIA);
    assert.deepEqual(result.left.group.left.group.meta, { type: LEXEMES.META, text: 'b' });
    assert.deepEqual(result.left.group.left.group.operator, { type: LEXEMES.OPERATOR, text: '=' });
    assert.deepEqual(result.left.group.left.group.valueRanges[0], {
      type: GRAMMAR.META_VALUE,
      value: {
        type: LEXEMES.STRING,
        text: 'text'
      }
    });
    assert.strictEqual(result.left.group.right.type, GRAMMAR.GROUP);
    assert.strictEqual(result.left.group.right.group.type, GRAMMAR.CRITERIA);
    assert.deepEqual(result.left.group.right.group.meta, { type: LEXEMES.META, text: 'medium' });
    assert.deepEqual(result.left.group.right.group.operator, { type: LEXEMES.OPERATOR, text: '!=' });
    assert.deepEqual(result.left.group.right.group.valueRanges[0], {
      type: GRAMMAR.META_VALUE,
      value: {
        type: LEXEMES.NUMBER,
        text: '44'
      }
    });
    assert.strictEqual(result.right.type, GRAMMAR.GROUP);
    assert.strictEqual(result.right.group.type, GRAMMAR.CRITERIA);
    assert.deepEqual(result.right.group.meta, { type: LEXEMES.META, text: 'bytes.src' });
    assert.deepEqual(result.right.group.operator, { type: LEXEMES.OPERATOR, text: '>=' });
    assert.deepEqual(result.right.group.valueRanges[0], {
      type: GRAMMAR.META_VALUE,
      value: {
        type: LEXEMES.NUMBER,
        text: '1'
      }
    });
  });
});

import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { DEFAULT_LANGUAGES, DEFAULT_ALIASES } from '../../helpers/redux-data-helper';
import Scanner from 'investigate-events/util/scanner';
import Parser from 'investigate-events/util/parser';
import LEXEMES from 'investigate-events/constants/lexemes';
import GRAMMAR from 'investigate-events/constants/grammar';

module('Unit | Util | Parser', function(hooks) {
  setupTest(hooks);

  test('correctly parses a basic meta operator value set', function(assert) {
    const tokens = [
      { type: LEXEMES.META, text: 'medium' },
      { type: LEXEMES.OPERATOR_EQ, text: '=' },
      { type: LEXEMES.INTEGER, text: '3' }
    ];
    const p = new Parser(tokens, DEFAULT_LANGUAGES, DEFAULT_ALIASES);
    const result = p.parse();
    assert.strictEqual(result.type, GRAMMAR.WHERE_CRITERIA, 'Top level is where criteria');
    assert.deepEqual(result.children, [
      {
        type: GRAMMAR.CRITERIA,
        meta: { type: LEXEMES.META, text: 'medium' },
        operator: { type: LEXEMES.OPERATOR_EQ, text: '=' },
        valueRanges: [
          {
            type: GRAMMAR.META_VALUE,
            value: { type: LEXEMES.INTEGER, text: '3' }
          }
        ]
      }
    ], 'children contains the expected single criteria with correct values');
  });

  test('correctly parses NOT', function(assert) {
    const tokens = [
      { type: LEXEMES.NOT, text: 'NOT' },
      { type: LEXEMES.META, text: 'medium' },
      { type: LEXEMES.OPERATOR_EQ, text: '=' },
      { type: LEXEMES.INTEGER, text: '3' }
    ];
    const p = new Parser(tokens, DEFAULT_LANGUAGES, DEFAULT_ALIASES);
    const result = p.parse();
    assert.strictEqual(result.type, GRAMMAR.WHERE_CRITERIA, 'Top level is WHERE_CRITERIA');
    assert.deepEqual(result.children, [
      {
        type: GRAMMAR.NOT
      },
      {
        type: GRAMMAR.CRITERIA,
        meta: { type: LEXEMES.META, text: 'medium' },
        operator: { type: LEXEMES.OPERATOR_EQ, text: '=' },
        valueRanges: [
          {
            type: GRAMMAR.META_VALUE,
            value: { type: LEXEMES.INTEGER, text: '3' }
          }
        ]
      }
    ]);
  });

  test('correctly parses a negative number', function(assert) {
    const tokens = [
      { type: LEXEMES.META, text: 'medium' },
      { type: LEXEMES.OPERATOR_EQ, text: '=' },
      { type: LEXEMES.HYPHEN, text: '-' },
      { type: LEXEMES.INTEGER, text: '3' }
    ];
    const p = new Parser(tokens, DEFAULT_LANGUAGES, DEFAULT_ALIASES);
    const result = p.parse();
    assert.strictEqual(result.type, GRAMMAR.WHERE_CRITERIA, 'Top level is where criteria');
    const [ criteria ] = result.children;
    assert.strictEqual(criteria.isInvalid, true, 'should be invalid');
    assert.strictEqual(criteria.validationError.string, 'Negative values are not allowed.', 'validation error should be correct');
    assert.strictEqual(criteria.type, GRAMMAR.CRITERIA, 'type should be correct');
    assert.deepEqual(criteria.meta, { type: LEXEMES.META, text: 'medium' });
    assert.deepEqual(criteria.operator, { type: LEXEMES.OPERATOR_EQ, text: '=' });
    assert.deepEqual(criteria.valueRanges, [{
      type: GRAMMAR.META_VALUE,
      value: { type: LEXEMES.INTEGER, text: '-3' }
    }]);
  });

  test('correctly parses two meta and AND', function(assert) {
    const tokens = [
      { type: LEXEMES.META, text: 'medium' },
      { type: LEXEMES.OPERATOR_EQ, text: '=' },
      { type: LEXEMES.INTEGER, text: '3' },
      { type: LEXEMES.AND, text: 'AND' },
      { type: LEXEMES.META, text: 'filename' },
      { type: LEXEMES.OPERATOR_NOT_EQ, text: '!=' },
      { type: LEXEMES.STRING, text: 'hyberfile.sys' }
    ];
    const p = new Parser(tokens, DEFAULT_LANGUAGES, DEFAULT_ALIASES);
    const result = p.parse();
    assert.strictEqual(result.type, GRAMMAR.WHERE_CRITERIA, 'Top level is where criteria');
    assert.deepEqual(result.children, [
      {
        type: GRAMMAR.CRITERIA,
        meta: { type: LEXEMES.META, text: 'medium' },
        operator: { type: LEXEMES.OPERATOR_EQ, text: '=' },
        valueRanges: [
          {
            type: GRAMMAR.META_VALUE,
            value: { type: LEXEMES.INTEGER, text: '3' }
          }
        ]
      },
      { type: LEXEMES.AND, text: 'AND' },
      {
        type: GRAMMAR.CRITERIA,
        meta: { type: LEXEMES.META, text: 'filename' },
        operator: { type: LEXEMES.OPERATOR_NOT_EQ, text: '!=' },
        valueRanges: [
          {
            type: GRAMMAR.META_VALUE,
            value: { type: LEXEMES.STRING, text: 'hyberfile.sys' }
          }
        ]
      }
    ], 'children contains the expected two criteria with correct values, separated by a LEXEMES.AND');
  });

  test('correctly parses ranges', function(assert) {
    // medium = 3 - 5
    const tokens = [
      { type: LEXEMES.META, text: 'medium' },
      { type: LEXEMES.OPERATOR_EQ, text: '=' },
      { type: LEXEMES.INTEGER, text: '3' },
      { type: LEXEMES.HYPHEN, text: '-' },
      { type: LEXEMES.INTEGER, text: '5' }
    ];
    const p = new Parser(tokens, DEFAULT_LANGUAGES, DEFAULT_ALIASES);
    const result = p.parse();
    assert.strictEqual(result.type, GRAMMAR.WHERE_CRITERIA, 'Top level is where criteria');
    assert.deepEqual(result.children, [
      {
        type: GRAMMAR.CRITERIA,
        meta: { type: LEXEMES.META, text: 'medium' },
        operator: { type: LEXEMES.OPERATOR_EQ, text: '=' },
        valueRanges: [
          {
            type: GRAMMAR.META_VALUE_RANGE,
            from: { type: LEXEMES.INTEGER, text: '3' },
            to: { type: LEXEMES.INTEGER, text: '5' }
          }
        ]
      }
    ], 'children contains the expected single criteria with correct values');
  });

  // Support for this is currently commented out in the parser until UI support is added.
  // This test can be used once those blocks are un-commented and UI support exists.
  test('correctly parses multiple values/ranges', function(assert) {
    // medium = 3,5-7,9
    const tokens = [
      { type: LEXEMES.META, text: 'medium' },
      { type: LEXEMES.OPERATOR_EQ, text: '=' },
      { type: LEXEMES.INTEGER, text: '3' },
      { type: LEXEMES.VALUE_SEPARATOR, text: ',' },
      { type: LEXEMES.INTEGER, text: '5' },
      { type: LEXEMES.HYPHEN, text: '-' },
      { type: LEXEMES.INTEGER, text: '7' },
      { type: LEXEMES.VALUE_SEPARATOR, text: ',' },
      { type: LEXEMES.INTEGER, text: '9' }
    ];
    const p = new Parser(tokens, DEFAULT_LANGUAGES, DEFAULT_ALIASES);
    const result = p.parse();
    assert.strictEqual(result.type, GRAMMAR.WHERE_CRITERIA, 'Top level is where criteria');
    assert.deepEqual(result.children, [
      {
        type: GRAMMAR.CRITERIA,
        meta: { type: LEXEMES.META, text: 'medium' },
        operator: { type: LEXEMES.OPERATOR_EQ, text: '=' },
        valueRanges: [
          {
            type: GRAMMAR.META_VALUE,
            value: { type: LEXEMES.INTEGER, text: '3' }
          },
          {
            type: GRAMMAR.META_VALUE_RANGE,
            from: { type: LEXEMES.INTEGER, text: '5' },
            to: { type: LEXEMES.INTEGER, text: '7' }
          },
          {
            type: GRAMMAR.META_VALUE,
            value: { type: LEXEMES.INTEGER, text: '9' }
          }
        ]
      }
    ], 'children contains the expected single criteria with correct values');
  });

  test('correctly parses a group (parenthesis)', function(assert) {
    const tokens = [
      { type: LEXEMES.LEFT_PAREN, text: '(' },
      { type: LEXEMES.META, text: 'b' },
      { type: LEXEMES.OPERATOR_EQ, text: '=' },
      { type: LEXEMES.STRING, text: 'netwitness' },
      { type: LEXEMES.RIGHT_PAREN, text: ')' }
    ];
    const p = new Parser(tokens, DEFAULT_LANGUAGES, DEFAULT_ALIASES);
    const result = p.parse();
    assert.strictEqual(result.type, GRAMMAR.WHERE_CRITERIA, 'Top level is where criteria');
    assert.deepEqual(result.children, [
      {
        type: GRAMMAR.GROUP,
        group: {
          type: GRAMMAR.WHERE_CRITERIA,
          children: [
            {
              type: GRAMMAR.CRITERIA,
              meta: { type: LEXEMES.META, text: 'b' },
              operator: { type: LEXEMES.OPERATOR_EQ, text: '=' },
              valueRanges: [
                {
                  type: GRAMMAR.META_VALUE,
                  value: { type: LEXEMES.STRING, text: 'netwitness' }
                }
              ]
            }
          ]
        }
      }
    ]);
  });

  test('throws an error for a meta without value when operator requires one (string)', function(assert) {
    const tokens = [
      { type: LEXEMES.META, text: 'b' },
      { type: LEXEMES.OPERATOR_EQ, text: '=' },
      { type: LEXEMES.META, text: 'medium' }
    ];
    const p = new Parser(tokens, DEFAULT_LANGUAGES, DEFAULT_ALIASES);
    const result = p.parse();
    assert.ok(result.children[0].isInvalid);
    assert.strictEqual(result.children[0].validationError.string, 'Strings must be quoted with "');
    assert.deepEqual(result.children[0].meta, { type: LEXEMES.META, text: 'b' });
    assert.deepEqual(result.children[0].operator, { type: LEXEMES.OPERATOR_EQ, text: '=' });
  });

  test('throws an error for a meta without value when operator requires one (number)', function(assert) {
    const tokens = [
      { type: LEXEMES.META, text: 'medium' },
      { type: LEXEMES.OPERATOR_EQ, text: '=' },
      { type: LEXEMES.META, text: 'b' }
    ];
    const p = new Parser(tokens, DEFAULT_LANGUAGES, DEFAULT_ALIASES);
    const result = p.parse();
    assert.ok(result.children[0].isInvalid);
    assert.strictEqual(result.children[0].validationError.string, 'You must enter an 8-bit Integer.');
    assert.deepEqual(result.children[0].meta, { type: LEXEMES.META, text: 'medium' });
    assert.deepEqual(result.children[0].operator, { type: LEXEMES.OPERATOR_EQ, text: '=' });
  });

  test('does not throw an error for a unary operator without a value', function(assert) {
    const tokens = [
      { type: LEXEMES.META, text: 'medium' },
      { type: LEXEMES.OPERATOR_EXISTS, text: 'exists' }
    ];
    const p = new Parser(tokens, DEFAULT_LANGUAGES, DEFAULT_ALIASES);
    const result = p.parse();
    assert.strictEqual(result.type, GRAMMAR.WHERE_CRITERIA);
    assert.deepEqual(result.children, [
      {
        type: GRAMMAR.CRITERIA,
        meta: { type: LEXEMES.META, text: 'medium' },
        operator: { type: LEXEMES.OPERATOR_EXISTS, text: 'exists' }
      }
    ]);
  });

  test('correctly parses multiple complex filters', function(assert) {
    // ((b = "text") OR (medium != 44)) AND (bytes.src = 1)
    const tokens = [
      { type: LEXEMES.LEFT_PAREN, text: '(' },
      { type: LEXEMES.LEFT_PAREN, text: '(' },
      { type: LEXEMES.META, text: 'b' },
      { type: LEXEMES.OPERATOR_EQ, text: '=' },
      { type: LEXEMES.STRING, text: 'text' },
      { type: LEXEMES.RIGHT_PAREN, text: ')' },
      { type: LEXEMES.OR, text: 'OR' },
      { type: LEXEMES.LEFT_PAREN, text: '(' },
      { type: LEXEMES.META, text: 'medium' },
      { type: LEXEMES.OPERATOR_NOT_EQ, text: '!=' },
      { type: LEXEMES.INTEGER, text: '44' },
      { type: LEXEMES.RIGHT_PAREN, text: ')' },
      { type: LEXEMES.RIGHT_PAREN, text: ')' },
      { type: LEXEMES.AND, text: 'AND' },
      { type: LEXEMES.LEFT_PAREN, text: '(' },
      { type: LEXEMES.META, text: 'bytes.src' },
      { type: LEXEMES.OPERATOR_EQ, text: '=' },
      { type: LEXEMES.INTEGER, text: '1' },
      { type: LEXEMES.RIGHT_PAREN, text: ')' }
    ];
    const p = new Parser(tokens, DEFAULT_LANGUAGES, DEFAULT_ALIASES);
    const result = p.parse();
    assert.strictEqual(result.type, GRAMMAR.WHERE_CRITERIA);
    assert.deepEqual(result.children, [
      {
        type: GRAMMAR.GROUP,
        group: {
          type: GRAMMAR.WHERE_CRITERIA,
          children: [
            {
              type: GRAMMAR.GROUP,
              group: {
                type: GRAMMAR.WHERE_CRITERIA,
                children: [
                  {
                    type: GRAMMAR.CRITERIA,
                    meta: { type: LEXEMES.META, text: 'b' },
                    operator: { type: LEXEMES.OPERATOR_EQ, text: '=' },
                    valueRanges: [
                      {
                        type: GRAMMAR.META_VALUE,
                        value: { type: LEXEMES.STRING, text: 'text' }
                      }
                    ]
                  }
                ]
              }
            },
            { type: LEXEMES.OR, text: 'OR' },
            {
              type: GRAMMAR.GROUP,
              group: {
                type: GRAMMAR.WHERE_CRITERIA,
                children: [
                  {
                    type: GRAMMAR.CRITERIA,
                    meta: { type: LEXEMES.META, text: 'medium' },
                    operator: { type: LEXEMES.OPERATOR_NOT_EQ, text: '!=' },
                    valueRanges: [
                      {
                        type: GRAMMAR.META_VALUE,
                        value: { type: LEXEMES.INTEGER, text: '44' }
                      }
                    ]
                  }
                ]
              }
            }
          ]
        }
      },
      { type: LEXEMES.AND, text: 'AND' },
      {
        type: GRAMMAR.GROUP,
        group: {
          type: GRAMMAR.WHERE_CRITERIA,
          children: [
            {
              type: GRAMMAR.CRITERIA,
              meta: { type: LEXEMES.META, text: 'bytes.src' },
              operator: { type: LEXEMES.OPERATOR_EQ, text: '=' },
              valueRanges: [
                {
                  type: GRAMMAR.META_VALUE,
                  value: { type: LEXEMES.INTEGER, text: '1' }
                }
              ]
            }
          ]
        }
      }
    ]);
  });

  test('transformToString turns a basic meta back into a string', function(assert) {
    const source = 'medium = 3';
    const s = new Scanner(source);
    const p = new Parser(s.scanTokens(), DEFAULT_LANGUAGES);
    const tree = p.parse();
    assert.strictEqual(Parser.transformToString(tree), source);
  });

  test('transformToString handles groups', function(assert) {
    const source = '(medium = 3)';
    const s = new Scanner(source);
    const p = new Parser(s.scanTokens(), DEFAULT_LANGUAGES);
    const tree = p.parse();
    assert.strictEqual(Parser.transformToString(tree), source);
  });

  test('transformToString handles unary operators', function(assert) {
    const source = 'medium exists';
    const s = new Scanner(source);
    const p = new Parser(s.scanTokens(), DEFAULT_LANGUAGES);
    const tree = p.parse();
    assert.strictEqual(Parser.transformToString(tree), source);
  });

  test('transformToString handles strings', function(assert) {
    const source = "b = 'a string'";
    const s = new Scanner(source);
    const p = new Parser(s.scanTokens(), DEFAULT_LANGUAGES);
    const tree = p.parse();
    assert.strictEqual(Parser.transformToString(tree), source);
  });

  test('transformToString handles logical operators', function(assert) {
    const source = 'medium exists AND medium = 3';
    const s = new Scanner(source);
    const p = new Parser(s.scanTokens(), DEFAULT_LANGUAGES);
    const tree = p.parse();
    assert.strictEqual(Parser.transformToString(tree), source);
  });

  test('transformToString handles ranges and value separators', function(assert) {
    const source = 'medium = 3,5-7,9';
    const s = new Scanner(source);
    const p = new Parser(s.scanTokens(), DEFAULT_LANGUAGES);
    const tree = p.parse();
    assert.strictEqual(Parser.transformToString(tree), source);
  });

  test('transformToString handles logical operators and groups', function(assert) {
    const source = 'medium exists OR (alias.ip = 127.0.0.1) AND medium = 3';
    const s = new Scanner(source);
    const p = new Parser(s.scanTokens(), DEFAULT_LANGUAGES);
    const tree = p.parse();
    assert.strictEqual(Parser.transformToString(tree), source);
  });

  test('transformToString handles NOT', function(assert) {
    const source = 'NOT(medium = 2)';
    const s = new Scanner(source);
    const p = new Parser(s.scanTokens(), DEFAULT_LANGUAGES);
    const tree = p.parse();
    assert.strictEqual(Parser.transformToString(tree), source);
  });
});

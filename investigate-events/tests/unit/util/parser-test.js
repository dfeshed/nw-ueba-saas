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
    assert.strictEqual(result.type, GRAMMAR.WHERE_CRITERIA, 'Top level is where criteria');
    assert.deepEqual(result.children, [
      {
        type: GRAMMAR.CRITERIA,
        meta: { type: LEXEMES.META, text: 'medium' },
        operator: { type: LEXEMES.OPERATOR, text: '=' },
        valueRanges: [
          {
            type: GRAMMAR.META_VALUE,
            value: { type: LEXEMES.NUMBER, text: '3' }
          }
        ]
      }
    ], 'children contains the expected single criteria with correct values');
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
    assert.strictEqual(result.type, GRAMMAR.WHERE_CRITERIA, 'Top level is where criteria');
    assert.deepEqual(result.children, [
      {
        type: GRAMMAR.CRITERIA,
        meta: { type: LEXEMES.META, text: 'medium' },
        operator: { type: LEXEMES.OPERATOR, text: '=' },
        valueRanges: [
          {
            type: GRAMMAR.META_VALUE,
            value: { type: LEXEMES.NUMBER, text: '3' }
          }
        ]
      },
      { type: LEXEMES.AND, text: '&&' },
      {
        type: GRAMMAR.CRITERIA,
        meta: { type: LEXEMES.META, text: 'filename' },
        operator: { type: LEXEMES.OPERATOR, text: '!=' },
        valueRanges: [
          {
            type: GRAMMAR.META_VALUE,
            value: { type: LEXEMES.STRING, text: 'hyberfile.sys' }
          }
        ]
      }
    ], 'children contains the expected two criteria with correct values, separated by a LEXEMES.AND');
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
              operator: { type: LEXEMES.OPERATOR, text: '=' },
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
    assert.strictEqual(result.type, GRAMMAR.WHERE_CRITERIA);
    assert.deepEqual(result.children, [
      {
        type: GRAMMAR.CRITERIA,
        meta: { type: LEXEMES.META, text: 'medium' },
        operator: { type: LEXEMES.OPERATOR, text: 'exists' }
      }
    ]);
  });

  test('correctly parses multiple complex filters', function(assert) {
    // ((b = "text") || (medium != 44)) && (bytes.src >= 1)
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
                    operator: { type: LEXEMES.OPERATOR, text: '=' },
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
            { type: LEXEMES.OR, text: '||' },
            {
              type: GRAMMAR.GROUP,
              group: {
                type: GRAMMAR.WHERE_CRITERIA,
                children: [
                  {
                    type: GRAMMAR.CRITERIA,
                    meta: { type: LEXEMES.META, text: 'medium' },
                    operator: { type: LEXEMES.OPERATOR, text: '!=' },
                    valueRanges: [
                      {
                        type: GRAMMAR.META_VALUE,
                        value: { type: LEXEMES.NUMBER, text: '44' }
                      }
                    ]
                  }
                ]
              }
            }
          ]
        }
      },
      { type: LEXEMES.AND, text: '&&' },
      {
        type: GRAMMAR.GROUP,
        group: {
          type: GRAMMAR.WHERE_CRITERIA,
          children: [
            {
              type: GRAMMAR.CRITERIA,
              meta: { type: LEXEMES.META, text: 'bytes.src' },
              operator: { type: LEXEMES.OPERATOR, text: '>=' },
              valueRanges: [
                {
                  type: GRAMMAR.META_VALUE,
                  value: { type: LEXEMES.NUMBER, text: '1' }
                }
              ]
            }
          ]
        }
      }
    ]);
  });
});

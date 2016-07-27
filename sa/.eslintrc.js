module.exports = {
  plugins: [
    'ember-suave' // using custom suave rules, so importing plugin, will be more later
  ],
  extends: [
    require.resolve('ember-cli-eslint/coding-standard/ember-application.js')
  ],
  rules: {
    // SUAVE CUSTOM RULES
    // 'ember-suave/prefer-destructuring': 'error',  // not available yet
    'ember-suave/no-direct-property-access': 'error',
    'ember-suave/require-access-in-comments': 'error',
    'ember-suave/require-const-for-ember-properties': 'error',

    // BASIC ESLINT RULES
    // STARTED WITH SUAVE, BUT MOVED AWAY
    'radix': ['error', 'always'],
    'no-empty': 'error',
    'brace-style': ['error', '1tbs', {
      'allowSingleLine': false
    }],
    'no-multiple-empty-lines': 'error',
    'one-var': ['error', {
      'uninitialized': 'always',
      'initialized': 'never'
    }],
    'operator-linebreak': ['error', 'after'],
    'key-spacing': ['error', {
      'beforeColon': false,
      'afterColon': true
    }],
    'space-unary-ops': ['error', {
      'words': false,
      'nonwords': false
    }],
    'semi-spacing': ['error', {
      'before': false,
      'after': true
    }],
    'space-before-function-paren': ['error', 'never'],
    'space-in-parens': ['error', 'never'],
    'no-spaced-func': 'error',
    'comma-dangle': ['error', 'never'],
    'no-trailing-spaces': 'error',
    'no-var': 'error',
    'camelcase': ['error', {
      'properties': 'never'
    }],
    'new-cap': 'error',
    'comma-style': ['error', 'last'],
    'curly': ['error', 'all'],
    'dot-notation': 'error',
    'object-shorthand': 'error',
    'arrow-parens': 'error',
    'semi': ['error', 'always'],
    'space-infix-ops': 'error',
    'keyword-spacing': 'error',
    'spaced-comment': ['error', 'always'],
    'space-before-blocks': ['error', 'always'],
    'prefer-spread': 'error',
    'prefer-template': 'error',
    'indent': ['error', 2, {
      'SwitchCase': 1
    }],
    'quotes': ['error', 'single', {
      'avoidEscape': true
    }]
  }
};
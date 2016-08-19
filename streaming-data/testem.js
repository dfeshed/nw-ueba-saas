module.exports = {
  'framework': 'qunit',
  'test_page': 'tests/index.html?hidepassed&coverage',
  'disable_watching': true,
  'launch_in_ci': [
    'PhantomJS'
  ],
  'launch_in_dev': [
    'PhantomJS',
    'Chrome'
  ]
};
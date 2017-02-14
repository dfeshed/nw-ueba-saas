/**
 * @description creates mock API route for /eula/rsa.
 * @public
 */

export default function(config) {
  config.get('/eula/rsa', function() {
    return '<h1>Mock EULA Response</h1><p>Just click accept.</p>';
  });
}

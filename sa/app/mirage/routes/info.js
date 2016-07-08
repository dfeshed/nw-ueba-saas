/**
 * @description creates mock API route for /api/devices.
 * @public
 */

export default function(config) {
  config.get('/info', function() {
    return { 'version': '10.6.0.0-SNAPSHOT','commit': 28,'changeset': 'f716b11','date': 1435711785000 };
  });
}
